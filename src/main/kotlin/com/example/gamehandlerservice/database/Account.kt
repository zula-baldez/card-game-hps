package com.example.gamehandlerservice.database

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "player")
class Account(
    var name: String,
    var fines: Int,
    var active: Boolean = false,
    var additionalCards: Int,
    @Id
    var id : Long
)
