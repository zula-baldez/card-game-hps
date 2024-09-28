package com.example.roomservice.repository

import com.example.personalaccount.database.Account
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "room")
class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_id_generator")
    @SequenceGenerator(name = "room_id_generator", sequenceName = "room_id_seq", allocationSize = 1)
    var id: Long?,
    @Column(name = "name")
    var name: String,
    @Column(name = "host_id")
    var hostId: Long,
    @Column(name = "capacity")
    var capacity: Int,
    @Column(name = "current_game_id")
    var currentGameId: Long,
    @OneToMany(mappedBy="room", fetch = FetchType.LAZY)
    var players: List<Account> = ArrayList()
)