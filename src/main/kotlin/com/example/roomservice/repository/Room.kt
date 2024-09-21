package com.example.roomservice.repository

import com.example.gamehandlerservice.database.Account
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "room")
class Room(
    @Id
    @GeneratedValue
    var id: Long?,
    var name: String,
    var hostId: Long,
    var capacity: Int,
    @OneToMany(mappedBy="currentRoomId", fetch = FetchType.LAZY)
    var players: List<Account>
)