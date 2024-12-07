package com.example.personalaccount

import com.example.common.dto.api.Pagination
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.database.FriendshipEntity
import com.example.personalaccount.database.FriendshipRepository
import com.example.personalaccount.exceptions.AddFriendException
import com.example.personalaccount.exceptions.RemoveFriendException
import com.example.personalaccount.model.FriendshipStatus
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.PersonalAccountManagerImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl

class PersonalAccountManagerImplTest {
    private lateinit var accountRepository: AccountRepository
    private lateinit var friendshipRepository: FriendshipRepository
    private lateinit var accountService: AccountService
    private lateinit var personalAccountManager: PersonalAccountManagerImpl
    private lateinit var user: AccountEntity
    private lateinit var friend: AccountEntity

    @BeforeEach
    fun setUp() {
        accountRepository = mock(AccountRepository::class.java)
        friendshipRepository = mock(FriendshipRepository::class.java)
        accountService = mock(AccountService::class.java)

        personalAccountManager = PersonalAccountManagerImpl(
            accountRepository,
            friendshipRepository,
            accountService
        )
        user = AccountEntity(id = 1L, name = "Alice", fines = 0, "avatar", mutableSetOf(), mutableSetOf(), 1)

        friend = AccountEntity(id = 2L, name = "Bob", fines = 0, "avatar", mutableSetOf(), mutableSetOf(), 1)
    }

    @Test
    fun `should throw AddFriendException when adding oneself as a friend`() {
        assertThrows<AddFriendException> {
            personalAccountManager.addFriend(1L, 1L)
        }
    }

    @Test
    fun `should accept incoming friend request`() {
        val incomingRequest =
            FriendshipEntity(fromAccount = friend, toAccount = user, status = FriendshipStatus.PENDING)
        user.incomingFriendRequests.add(incomingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)
        `when`(friendshipRepository.save(any())).thenReturn(incomingRequest)

        personalAccountManager.addFriend(user.id, friend.id)

        assertEquals(FriendshipStatus.ACCEPTED, incomingRequest.status)
        verify(friendshipRepository).save(incomingRequest)
        verify(friendshipRepository).save(
            FriendshipEntity(
                fromAccount = user,
                toAccount = friend,
                status = FriendshipStatus.ACCEPTED
            )
        )
    }

    @Test
    fun `should throw exception if already friends`() {
        val incomingRequest =
            FriendshipEntity(fromAccount = friend, toAccount = user, status = FriendshipStatus.ACCEPTED)
        user.incomingFriendRequests.add(incomingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)
        `when`(friendshipRepository.save(any())).thenReturn(incomingRequest)

        assertThrows<AddFriendException> {
            personalAccountManager.addFriend(user.id, friend.id)
        }.also {
            assertEquals("Already friends", it.message)
        }
    }

    @Test
    fun `should handle rejected incoming request`() {
        val incomingRequest =
            FriendshipEntity(fromAccount = friend, toAccount = user, status = FriendshipStatus.REJECTED)
        user.incomingFriendRequests.add(incomingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        personalAccountManager.addFriend(user.id, friend.id)

        assertFalse(user.incomingFriendRequests.contains(incomingRequest))
        verify(friendshipRepository).delete(incomingRequest)
        verify(friendshipRepository).save(
            FriendshipEntity(
                fromAccount = user,
                toAccount = friend,
                status = FriendshipStatus.PENDING
            )
        )
    }


    @Test
    fun `should throw exception if outgoing request is pending`() {
        val outgoingRequest =
            FriendshipEntity(fromAccount = user, toAccount = friend, status = FriendshipStatus.PENDING)
        user.friends.add(outgoingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        assertThrows<AddFriendException> {
            personalAccountManager.addFriend(user.id, friend.id)
        }.also {
            assertEquals("Friend request already sent!", it.message)
        }
    }

    @Test
    fun `should throw exception if friendship is rejected`() {
        val outgoingRequest =
            FriendshipEntity(fromAccount = user, toAccount = friend, status = FriendshipStatus.REJECTED)
        user.friends.add(outgoingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        assertThrows<AddFriendException> {
            personalAccountManager.addFriend(user.id, friend.id)
        }.also {
            assertEquals("Friend request is rejected!", it.message)
        }
    }

    @Test
    fun `should create friendship if no requests`() {
        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        personalAccountManager.addFriend(user.id, friend.id)

        verify(friendshipRepository).save(
            FriendshipEntity(
                fromAccount = user,
                toAccount = friend,
                status = FriendshipStatus.PENDING
            )
        )
    }


    @Test
    fun `should reject incoming friend request`() {
        val incomingRequest =
            FriendshipEntity(fromAccount = friend, toAccount = user, status = FriendshipStatus.PENDING)
        user.incomingFriendRequests.add(incomingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        personalAccountManager.removeFriend(user.id, friend.id)

        assertEquals(FriendshipStatus.REJECTED, incomingRequest.status)
        verify(friendshipRepository).save(incomingRequest)
    }

    @Test
    fun `should throw exception when trying to remove already rejected request`() {
        val incomingRequest =
            FriendshipEntity(fromAccount = friend, toAccount = user, status = FriendshipStatus.REJECTED)
        user.incomingFriendRequests.add(incomingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        assertThrows<RemoveFriendException> {
            personalAccountManager.removeFriend(user.id, friend.id)
        }.also {
            assertEquals("Request already rejected!", it.message)
        }
    }

    @Test
    fun `should remove accepted friendship`() {
        val outgoingRequest =
            FriendshipEntity(fromAccount = user, toAccount = friend, status = FriendshipStatus.ACCEPTED)
        val incomingRequest =
            FriendshipEntity(fromAccount = friend, toAccount = user, status = FriendshipStatus.ACCEPTED)
        user.friends.add(outgoingRequest)
        user.incomingFriendRequests.add(incomingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        personalAccountManager.removeFriend(user.id, friend.id)

        assertFalse(user.friends.contains(outgoingRequest))
        assertFalse(friend.incomingFriendRequests.contains(outgoingRequest))
        verify(accountRepository).saveAll(listOf(user, friend))
    }

    @Test
    fun `should remove pending outgoing request`() {
        val outgoingRequest =
            FriendshipEntity(fromAccount = user, toAccount = friend, status = FriendshipStatus.PENDING)
        user.friends.add(outgoingRequest)
        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)
        personalAccountManager.removeFriend(user.id, friend.id)
        assertFalse(user.friends.contains(outgoingRequest))
    }

    @Test
    fun `should throw exception when trying to remove already rejected outgoing request`() {
        val outgoingRequest =
            FriendshipEntity(fromAccount = user, toAccount = friend, status = FriendshipStatus.REJECTED)
        user.friends.add(outgoingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        assertThrows<RemoveFriendException> {
            personalAccountManager.removeFriend(user.id, friend.id)
        }.also {
            assertEquals("Cannot delete already rejected request", it.message)
        }
    }

    @Test
    fun `should throw exception when trying to remove accepted outgoing request without incoming request`() {
        val outgoingRequest =
            FriendshipEntity(fromAccount = user, toAccount = friend, status = FriendshipStatus.ACCEPTED)
        user.friends.add(outgoingRequest)

        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)

        assertThrows<RemoveFriendException> {
            personalAccountManager.removeFriend(user.id, friend.id)
        }.also {
            assertEquals("Something went wrong", it.message)
        }
    }

    @Test
    fun `should throw exception when friendship not found`() {
        `when`(accountService.findByIdOrThrow(user.id)).thenReturn(user)
        `when`(accountService.findByIdOrThrow(friend.id)).thenReturn(friend)
        assertThrows<RemoveFriendException> {
            personalAccountManager.removeFriend(user.id, friend.id)
        }.also {
            assertEquals("Friendship not found", it.message)
        }
    }


    @Test
    fun `getAllFriends returns friends list for valid accountId`() {
        val accountId = 1L
        val pagination = Pagination(0, 10)
        val mockAccount = AccountEntity(id = 1L, name = "Alice", fines = 0, "avatar", mutableSetOf(), mutableSetOf(), 1)
        val mockAccount2 = AccountEntity(id = 2L, name = "Bob", fines = 0, "avatar", mutableSetOf(), mutableSetOf(), 1)
        val friendships = listOf(
            FriendshipEntity(fromAccount = mockAccount2, toAccount = mockAccount, status = FriendshipStatus.ACCEPTED),
        )

        `when`(accountService.findByIdOrThrow(accountId)).thenReturn(mockAccount)
        `when`(
            friendshipRepository.findAllByToAccountAndStatusIn(
                mockAccount,
                listOf(FriendshipStatus.PENDING, FriendshipStatus.ACCEPTED),
                pagination.toPageable()
            )
        )
            .thenReturn(PageImpl(friendships))

        val result = personalAccountManager.getAllFriends(accountId, pagination)

        assert(result.content.size == 1)
        assert(result.content[0].status == FriendshipStatus.ACCEPTED)
    }

    @Test
    fun `getAllFriends throws exception for invalid accountId`() {
        val accountId = 999L
        val pagination = Pagination(0, 10)

        `when`(accountService.findByIdOrThrow(accountId)).thenThrow(RuntimeException("Account not found"))

        assertThrows<RuntimeException> {
            personalAccountManager.getAllFriends(accountId, pagination)
        }
    }

    @Test
    fun `addFine increases fines and sends fine notification`() {
        val accountId = 1L
        val mockAccount = AccountEntity(id = 1, name = "Alice", fines = 0, "avatar", mutableSetOf(), mutableSetOf(), 1)

        `when`(accountService.findByIdOrThrow(accountId)).thenReturn(mockAccount)
        `when`(accountRepository.save(mockAccount)).thenReturn(mockAccount)

        personalAccountManager.addFine(accountId)

        assert(mockAccount.fines == 1)
    }

    @Test
    fun `addFine throws exception for invalid accountId`() {
        val accountId = 999L

        `when`(accountService.findByIdOrThrow(accountId)).thenThrow(RuntimeException("Account not found"))

        assertThrows<RuntimeException> {
            personalAccountManager.addFine(accountId)
        }
    }

}