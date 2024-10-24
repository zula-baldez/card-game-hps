package com.example.personalaccount.model

import com.example.common.dto.personalaccout.business.AccountDto

data class FriendshipDto(
    val id: Long,
    val fromAccount: AccountDto,
    val toAccount: AccountDto,
    val status: FriendshipStatus
)
