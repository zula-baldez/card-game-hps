package com.example.personalaccount.database

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank


@Table(name = "users")
@Entity
class UserEntity(
    @Column(name = "name")
    @NotBlank
    var name: String? = null,

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(name = "user_id_generator", sequenceName = "users_id_seq", allocationSize = 1)
    var id: Long? = null
)