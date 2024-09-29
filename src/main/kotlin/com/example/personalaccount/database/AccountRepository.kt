package com.example.personalaccount.database

import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.service.AccountService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<AccountEntity, Long>