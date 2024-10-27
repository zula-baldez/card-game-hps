package com.example.authservice.database

import com.example.common.util.Role
import org.springframework.data.repository.CrudRepository

interface RoleRepository : CrudRepository<RoleEntity, Long> {
    fun findFirstByRoleName(roleName: Role): RoleEntity
}