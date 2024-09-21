package com.example.personalaccount.service

import com.example.gamehandlerservice.model.dto.FineDTO
import com.example.personalaccount.database.Account
import com.example.personalaccount.database.AccountRepo
import com.example.personalaccount.exceptions.FriendNotFoundException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.util.*


@Component
class PersonalAccountManagerImpl(
    val accountRepo: AccountRepo,
    val simpMessagingTemplate: SimpMessagingTemplate
) : PersonalAccountManager {

    override fun addFriend(userId: Long, friendId: Long) {
        val userOpt: Optional<Account> = accountRepo.findById(userId)
        val friendOpt: Optional<Account> = accountRepo.findById(friendId)

        if (userOpt.isPresent && friendOpt.isPresent) {
            val user = userOpt.get()
            val friend = friendOpt.get()
            user.friends.plus(friend)
            friend.friends.plus(user)
            accountRepo.save(user)
            accountRepo.save(friend)
        }
    }

    override fun removeFriend(userId: Long, friendId: Long) {
        val userOpt: Optional<Account> = accountRepo.findById(userId)
        val friendOpt: Optional<Account> = accountRepo.findById(friendId)

        if (userOpt.isPresent && friendOpt.isPresent) {
            val user = userOpt.get()
            val friend = friendOpt.get()
            user.friends.minus(friend)
            friend.friends.minus(user)
            accountRepo.save(user)
            accountRepo.save(friend)
        }
    }


    override fun getAllFriends(userId: Long): Set<Account>? {
        val friends = accountRepo.findById(userId)
            .map { it.friends }
            .orElse(emptySet())
        if (friends.isEmpty()) {
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
        accountRepo.findById(id).ifPresent { account ->
            account.fines++
            sendAddFine(id)
        }
    }

    override fun getInRoomAccounts(): List<Account> = accountRepo.findAll().toList()
}