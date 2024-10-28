package com.example.authservice

import com.example.authservice.database.RoleEntity
import com.example.authservice.database.UserEntity
import com.example.common.util.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EntitiesInitializationTest {
    @Test
    fun `should initialize RoleEntity correctly`() {
        val roleName = Role.ADMIN
        val roleEntity = RoleEntity( 1, Role.ADMIN )

        assertEquals(roleEntity.roleName, roleName)
    }
    @Test
    fun `should initialize UserEntity correctly`() {
        val userName = "TestUser"
        val userPassword = "SecurePassword"
        val userEntity = UserEntity(name = userName, password = userPassword)

        assertEquals(userEntity.name, userName)
        assertEquals(userEntity.password, userPassword)
        assertEquals(userEntity.id, null)
        assertTrue(userEntity.roles.isEmpty())
    }
}