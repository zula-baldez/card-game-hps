package com.example.roomservice.repository

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table(name = "account_room")
class AccountInRoomEntity(
    @Id
    val accountId: Long,
    val roomId: Long,
    @Transient
    @Value("false")
    val isNewAccount: Boolean = false
): Persistable<Long> {
    override fun getId(): Long {
        return accountId
    }

    override fun isNew(): Boolean {
        return isNewAccount
    }
}