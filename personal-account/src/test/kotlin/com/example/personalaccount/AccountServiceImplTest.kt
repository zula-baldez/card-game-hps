package com.example.personalaccount

import com.example.common.dto.Avatar
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.service.AccountServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import java.util.*

class AccountServiceImplTest {

    private val accountRepository: AccountRepository = Mockito.mock(AccountRepository::class.java)
    private val accountService = AccountServiceImpl(accountRepository)

    @Test
    fun `should return account when found by id`() {
        val accountId = 1L
        val expectedAccount = AccountEntity(id = 1, name = "Alice", fines = 0, "avatar",mutableSetOf(), mutableSetOf(), 1)

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount))
        val result = accountService.findByIdOrThrow(accountId)

        assertEquals(expectedAccount, result)
    }

    @Test
    fun `should return existing account when available`() {
        val userEntity = CreateAccountDto(
            1,
            "testUser"
        )
        val existingAccount = AccountEntity(id = 1, name = "Alice", fines = 0, "avatar",mutableSetOf(), mutableSetOf(), 1)

        whenever(accountRepository.findById(1)) doReturn Optional.of(existingAccount)
        val result = accountService.createAccountForUser(userEntity)
        assertEquals(result, existingAccount)
        verify(accountRepository, never()).save(any())
    }

    @Test
    fun `findByIdOrThrow should return account if found`() {
        val accountId = 1L
        val account = AccountEntity(id = accountId, name = "Test User", fines = 0, avatar = "", currentRoomId = null)

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.ofNullable(account))

        val result = accountService.findByIdOrThrow(accountId)

        assertEquals(account, result)
    }

    @Test
    fun `findByIdOrThrow should throw AccountNotFoundException if account not found`() {
        val accountId = 1L

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.empty())

        val exception = assertThrows<AccountNotFoundException> {
            accountService.findByIdOrThrow(accountId)
        }

        assertEquals(accountId, exception.accountId)
    }

    @Test
    fun `createAccountForUser should create and return account if not exists`() {
        val createAccountDto = CreateAccountDto(id = 2L, username = "New User")

        `when`(accountRepository.findById(createAccountDto.id)).thenReturn(Optional.empty())
        val newAccount = AccountEntity(id = createAccountDto.id, name = "New User", fines = 0, avatar = "", currentRoomId = null)

        `when`(accountRepository.save(any())).thenReturn(newAccount)

        val result = accountService.createAccountForUser(createAccountDto)

        assertEquals(newAccount, result)
        verify(accountRepository).save(any())
    }

    @Test
    fun `createAccountForUser should return existing account if exists`() {
        val createAccountDto = CreateAccountDto(id = 3L, username = "Existing User")
        val existingAccount = AccountEntity(id = createAccountDto.id, name = "Existing User", fines = 0, avatar = "", currentRoomId = null)

        `when`(accountRepository.findById(createAccountDto.id)).thenReturn(Optional.of(existingAccount))

        val result = accountService.createAccountForUser(createAccountDto)

        assertEquals(existingAccount, result)
        verify(accountRepository, never()).save(any())
    }

    @Test
    fun `updateAccountRoom should update and return account if exists`() {
        val userId = 4L
        val roomId = 5L
        val account = AccountEntity(id = userId, name = "User", fines = 0, avatar = "", currentRoomId = null)

        `when`(accountRepository.findById(userId)).thenReturn(Optional.of(account))
        `when`(accountRepository.save(any())).thenReturn(account)

        val result = accountService.updateAccountRoom(userId, roomId)

        assertEquals(roomId, result.currentRoomId)
        verify(accountRepository).save(account)
    }

    @Test
    fun `updateAccountRoom should throw AccountNotFoundException if account not found`() {
        val userId = 6L

        `when`(accountRepository.findById(userId)).thenReturn(Optional.empty())

        val exception = assertThrows<AccountNotFoundException> {
            accountService.updateAccountRoom(userId, null)
        }

        assertEquals(userId, exception.accountId)
    }

    @Test
    fun `updateAccountAvatar should update and return account if exists`() {
        val avatar = Avatar(accountId = 7L, url = "http://example.com/avatar.png")
        val account = AccountEntity(id = avatar.accountId, name = "User", fines = 0, avatar = "", currentRoomId = null)

        `when`(accountRepository.findById(avatar.accountId)).thenReturn(Optional.of(account))
        `when`(accountRepository.save(any())).thenReturn(account)

        val result = accountService.updateAccountAvatar(avatar)

        assertEquals(avatar.url, result.avatar)
        verify(accountRepository).save(account)
    }

    @Test
    fun `updateAccountAvatar should throw AccountNotFoundException if account not found`() {
        val avatar = Avatar(accountId = 8L, url = "http://example.com/avatar.png")

        `when`(accountRepository.findById(avatar.accountId)).thenReturn(Optional.empty())

        val exception = assertThrows<AccountNotFoundException> {
            accountService.updateAccountAvatar(avatar)
        }

        assertEquals(avatar.accountId, exception.accountId)
    }

}



