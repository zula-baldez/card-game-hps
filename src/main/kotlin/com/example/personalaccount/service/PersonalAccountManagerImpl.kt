package com.example.personalaccount.service

import com.example.gamehandlerservice.model.dto.FineDTO
import com.example.personalaccount.database.Account
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.exceptions.AddFriendException
import com.example.personalaccount.exceptions.DeleteFriendException
import com.example.personalaccount.exceptions.FriendNotFoundException
import com.example.personalaccount.model.FriendshipStatus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.util.*


@Component
class PersonalAccountManagerImpl(
    val accountRepository: AccountRepository,
    val simpMessagingTemplate: SimpMessagingTemplate
) : PersonalAccountManager {

    override fun addFriend(userId: Long, friendId: Long): FriendshipStatus {
        val userOpt: Optional<Account> = accountRepository.findById(userId)
        val friendOpt: Optional<Account> = accountRepository.findById(friendId)

        if (userOpt.isPresent && friendOpt.isPresent) {
            val user = userOpt.get()
            val friend = friendOpt.get()
            user.friends.add(friend)
            friend.friends.add(user)
            accountRepository.save(user)
            accountRepository.save(friend)
            return FriendshipStatus.ALLOWED
        }
        throw AddFriendException("Failed to add friendship")
    }

    override fun removeFriend(userId: Long, friendId: Long): FriendshipStatus {
        val userOpt: Optional<Account> = accountRepository.findById(userId)
        val friendOpt: Optional<Account> = accountRepository.findById(friendId)

        if (userOpt.isPresent && friendOpt.isPresent) {
            val user = userOpt.get()
            val friend = friendOpt.get()
            user.friends.remove(friend)
            friend.friends.remove(user)
            accountRepository.save(user)
            accountRepository.save(friend)
            return FriendshipStatus.DENIED
        }
        throw DeleteFriendException("Failed to delete friendship")
    }


    override fun getAllFriends(userId: Long): Optional<MutableSet<Account>>? {
        val friends = accountRepository.findById(userId)
            .map { it.friends }
        if (friends.get().isEmpty()) {
            throw FriendNotFoundException("Friends not found for user with ID: $userId")

        }
        return friends

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendAddFine(id: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            simpMessagingTemplate.convertAndSend("/topic/fines", FineDTO(id))
        }
    }

    override fun addFine(id: Long) {
        accountRepository.findById(id).ifPresent { account ->
            account.fines++
            sendAddFine(id)
        }
    }

    override fun getInRoomAccounts(): List<Account> = accountRepository.findAll().toList()
}