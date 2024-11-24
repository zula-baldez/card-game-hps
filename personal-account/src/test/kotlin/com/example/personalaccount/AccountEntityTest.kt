package com.example.personalaccount

import com.example.personalaccount.database.AccountEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AccountEntityTest {

    @Test
    fun `test DTO initialization`() {
        val accountEntity = AccountEntity(
            id = 1,
            name = "John Doe",
            fines = 100,
            avatar = "avatar_url",
            currentRoomId = 10
        )

        val accountDto = accountEntity.toDto()

        assertEquals(accountEntity.id, accountDto.id)
        assertEquals(accountEntity.name, accountDto.name)
        assertEquals(accountEntity.fines, accountDto.fines)
        assertEquals(accountEntity.avatar, accountDto.avatar)
        assertEquals(accountEntity.currentRoomId, accountDto.roomId)
    }

    @Test
    fun `test equals method`() {
        val account1 = AccountEntity(id = 1, name = "Alice", fines = 0, avatar = "url1", currentRoomId = 1)
        val account2 = AccountEntity(id = 1, name = "Bob", fines = 0, avatar = "url2", currentRoomId = 2)
        val account3 = AccountEntity(id = 2, name = "Charlie", fines = 0, avatar = "url3", currentRoomId = 3)

        assertTrue(account1.equals(account2))

        assertFalse(account1.equals(account3))
    }


}