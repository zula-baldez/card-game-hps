package com.example.roomservice

import com.example.roomservice.repository.AccountInRoomEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EntitiesInitializationTest {

    @Test
    fun `test AccountInRoomEntity initialization`() {
        val accountInRoom = AccountInRoomEntity(accountId = 1L, roomId = 101L)

        assertEquals(1L, accountInRoom.accountId)
        assertEquals(101L, accountInRoom.roomId)
        assertEquals(false, accountInRoom.isNewAccount)
        assertEquals(1L, accountInRoom.id)
        assertEquals(false, accountInRoom.isNew)
    }
}