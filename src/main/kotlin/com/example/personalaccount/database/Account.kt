package com.example.personalaccount.database

import com.example.common.dto.business.AccountDto
import com.example.roomservice.repository.Room
import jakarta.persistence.*

@Entity
@Table(name = "player")
class Account(
    @Id
    var id: Long,
    @Column(name = "name")
    var name: String,
    @Column(name = "fines")
    var fines: Int,
    @Column(name = "active")
    var active: Boolean = false,
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
) {
    fun toDto(): AccountDto {
        return AccountDto(
            id,
            name,
            fines,
            active,
            roomId = room?.id
        )
    }
}
