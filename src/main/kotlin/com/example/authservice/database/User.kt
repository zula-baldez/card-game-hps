package com.example.authservice.database

import jakarta.persistence.*


@Table(name = "users")
@Entity
class User() {
    constructor(name: String, password: String) : this() {
        this.name = name
        this.password = password
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "name")
    var name: String? = null

    @Column(name = "password")
    var password: String? = null

    @Column(name = "fines")
    private var fines: Int? = null

    @ManyToMany(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<RoleEntity> = HashSet()
}