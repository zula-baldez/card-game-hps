package com.example.authservice.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : JpaRepository<UserEntity, Long> {
    fun findByName(name : String) : UserEntity?
}
