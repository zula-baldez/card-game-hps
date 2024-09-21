package com.example.personalaccount.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepo : JpaRepository<Account, Long>