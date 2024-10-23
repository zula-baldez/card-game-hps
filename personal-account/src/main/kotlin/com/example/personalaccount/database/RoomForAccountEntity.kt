package com.example.personalaccount.database

import jakarta.persistence.*
@Entity
@Table(name = "room_account")
class RoomForAccountEntity(
    @Id
    @GeneratedValue(generator = "room_account_id_generator")
    @SequenceGenerator(name = "room_account_id_generator", sequenceName = "room_account_id_seq", allocationSize = 1)
    var id: Long,
    var name: String)

