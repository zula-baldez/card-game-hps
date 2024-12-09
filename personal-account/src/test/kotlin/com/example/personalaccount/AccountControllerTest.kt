package com.example.personalaccount

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.controllers.AccountController
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.exceptions.InvalidAvatarFileException
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.AvatarsHandler
import com.example.personalaccount.service.PersonalAccountManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AccountControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var accountService: AccountService
    private lateinit var personalAccountManager: PersonalAccountManager
    private lateinit var accountController: AccountController
    private lateinit var hazelcastInstance: HazelcastInstance
    private lateinit var avatarsHandler: AvatarsHandler
    private lateinit var objectMapper: ObjectMapper

    @Mock
    private lateinit var map:  IMap<Long, AccountDto>


    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        accountService = mock()
        personalAccountManager = mock()
        avatarsHandler = mock()
        hazelcastInstance = mock()
        objectMapper = mock()
        accountController = AccountController(accountService, personalAccountManager, avatarsHandler, hazelcastInstance,objectMapper)
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build()
    }

    @Test
    fun `should add fine to account`() {
        val accountId = 1L

        whenever(hazelcastInstance.getMap<Long, AccountDto>("map")).thenReturn(map)

        accountController.addFine(accountId)

        verify(personalAccountManager).addFine(accountId)

    }

    @Test
    fun `should return NOT_FOUND status when account not found`() {
        val accountId = 999L
        whenever(accountService.findByIdOrThrow(accountId)).thenThrow(AccountNotFoundException(accountId))
        whenever(hazelcastInstance.getMap<Long, AccountDto>("map")).thenReturn(map)

        val exception = assertThrows<AccountNotFoundException> {
            accountController.getAccountById(accountId)
        }
        assert(exception.message == "Account with id 999 not found")
    }

    @Test
    fun `should return 400 when invalid avatar file`() {
        val accountId = 999L
        whenever(accountService.findByIdOrThrow(accountId)).thenThrow(InvalidAvatarFileException("Invalid file"))
        whenever(hazelcastInstance.getMap<Long, AccountDto>("map")).thenReturn(map)

        val exception = assertThrows<InvalidAvatarFileException> {
            accountController.getAccountById(accountId)
        }
        assert(exception.message == "Invalid file")
    }

    @Test
    fun `should get account by id`() {
        val accountDto = AccountDto(id = 1, name = "testUser", fines = 0, avatar = "avatar", roomId = 1)
        `when`(accountService.findByIdOrThrow(1L)).thenReturn(toEntity(accountDto))
        whenever(hazelcastInstance.getMap<Long, AccountDto>("map")).thenReturn(map)

        val result = accountController.getAccountById(1L)
        assertEquals(1, result.id)
        assertEquals("testUser", result.name)
        assertEquals(0, result.fines)
        assertEquals("avatar", result.avatar)
        assertEquals(1, result.roomId)
    }

    @Test
    fun `should create account`() {
        val createAccountDto = CreateAccountDto(id = 1, username = "newUser")
        val accountDto = AccountDto(id = 1, name = "testUser", fines = 0, avatar = "avatar", roomId = 1)
        `when`(accountService.createAccountForUser(createAccountDto)).thenReturn(toEntity(accountDto))
        whenever(hazelcastInstance.getMap<Long, AccountDto>("map")).thenReturn(map)

        val result = accountController.createAccount(createAccountDto)
        assertEquals(1, result.id)
        assertEquals("testUser", result.name)
        assertEquals(0, result.fines)
        assertEquals("avatar", result.avatar)
        assertEquals(1, result.roomId)
    }

    @Test
    fun `should update account room`() {
        val updateRequest = UpdateAccountRoomRequest(roomId = 3)
        val accountDto = AccountDto(id = 1, name = "testUser", fines = 0, avatar = "avatar", roomId = 3)
        `when`(accountService.updateAccountRoom(1L, 3)).thenReturn(toEntity(accountDto))
        whenever(hazelcastInstance.getMap<Long, AccountDto>("map")).thenReturn(map)

        val result = accountController.updateAccountRoom(1L, updateRequest)
        assertEquals(1, result.id)
        assertEquals("testUser", result.name)
        assertEquals(0, result.fines)
        assertEquals("avatar", result.avatar)
        assertEquals(3, result.roomId)
    }

    @Test
    fun `should update account avatar`() {
        val mockFile = MockMultipartFile("file", "avatar.png", "image/png", ByteArray(10))
        whenever(hazelcastInstance.getMap<Long, AccountDto>("map")).thenReturn(map)
        accountController.updateAccountAvatar(1L, mockFile)
        verify(avatarsHandler).handleFile(1L, mockFile)
    }

    @Test
    fun `handleAccountNotFoundException should return 404`() {
        val exception = AccountNotFoundException(1L)
        val response = accountController.handleAccountNotFound(exception)
        assertEquals("Account with id 1 not found", response)
    }

    @Test
    fun `handleInvalidAvatarFileException should return bad request response`() {
        val exception = InvalidAvatarFileException("avatar")
        val response = accountController.handleInvalidAvatarFile(exception)
        assertEquals("avatar", response)
    }



    private fun toEntity(accountDto: AccountDto): AccountEntity {
        return AccountEntity(
            id = accountDto.id,
            name = accountDto.name,
            fines = accountDto.fines,
            avatar = accountDto.avatar,
            currentRoomId = accountDto.roomId,

            friends = mutableSetOf(),
            incomingFriendRequests = mutableSetOf()
        )
    }
}