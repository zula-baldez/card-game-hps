package com.example.personalaccount.service

import com.example.common.dto.api.Pagination
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.database.FriendshipEntity
import com.example.personalaccount.database.FriendshipRepository
import com.example.personalaccount.exceptions.AddFriendException
import com.example.personalaccount.exceptions.RemoveFriendException
import com.example.personalaccount.model.FriendshipDto
import com.example.personalaccount.model.FriendshipStatus
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class PersonalAccountManagerImpl(
    val accountRepository: AccountRepository,
    val friendshipRepository: FriendshipRepository,
    val accountService: AccountService
    //val simpMessagingTemplate: SimpMessagingTemplate,
) : PersonalAccountManager {

    /**
     * Sends or accepts a friendship request
     */
    @Transactional
    override fun addFriend(accountId: Long, friendId: Long) {
        require(accountId != friendId) { throw AddFriendException("Cannot add yourself as friend") }
        val user = accountService.findByIdOrThrow(accountId)
        val friend = accountService.findByIdOrThrow(friendId)
        val incomingRequest = user.incomingFriendRequests.find { it.fromAccount == friend }
        val outgoingRequest = user.friends.find { it.toAccount == friend }

        if (incomingRequest != null) {
            when (incomingRequest.status) {
                FriendshipStatus.PENDING -> {
                    // Accept incoming request
                    incomingRequest.status = FriendshipStatus.ACCEPTED
                    val friendship = FriendshipEntity(
                        fromAccount = user,
                        toAccount = friend,
                        status = FriendshipStatus.ACCEPTED
                    )
                    friendshipRepository.save(incomingRequest)
                    friendshipRepository.save(friendship)
                }

                FriendshipStatus.REJECTED -> {
                    // I rejected the request, but now I changed my mind
                    // and want to send my own request
                    user.incomingFriendRequests.remove(incomingRequest)
                    friend.friends.remove(incomingRequest)
                    accountRepository.saveAll(listOf(user, friend))
                    friendshipRepository.delete(incomingRequest)
                    friendshipRepository.save(
                        FriendshipEntity(
                            fromAccount = user,
                            toAccount = friend,
                            status = FriendshipStatus.PENDING
                        )
                    )
                }

                FriendshipStatus.ACCEPTED ->
                    throw AddFriendException("Already friends")
            }
        } else if (outgoingRequest != null) {
            when (outgoingRequest.status) {
                FriendshipStatus.PENDING -> {
                    throw AddFriendException("Friend request already sent!")
                }

                FriendshipStatus.REJECTED -> {
                    throw AddFriendException("Friend request is rejected!")
                }

                FriendshipStatus.ACCEPTED -> {
                    // this part will never be reached
                    throw AddFriendException("Already friends!")
                }
            }
        } else {
            val friendship = FriendshipEntity(
                fromAccount = user,
                toAccount = friend,
                status = FriendshipStatus.PENDING
            )
            friendshipRepository.save(friendship)
        }
    }

    /**
     * Deletes a friend or denies a friendship request
     */
    @Transactional
    override fun removeFriend(accountId: Long, friendId: Long) {
        val user = accountService.findByIdOrThrow(accountId)
        val friend = accountService.findByIdOrThrow(friendId)
        val incomingRequest = user.incomingFriendRequests.find { it.fromAccount == friend }
        val outgoingRequest = user.friends.find { it.toAccount == friend }

        if (incomingRequest != null) {
            when (incomingRequest.status) {
                FriendshipStatus.PENDING -> {
                    incomingRequest.status = FriendshipStatus.REJECTED
                    friendshipRepository.save(incomingRequest)
                }

                FriendshipStatus.REJECTED -> {
                    throw RemoveFriendException("Request already rejected!")
                }

                FriendshipStatus.ACCEPTED -> {
                    incomingRequest.status = FriendshipStatus.REJECTED
                    friendshipRepository.save(incomingRequest)

                    user.friends.remove(outgoingRequest)
                    friend.incomingFriendRequests.remove(outgoingRequest)
                    friendshipRepository.delete(outgoingRequest!!)
                    accountRepository.saveAll(listOf(user, friend))
                }
            }
        } else if (outgoingRequest != null) {
            when (outgoingRequest.status) {
                FriendshipStatus.PENDING -> {
                    friendshipRepository.delete(outgoingRequest)
                    user.friends.remove(outgoingRequest)
                    friend.incomingFriendRequests.remove(outgoingRequest)
                    accountRepository.saveAll(listOf(user, friend))
                }

                FriendshipStatus.REJECTED ->
                    // Avoid spam requests
                    throw RemoveFriendException("Cannot delete already rejected request")

                FriendshipStatus.ACCEPTED ->
                    // incomingRequest should not be null if already friends
                    throw RemoveFriendException("Something went wrong")
            }
        } else {
            throw RemoveFriendException("Friendship not found")
        }
    }

    override fun getAllFriends(accountId: Long, pagination: Pagination): Page<FriendshipDto> {
        val account = accountService.findByIdOrThrow(accountId)

        return friendshipRepository.findAllByToAccountAndStatusIn(
            account,
            listOf(
                FriendshipStatus.PENDING,
                FriendshipStatus.ACCEPTED
            ),
            pagination.toPageable()
        ).map { it.toDto() }
    }
//
//    private fun sendAddFine(id: Long) {
//        simpMessagingTemplate.convertAndSend("/topic/fines", FineDTO(id))
//    }

    override fun addFine(accountId: Long) {
        val account = accountService.findByIdOrThrow(accountId)
        account.fines++
        accountRepository.save(account)
        //sendAddFine(accountId)
    }
}