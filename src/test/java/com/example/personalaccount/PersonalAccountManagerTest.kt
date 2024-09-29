package com.example.personalaccount

import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.exceptions.AddFriendException
import com.example.personalaccount.exceptions.RemoveFriendException
import com.example.personalaccount.exceptions.FriendNotFoundException
import com.example.personalaccount.model.FriendshipStatus
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.PersonalAccountManagerImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.any
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.Optional

internal class PersonalAccountManagerImplTest {

//    private var accountService: AccountService = mock(AccountService::class.java)
//    private var simpMessagingTemplate: SimpMessagingTemplate = mock(SimpMessagingTemplate::class.java)
//    private var personalAccountManagerImpl: PersonalAccountManagerImpl =
//        PersonalAccountManagerImpl(accountService, simpMessagingTemplate)
//    private val userId = 1L
//    private val friendId = 2L
//    private lateinit var user: AccountEntity
//    private lateinit var friend: AccountEntity
//
//    @BeforeEach
//    fun setUp() {
//        user = AccountEntity(
//            name = "User1",
//            fines = 0,
//            id = userId
//        )
//        friend = AccountEntity(
//            name = "User2",
//            fines = 0,
//            id = friendId
//        )
//        accountService = mock(AccountService::class.java)
//        personalAccountManagerImpl = PersonalAccountManagerImpl(accountService, simpMessagingTemplate)
//    }
//
//    @Test
//    fun `should allow adding a friend when both users exist`() {
//        doReturn(Optional.of(user)).`when`(accountService).findByIdOrThrow(userId)
//        doReturn(Optional.of(friend)).`when`(accountService).findByIdOrThrow(friendId)
//
//        personalAccountManagerImpl.addFriend(userId, friendId)
//
//        assertTrue(user.friends.contains(friend))
//        verify(accountRepository).save(user)
//    }
//
//    @Test
//    fun `should throw exception when user does not exist`() {
//        `when`(accountService.findByIdOrThrow(userId)).thenThrow(AccountNotFoundException(userId))
//        `when`(accountService.findByIdOrThrow(friendId)).thenReturn(friend)
//
//        val exception = assertThrows<AddFriendException> {
//            personalAccountManagerImpl.addFriend(userId, friendId)
//        }
//        assertEquals("Failed to add friendship", exception.message)
//    }
//
//    @Test
//    fun `should throw exception when friend does not exist`() {
//        `when`(accountService.findByIdOrThrow(userId)).thenReturn(user)
//        `when`(accountService.findByIdOrThrow(friendId)).thenThrow(AccountNotFoundException(friendId))
//
//        val exception = assertThrows<AddFriendException> {
//            personalAccountManagerImpl.addFriend(userId, friendId)
//        }
//        assertEquals("Failed to add friendship", exception.message)
//    }
//
//    @Test
//    fun `should throw exception when both users do not exist`() {
//        `when`(accountService.findByIdOrThrow(userId)).thenReturn(AccountNotFoundException(userId))
//        `when`(accountService.findByIdOrThrow(friendId)).thenReturn(AccountNotFoundException(userId))
//
//        val exception = assertThrows<AddFriendException> {
//            personalAccountManagerImpl.addFriend(userId, friendId)
//        }
//        assertEquals("Failed to add friendship", exception.message)
//    }
//
//    @Test
//    fun `should remove friend successfully`() {
//        user.friends.add(friend)
//        friend.friends.add(user)
//
//        `when`(accountRepository.findById(userId)).thenReturn(Optional.of(user))
//        `when`(accountRepository.findById(friendId)).thenReturn(Optional.of(friend))
//
//        val status = personalAccountManagerImpl.removeFriend(userId, friendId)
//
//        assertFalse(user.friends.contains(friend))
//        assertFalse(friend.friends.contains(user))
//        verify(accountRepository).save(user)
//        verify(accountRepository).save(friend)
//    }
//
//    @Test
//    fun `should throw DeleteFriendException for non-existing user`() {
//        `when`(accountRepository.findById(userId)).thenReturn(Optional.empty())
//
//        val exception = assertThrows(RemoveFriendException::class.java) {
//            personalAccountManagerImpl.removeFriend(userId, friendId)
//        }
//
//        assertTrue(exception.message!!.contains("Failed to delete friendship"))
//    }
//
//    @Test
//    fun `should return all friends for user`() {
//        `when`(accountRepository.findById(userId)).thenReturn(Optional.of(user))
//        `when`(accountRepository.findById(friendId)).thenReturn(Optional.of(friend))
//        personalAccountManagerImpl.addFriend(userId, friendId)
//        val friends = personalAccountManagerImpl.getAllFriends(userId)
//
//        if (friends != null) {
//            assertTrue(friends.isPresent)
//        }
//        if (friends != null) {
//            assertEquals(1, friends.get().size)
//        }
//    }
//
//    @Test
//    fun `should throw FriendNotFoundException for user with no friends`() {
//        `when`(accountRepository.findById(userId)).thenReturn(Optional.of(user))
//        val exception = assertThrows(FriendNotFoundException::class.java) {
//            personalAccountManagerImpl.getAllFriends(userId)
//        }
//
//        assertTrue(exception.message!!.contains("Friends not found for user with ID: $userId"))
//    }
//
//    @Test
//    fun `should add fine successfully to an account`() {
//        user.fines = 0
//        `when`(accountRepository.findById(userId)).thenReturn(Optional.of(user))
//        personalAccountManagerImpl.addFine(userId)
//        assertEquals(1, user.fines)
//    }
//
//    @Test
//    fun `should return list of accounts from the repository`() {
//        val mockAccounts = listOf(user, friend)
//
//        `when`(accountRepository.findAll()).thenReturn(mockAccounts)
//
//        val result = personalAccountManagerImpl.getInRoomAccounts()
//
//        assertEquals(mockAccounts, result)
//        verify(accountRepository, times(1)).findAll()
//    }
//
//    @Test
//    fun `should return an empty list when no accounts found`() {
//        val mockAccountEntities: List<AccountEntity> = emptyList()
//
//        `when`(accountRepository.findAll()).thenReturn(mockAccountEntities)
//        val result = personalAccountManagerImpl.getInRoomAccounts()
//
//        assertEquals(mockAccountEntities, result)
//        verify(accountRepository, times(1)).findAll()
//    }
//
//    @Test
//    fun testAccountIdInitialization() {
//        val accountId = 1L
//        val accountEntity = AccountEntity(name = "John Doe", fines = 0, id = accountId)
//        assertNotNull(accountEntity.id)
//        assertEquals(accountId, accountEntity.id)
//        assertEquals("John Doe", accountEntity.name)
//    }
//
//    @Test
//    fun testToDtoConvert(){
//        val accountEntity = AccountEntity(name = "John Doe", fines = 0, id = 1L)
//        val dto = accountEntity.toDto()
//        assertEquals(accountEntity.id, dto.id)
//        assertEquals(accountEntity.name, dto.name)
//        assertEquals(accountEntity.fines, dto.fines)
//    }
}