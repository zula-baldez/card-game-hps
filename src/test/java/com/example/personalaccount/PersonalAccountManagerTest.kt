package com.example.personalaccount

import com.example.personalaccount.database.Account
import com.example.personalaccount.database.AccountRepo
import com.example.personalaccount.exceptions.AddFriendException
import com.example.personalaccount.exceptions.DeleteFriendException
import com.example.personalaccount.exceptions.FriendNotFoundException
import com.example.personalaccount.model.FriendshipStatus
import com.example.personalaccount.service.PersonalAccountManagerImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.Optional

internal class PersonalAccountManagerImplTest {

    private var accountRepo: AccountRepo = mock(AccountRepo::class.java)
    private var simpMessagingTemplate: SimpMessagingTemplate = mock(SimpMessagingTemplate::class.java)
    private var personalAccountManagerImpl: PersonalAccountManagerImpl =
        PersonalAccountManagerImpl(accountRepo, simpMessagingTemplate)
    private val userId = 1L
    private val friendId = 2L
    private lateinit var user: Account
    private lateinit var friend: Account

    @BeforeEach
    fun setUp() {
        user = Account(
            name = "User1",
            fines = 0,
            active = true,
            additionalCards = 1,
            id = userId
        )
        friend = Account(
            name = "User2",
            fines = 0,
            active = true,
            additionalCards = 1,
            id = friendId
        )
        accountRepo = mock(AccountRepo::class.java)
        personalAccountManagerImpl = PersonalAccountManagerImpl(accountRepo, simpMessagingTemplate)
    }

    @Test
    fun `should allow adding a friend when both users exist`() {
        doReturn(Optional.of(user)).`when`(accountRepo).findById(userId)
        doReturn(Optional.of(friend)).`when`(accountRepo).findById(friendId)

        val status = personalAccountManagerImpl.addFriend(userId, friendId)

        assertEquals(status, FriendshipStatus.ALLOWED)
        assertTrue(user.friends.contains(friend))
        verify(accountRepo).save(user)
    }

    @Test
    fun `should throw exception when user does not exist`() {
        `when`(accountRepo.findById(userId)).thenReturn(Optional.empty())
        `when`(accountRepo.findById(friendId)).thenReturn(Optional.of(friend))

        val exception = assertThrows<AddFriendException> {
            personalAccountManagerImpl.addFriend(userId, friendId)
        }
        assertEquals("Failed to add friendship", exception.message)
        verify(accountRepo, never()).save(any())
    }

    @Test
    fun `should throw exception when friend does not exist`() {
        `when`(accountRepo.findById(userId)).thenReturn(Optional.of(user))
        `when`(accountRepo.findById(friendId)).thenReturn(Optional.empty())

        val exception = assertThrows<AddFriendException> {
            personalAccountManagerImpl.addFriend(userId, friendId)
        }
        assertEquals("Failed to add friendship", exception.message)
        verify(accountRepo, never()).save(any())
    }

    @Test
    fun `should throw exception when both users do not exist`() {
        `when`(accountRepo.findById(userId)).thenReturn(Optional.empty())
        `when`(accountRepo.findById(friendId)).thenReturn(Optional.empty())

        val exception = assertThrows<AddFriendException> {
            personalAccountManagerImpl.addFriend(userId, friendId)
        }
        assertEquals("Failed to add friendship", exception.message)
        verify(accountRepo, never()).save(any())
    }

    @Test
    fun `should remove friend successfully`() {
        user.friends.add(friend)
        friend.friends.add(user)

        `when`(accountRepo.findById(userId)).thenReturn(Optional.of(user))
        `when`(accountRepo.findById(friendId)).thenReturn(Optional.of(friend))

        val status = personalAccountManagerImpl.removeFriend(userId, friendId)

        assertEquals(FriendshipStatus.DENIED, status)
        assertFalse(user.friends.contains(friend))
        assertFalse(friend.friends.contains(user))
        verify(accountRepo).save(user)
        verify(accountRepo).save(friend)
    }

    @Test
    fun `should throw DeleteFriendException for non-existing user`() {
        `when`(accountRepo.findById(userId)).thenReturn(Optional.empty())

        val exception = assertThrows(DeleteFriendException::class.java) {
            personalAccountManagerImpl.removeFriend(userId, friendId)
        }

        assertTrue(exception.message!!.contains("Failed to delete friendship"))
    }

    @Test
    fun `should return all friends for user`() {
        `when`(accountRepo.findById(userId)).thenReturn(Optional.of(user))
        `when`(accountRepo.findById(friendId)).thenReturn(Optional.of(friend))
        personalAccountManagerImpl.addFriend(userId, friendId)
        val friends = personalAccountManagerImpl.getAllFriends(userId)

        if (friends != null) {
            assertTrue(friends.isPresent)
        }
        if (friends != null) {
            assertEquals(1, friends.get().size)
        }
    }

    @Test
    fun `should throw FriendNotFoundException for user with no friends`() {
        `when`(accountRepo.findById(userId)).thenReturn(Optional.of(user))
        val exception = assertThrows(FriendNotFoundException::class.java) {
            personalAccountManagerImpl.getAllFriends(userId)
        }

        assertTrue(exception.message!!.contains("Friends not found for user with ID: $userId"))
    }

    @Test
    fun `should add fine successfully to an account`() {
        user.fines = 0
        `when`(accountRepo.findById(userId)).thenReturn(Optional.of(user))
        personalAccountManagerImpl.addFine(userId)
        assertEquals(1, user.fines)
    }

    @Test
    fun `should return list of accounts from the repository`() {
        val mockAccounts = listOf(user, friend)

        `when`(accountRepo.findAll()).thenReturn(mockAccounts)

        val result = personalAccountManagerImpl.getInRoomAccounts()

        assertEquals(mockAccounts, result)
        verify(accountRepo, times(1)).findAll()
    }

    @Test
    fun `should return an empty list when no accounts found`() {
        val mockAccounts: List<Account> = emptyList()

        `when`(accountRepo.findAll()).thenReturn(mockAccounts)
        val result = personalAccountManagerImpl.getInRoomAccounts()

        assertEquals(mockAccounts, result)
        verify(accountRepo, times(1)).findAll()
    }
    @Test
    fun testAccountIdInitialization() {
        val accountId = 1L
        val account = Account(name = "John Doe", fines = 0, additionalCards = 1, id = accountId)
        assertNotNull(account.id)
        assertEquals(accountId, account.id)
    }
}