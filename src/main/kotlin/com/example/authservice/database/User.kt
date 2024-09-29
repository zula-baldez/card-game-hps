package com.example.authservice.database

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank


@Table(name = "users")
@Entity
class User() {
    constructor(name: String, password: String) : this() {
        this.name = name
        this.password = password
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(name = "user_id_generator", sequenceName = "users_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "name")
    @NotBlank
    var name: String? = null

    @Column(name = "password")
    @NotBlank
    var password: String? = null

    @ManyToMany(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<RoleEntity> = HashSet()
}