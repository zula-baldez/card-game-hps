package com.example.personalaccount

import com.example.common.dto.api.Pagination
import com.example.gamehandlerservice.model.dto.FineDTO
import com.example.personalaccount.controllers.FriendsController
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.database.FriendshipEntity
import com.example.personalaccount.database.FriendshipRepository
import com.example.personalaccount.exceptions.AddFriendException
import com.example.personalaccount.exceptions.RemoveFriendException
import com.example.personalaccount.model.AddFriendRequest
import com.example.personalaccount.model.FriendshipStatus
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.AccountServiceImpl
import com.example.personalaccount.service.PersonalAccountManager
import com.example.personalaccount.service.PersonalAccountManagerImpl
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.security.Principal
import java.util.*


internal class FriendsControllerTest {
    private lateinit var accountRepositoryMock: AccountRepository
    private lateinit var friendshipRepositoryMock: FriendshipRepository
    private lateinit var accountServiceMock: AccountService
    private lateinit var messageTemplateMock: SimpMessagingTemplate
    private lateinit var friendsManagerImpl: PersonalAccountManager
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
            id = userId
        )
        friend = AccountEntity(
            name = "User2",
            fines = 0,
            id = friendId
        )

        accountRepositoryMock = mock<AccountRepository> {
            on { findById(userId) } doReturn Optional.of(user)
            on { findById(friendId) } doReturn Optional.of(friend)
        }
        friendshipRepositoryMock = mock<FriendshipRepository>()
        accountServiceMock = AccountServiceImpl(accountRepositoryMock)
        messageTemplateMock = mock<SimpMessagingTemplate>()
        friendsManagerImpl = PersonalAccountManagerImpl(
            accountRepositoryMock,
            friendshipRepositoryMock,
            accountServiceMock,
            messageTemplateMock
        )
        friendsController = FriendsController(friendsManagerImpl)
        principal = mock<Principal> {
            on { name } doReturn userId.toString()
        }
    }

    @Test
    fun testGetFriends() {
        val servletResponse = mock<HttpServletResponse>()
        val friendshipIncoming = FriendshipEntity(1, friend, user, FriendshipStatus.ACCEPTED)
        val friends = PageImpl(listOf(friendshipIncoming))
        whenever(friendshipRepositoryMock.findAllByToAccountAndStatusIn(eq(user), any(), any())).thenReturn(friends)

        val result = friendsController.getFriends(Pagination(), servletResponse, principal)
        verify(friendshipRepositoryMock).findAllByToAccountAndStatusIn(user, listOf(FriendshipStatus.PENDING, FriendshipStatus.ACCEPTED), Pagination().toPageable())
        verify(servletResponse).setIntHeader("x-total-friends", 1)
        assertEquals(result.size, 1)
        assertEquals(friend.toDto(), result[0].fromAccount)
        assertEquals(user.toDto(), result[0].toAccount)
    }

    @Test
    fun testThrowWhenAlreadyFriends() {
        val friendshipOutgoing = FriendshipEntity(1, user, friend, FriendshipStatus.ACCEPTED)
        val friendshipIncoming = FriendshipEntity(1, friend, user, FriendshipStatus.ACCEPTED)
        val friends = PageImpl(listOf(friendshipOutgoing, friendshipIncoming))
        user.friends.add(friendshipOutgoing)
        user.incomingFriendRequests.add(friendshipIncoming)
        friend.friends.add(friendshipIncoming)
        friend.incomingFriendRequests.add(friendshipOutgoing)
        whenever(friendshipRepositoryMock.findAllByToAccountAndStatusIn(eq(user), any(), any())).thenReturn(friends)

        assertThrows<AddFriendException> {
            friendsController.sendOrAcceptRequest(AddFriendRequest(friendId), principal)
        }

        verifyNoInteractions(friendshipRepositoryMock)
    }

    @Test
    fun testSendRequest() {
        friendsController.sendOrAcceptRequest(AddFriendRequest(friendId), principal)
        verify(friendshipRepositoryMock).save(FriendshipEntity(0, user, friend, FriendshipStatus.PENDING))
    }

    @Test
    fun testAcceptRequest() {
        val friendshipIncoming = FriendshipEntity(1, friend, user, FriendshipStatus.PENDING)
        user.incomingFriendRequests.add(friendshipIncoming)
        friend.friends.add(friendshipIncoming)

        friendsController.sendOrAcceptRequest(AddFriendRequest(friendId), principal)
        assertEquals(FriendshipStatus.ACCEPTED, friendshipIncoming.status)
        verify(friendshipRepositoryMock).save(FriendshipEntity(0, user, friend, FriendshipStatus.ACCEPTED))
    }

    @Test
    fun testSendRequestAfterRejected() {
        val friendshipIncoming = FriendshipEntity(1, friend, user, FriendshipStatus.REJECTED)
        user.incomingFriendRequests.add(friendshipIncoming)
        friend.friends.add(friendshipIncoming)

        friendsController.sendOrAcceptRequest(AddFriendRequest(friendId), principal)
        assertTrue(user.incomingFriendRequests.isEmpty())
        assertTrue(friend.friends.isEmpty())
        verify(friendshipRepositoryMock).delete(friendshipIncoming)
        verify(friendshipRepositoryMock).save(FriendshipEntity(0, user, friend, FriendshipStatus.PENDING))
    }

    @Test
    fun testThrowOnSendDoubleRequest() {
        val friendshipOutgoing = FriendshipEntity(1, user, friend, FriendshipStatus.PENDING)
        user.friends.add(friendshipOutgoing)
        friend.incomingFriendRequests.add(friendshipOutgoing)

        assertThrows<AddFriendException> {
            friendsController.sendOrAcceptRequest(AddFriendRequest(friendId), principal)
        }
        verifyNoInteractions(friendshipRepositoryMock)
    }

    @Test
    fun testThrowOnRejectedRequest() {
        val friendshipOutgoing = FriendshipEntity(1, user, friend, FriendshipStatus.REJECTED)
        user.friends.add(friendshipOutgoing)
        friend.incomingFriendRequests.add(friendshipOutgoing)

        assertThrows<AddFriendException> {
            friendsController.sendOrAcceptRequest(AddFriendRequest(friendId), principal)
        }
        verifyNoInteractions(friendshipRepositoryMock)
    }

    @Test
    fun testThrowOnImpossibleRequest() {
        val friendshipOutgoing = FriendshipEntity(1, user, friend, FriendshipStatus.ACCEPTED)
        user.friends.add(friendshipOutgoing)
        friend.incomingFriendRequests.add(friendshipOutgoing)

        assertThrows<AddFriendException> {
            friendsController.sendOrAcceptRequest(AddFriendRequest(friendId), principal)
        }
        verifyNoInteractions(friendshipRepositoryMock)
    }

    @Test
    fun testThrowWhenFriendingYourself() {
        assertThrows<AddFriendException> {
            friendsController.sendOrAcceptRequest(AddFriendRequest(userId), principal)
        }
    }

    @Test
    fun testDeleteFriendship() {
        val friendshipOutgoing = FriendshipEntity(1, user, friend, FriendshipStatus.ACCEPTED)
        val friendshipIncoming = FriendshipEntity(1, friend, user, FriendshipStatus.ACCEPTED)
        user.friends.add(friendshipOutgoing)
        user.incomingFriendRequests.add(friendshipIncoming)
        friend.friends.add(friendshipIncoming)
        friend.incomingFriendRequests.add(friendshipOutgoing)

        friendsController.removeFriendOrRequest(friendId, principal)

        assertEquals(FriendshipStatus.REJECTED, friendshipIncoming.status)
        verify(friendshipRepositoryMock).delete(friendshipOutgoing)
    }

    @Test
    fun testDeleteRequest() {
        val friendshipOutgoing = FriendshipEntity(1, user, friend, FriendshipStatus.PENDING)
        user.friends.add(friendshipOutgoing)
        friend.incomingFriendRequests.add(friendshipOutgoing)

        friendsController.removeFriendOrRequest(friendId, principal)

        verify(friendshipRepositoryMock).delete(friendshipOutgoing)
        assertTrue(user.friends.isEmpty())
        assertTrue(friend.incomingFriendRequests.isEmpty())
    }

    @Test
    fun testRejectRequest() {
        val friendshipIncoming = FriendshipEntity(1, friend, user, FriendshipStatus.PENDING)
        user.incomingFriendRequests.add(friendshipIncoming)
        friend.friends.add(friendshipIncoming)

        friendsController.removeFriendOrRequest(friendId, principal)

        assertEquals(FriendshipStatus.REJECTED, friendshipIncoming.status)
    }

    @Test
    fun testThrowWhenDoubleReject() {
        val friendshipIncoming = FriendshipEntity(1, friend, user, FriendshipStatus.PENDING)
        user.incomingFriendRequests.add(friendshipIncoming)
        friend.friends.add(friendshipIncoming)

        friendsController.removeFriendOrRequest(friendId, principal)

        assertEquals(FriendshipStatus.REJECTED, friendshipIncoming.status)

        assertThrows<RemoveFriendException> {
            friendsController.removeFriendOrRequest(friendId, principal)
        }
    }

    @Test
    fun testThrowWhenImpossibleReject() {
        val friendshipOutgoing = FriendshipEntity(1, user, friend, FriendshipStatus.ACCEPTED)
        user.friends.add(friendshipOutgoing)
        friend.incomingFriendRequests.add(friendshipOutgoing)

        assertThrows<RemoveFriendException> {
            friendsController.removeFriendOrRequest(friendId, principal)
        }
    }

    @Test
    fun testThrowOnDeleteRejectedRequest() {
        val friendshipOutgoing = FriendshipEntity(1, user, friend, FriendshipStatus.REJECTED)
        user.friends.add(friendshipOutgoing)
        friend.incomingFriendRequests.add(friendshipOutgoing)

        assertThrows<RemoveFriendException> {
            friendsController.removeFriendOrRequest(friendId, principal)
        }
    }

    @Test
    fun testThrowWhenDeleteNonExistingFriendship() {
        assertThrows<RemoveFriendException> {
            friendsController.removeFriendOrRequest(friendId, principal)
        }
    }


    @Test
    fun testAddFine() {
        friendsManagerImpl.addFine(userId)
        assertEquals(1, user.fines)
        verify(messageTemplateMock).convertAndSend(eq("/topic/fines"), FineDTO(userId))
    }

}