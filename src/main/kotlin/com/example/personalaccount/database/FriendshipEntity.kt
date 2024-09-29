package com.example.personalaccount.database

import com.example.personalaccount.model.FriendshipDto
import com.example.personalaccount.model.FriendshipStatus
import jakarta.persistence.*

@Entity
@Table(name = "friendships")
data class FriendshipEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friendship_id_generator")
    @SequenceGenerator(name = "friendship_id_generator", sequenceName = "friendship_id_seq", allocationSize = 1)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    val fromAccount: AccountEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    val toAccount: AccountEntity,
    @Enumerated(EnumType.STRING)
    var status: FriendshipStatus
) {
    fun toDto(): FriendshipDto {
        return FriendshipDto(
            id,
            fromAccount.toDto(),
            toAccount.toDto(),
            status
        )
    }
}