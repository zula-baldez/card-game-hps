package com.example.roomservice.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "banned_accounts")
class BannedAccountInRoomEntity(
    @Id
    var id: Long,
    var accountId: Long,
    var roomId: Long
)