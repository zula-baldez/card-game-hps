package com.example.personalaccount

import com.example.personalaccount.controllers.FriendsController
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.exceptions.FriendNotFoundException
import com.example.personalaccount.model.AddFriendRequest
import com.example.personalaccount.service.PersonalAccountManagerImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*


internal class FriendsControllerTest {

    private lateinit var friendsManagerImpl: PersonalAccountManagerImpl
    private lateinit var friendsController: FriendsController
    private lateinit var principal: Principal
    private val userId = 1L
    private val friendId = 2L
    private lateinit var user: AccountEntity
    private lateinit var friend: AccountEntity

    @BeforeEach
    fun setUp() {
        user = AccountEntity(
            name = "User1",
            fines = 0,
            active = true,
            id = userId
        )
        friend = AccountEntity(
            name = "User2",
            fines = 0,
            active = true,
            id = friendId
        )
        friendsManagerImpl = mock(PersonalAccountManagerImpl::class.java)
        friendsController = FriendsController(friendsManagerImpl)
        principal = mock(Principal::class.java)
    }

    @Test
    fun testGetFriends() {
        val friends = mutableSetOf(friend)

        `when`(principal.name).thenReturn(userId.toString())
        `when`(friendsManagerImpl.getAllFriends(userId)).thenReturn(Optional.of(friends))

        val result = friendsController.getFriends(principal)
        verify(friendsManagerImpl).getAllFriends(userId)
        assertEquals(Optional.of(friends), result)
    }

    @Test
    fun testAllowFriendship() {
        `when`(principal.name).thenReturn(userId.toString())
        val addFriendRequest = AddFriendRequest(friendId)
        val response = friendsController.allowFriendship(addFriendRequest, principal)
        verify(friendsManagerImpl).addFriend(userId, friendId)
        assertEquals(ResponseEntity.ok("Friendship with user ID $friendId has been allowed."), response)
    }

    @Test
    fun testDenyFriendship() {
        `when`(principal.name).thenReturn(userId.toString())
        val response = friendsController.denyFriendship(friendId, principal)

        verify(friendsManagerImpl).removeFriend(userId, friendId)
        assertEquals(ResponseEntity.ok("Friendship with user ID $friendId has been denied."), response)
    }

    @Test
    fun testHandleUnauthorized() {
        val exception = ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access")
        val response = friendsController.handleUnauthorized(exception)
        assertEquals("401 UNAUTHORIZED \"Unauthorized access\"", response)
    }

    @Test
    fun testHandleFriendNotFound() {
        val exception = FriendNotFoundException("Friend not found")
        val response = friendsController.handleFriendNotFound(exception)
        assertEquals("Friend not found", response)
    }

}