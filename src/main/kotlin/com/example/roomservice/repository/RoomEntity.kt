package com.example.roomservice.repository

import com.example.common.dto.business.RoomDto
import com.example.personalaccount.database.AccountEntity
import jakarta.persistence.*

@Entity
@Table(name = "room")
class RoomEntity(
    @Id
    @GeneratedValue(generator = "room_id_generator")
    @SequenceGenerator(name = "room_id_generator", sequenceName = "room_id_seq", allocationSize = 1)
    var id: Long,
    var name: String,
    var hostId: Long,
    var capacity: Int,
    var currentGameId: Long,
    @OneToMany(mappedBy = "roomEntity", fetch = FetchType.LAZY)
    var players: MutableList<AccountEntity> = ArrayList()
) {
    fun toDto(): RoomDto {
        return RoomDto(
            id,
            name,
            hostId,
            capacity,
            players.map { it.toDto() },
            currentGameId
        )
    }
}