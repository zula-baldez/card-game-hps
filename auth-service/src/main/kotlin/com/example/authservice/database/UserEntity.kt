package com.example.authservice.database

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank


@Table(name = "users")
@Entity
class UserEntity(
    @Column(name = "name")
    @NotBlank
    var name: String? = null,

    @Column(name = "password")
    @NotBlank
    var password: String? = null,

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToMany(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<RoleEntity> = HashSet()
)