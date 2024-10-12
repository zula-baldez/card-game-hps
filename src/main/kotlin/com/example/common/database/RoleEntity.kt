package com.example.common.database

import com.example.common.util.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import lombok.Data

@Table(name = "roles")
@Entity
@Data
class RoleEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_generator")
    @SequenceGenerator(name = "role_id_generator", sequenceName = "roles_id_seq", allocationSize = 1)
    val id: Long? = null

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    val roleName: Role? = null
}


