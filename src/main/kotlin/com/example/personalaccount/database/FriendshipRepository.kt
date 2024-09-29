package com.example.personalaccount.database

import com.example.personalaccount.model.FriendshipStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRepository : JpaRepository<FriendshipEntity, Long> {
    fun findAllByToAccountAndStatusIn(toAccount: AccountEntity, status: Collection<FriendshipStatus>, pageable: Pageable): List<FriendshipEntity>
}