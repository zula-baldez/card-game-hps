package com.example.roomservice

import com.example.common.dto.roomservice.RoomDto
import com.example.roomservice.security.RoomSecurityUtils
import com.example.roomservice.service.RoomManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono

class RoomSecurityUtilsTest {

    private val roomManager: RoomManager = mock(RoomManager::class.java)
    private val roomSecurityUtils = RoomSecurityUtils(roomManager)

    @Test
    fun `should return false if no SCOPE_USER authority`() {
        val authentication = mock(Authentication::class.java)
        `when`(authentication.authorities).thenReturn(emptyList())

        val result = roomSecurityUtils.canRemoveAccount(1L, authentication, 2L).block()

        assertEquals(false, result)
    }

    @Test
    fun `should return true if SCOPE_ADMIN authority is present`() {
        val authentication = mock(Authentication::class.java)

        val adminAuthority: GrantedAuthority = SimpleGrantedAuthority("SCOPE_ADMIN")
        val otherAuthority: GrantedAuthority = SimpleGrantedAuthority("SCOPE_USER")

        `when`(authentication.authorities).thenReturn(listOf(adminAuthority, otherAuthority))

        val result = roomSecurityUtils.canRemoveAccount(1L, authentication, 2L).block()

        assertEquals(true, result)
    }

    @Test
    fun `should return true if authentication name matches removeAccount`() {
        val authentication = mock(Authentication::class.java)
        val otherAuthority: GrantedAuthority = SimpleGrantedAuthority("SCOPE_USER")

        `when`(authentication.name).thenReturn("2")
        `when`(authentication.authorities).thenReturn(listOf(otherAuthority))

        val result = roomSecurityUtils.canRemoveAccount(1L, authentication, 2L).block()

        assertEquals(true, result)
    }


    @Test
    fun `should return false if no conditions are met`() {
        val authentication = mock(Authentication::class.java)
        `when`(authentication.name).thenReturn("anotherUser")
        `when`(authentication.authorities).thenReturn(listOf(mock(GrantedAuthority::class.java)))

        val room = RoomDto(1L, "hostUser", 5, 2, emptyList(),1L)
        `when`(roomManager.getRoom(1L)).thenReturn(Mono.just(room))

        val result = roomSecurityUtils.canRemoveAccount(1L, authentication, 3L).block()

        assertEquals(false, result)
    }

    @Test
    fun `should return false if room is not found`() {
        val authentication = mock(Authentication::class.java)
        `when`(authentication.name).thenReturn("hostUser")
        `when`(authentication.authorities).thenReturn(listOf(mock(GrantedAuthority::class.java)))

        `when`(roomManager.getRoom(1L)).thenReturn(Mono.empty())

        val result = roomSecurityUtils.canRemoveAccount(1L, authentication, 3L).block()

        assertEquals(false, result)
    }

    @Test
    fun `should return true if room host equals auth name`() {
        val room = RoomDto(
            id = 1,
            name = "Room1",
            hostId = 1L,
            capacity = 10,
            players = listOf(),
            currentGameId = 1L,
            bannedPlayers = emptyList()
        )
        val authentication = mock(Authentication::class.java)
        val otherAuthority: GrantedAuthority = SimpleGrantedAuthority("SCOPE_USER")
        `when`(authentication.name).thenReturn("1")
        `when`(authentication.authorities).thenReturn(listOf(otherAuthority))
        `when`(roomManager.getRoom(1L)).thenReturn(Mono.just(room))

        val result = roomSecurityUtils.canRemoveAccount(1L, authentication, 2L).block()
        assertEquals(true, result)
    }

}
