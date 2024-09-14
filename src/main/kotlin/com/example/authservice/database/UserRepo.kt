package com.example.authservice.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepo : JpaRepository<User, Long> {
    fun findByName(name : String) : User?
}
