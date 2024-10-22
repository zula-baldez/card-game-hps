package com.example.common.exceptions

class AccountNotFoundException(val accountId: Long) : RuntimeException("Account with id $accountId not found")