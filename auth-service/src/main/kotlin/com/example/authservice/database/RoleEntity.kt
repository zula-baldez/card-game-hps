package com.example.authservice.database

import com.example.common.util.Role
import jakarta.persistence.*
import lombok.Data

@Table(name = "roles")
@Entity
@Data
class RoleEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    val roleName: Role? = null
)



