package com.example.personalaccount.database

import jakarta.persistence.*

@Entity
@Table(name = "player")
class Account(
    var name: String,
    var fines: Int,
    var active: Boolean = false,
    var additionalCards: Int,
    @Id
    var id: Long,

    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "friend_id")]
    )
    var friends: Set<Account> = HashSet()
)
