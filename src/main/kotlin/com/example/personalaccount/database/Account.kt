package com.example.personalaccount.database

import com.example.roomservice.repository.Room
import jakarta.persistence.*

@Entity
@Table(name = "player")
class Account(
    @Column(name = "name")
    var name: String,
    @Column(name = "fines")
    var fines: Int,
    @Column(name = "active")
    var active: Boolean = false,
    @Id
    var id: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_room_id", referencedColumnName = "id")
    var room: Room? = null,

    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "friend_id")]
    )
    var friends: MutableSet<Account> = HashSet()
)
