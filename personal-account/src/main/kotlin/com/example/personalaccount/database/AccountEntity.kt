package com.example.personalaccount.database

import com.example.common.dto.personalaccout.AccountDto
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

@Entity
@Table(name = "accounts")
class AccountEntity(
    @Id
    var id: Long = 0,

    @field:NotBlank
    @Column(name = "name")
    var name: String,

    @field:PositiveOrZero
    @Column(name = "fines")
    var fines: Int,

    @Column(name = "avatar")
    var avatar: String,

    @OneToMany(mappedBy = "fromAccount", cascade = [CascadeType.PERSIST])
    var friends: MutableSet<FriendshipEntity> = HashSet(),
    @OneToMany(mappedBy = "toAccount", cascade = [CascadeType.PERSIST])
    var incomingFriendRequests: MutableSet<FriendshipEntity> = HashSet(),

    @Column(name = "current_room_id")
    var currentRoomId: Long?
) {
    fun toDto(): AccountDto {
        return AccountDto(
            id,
            name,
            fines,
            avatar,
            currentRoomId
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other is AccountEntity) {
            return other.id == this.id
        }
        return false
    }
}
