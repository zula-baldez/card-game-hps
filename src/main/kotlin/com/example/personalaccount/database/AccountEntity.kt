package com.example.personalaccount.database

import com.example.common.dto.business.AccountDto
import com.example.roomservice.repository.RoomEntity
import jakarta.persistence.*

@Entity
@Table(name = "player")
class AccountEntity(
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
    var roomEntity: RoomEntity? = null,

    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "friend_id")]
    )
    var friends: MutableSet<AccountEntity> = HashSet()
) {
    fun toDto(): AccountDto {
        return AccountDto(
            id,
            name,
            fines,
            active,
            roomId = roomEntity?.id
        )
    }
}
