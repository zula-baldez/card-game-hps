package com.example.personalaccount.database

import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepo : JpaRepository<Account, Long>