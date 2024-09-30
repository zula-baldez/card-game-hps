package com.example.personalaccount.database

import com.example.common.dto.business.AccountDto
import com.example.roomservice.repository.RoomEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

@Entity
@Table(name = "accounts")
class AccountEntity(
    @Id
    var id: Long,

    @NotBlank
    @Column(name = "name")
    var name: String,

    @Positive
    @Column(name = "fines")
    var fines: Int,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_room_id", referencedColumnName = "id")
    var roomEntity: RoomEntity? = null,
    @OneToMany(mappedBy = "fromAccount", cascade = [CascadeType.PERSIST])
    var friends: MutableSet<FriendshipEntity> = HashSet(),
    @OneToMany(mappedBy = "toAccount", cascade = [CascadeType.PERSIST])
    var incomingFriendRequests: MutableSet<FriendshipEntity> = HashSet()
) {
    fun toDto(): AccountDto {
        return AccountDto(
            id,
            name,
            fines,
            roomId = roomEntity?.id
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other is AccountEntity) {
            return other.id == this.id
        }
        return false
    }
}
