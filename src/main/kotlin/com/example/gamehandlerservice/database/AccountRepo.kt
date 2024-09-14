package com.example.gamehandlerservice.database

import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepo : JpaRepository<Account, Long>