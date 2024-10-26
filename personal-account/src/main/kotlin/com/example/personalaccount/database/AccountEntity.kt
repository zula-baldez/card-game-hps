package com.example.personalaccount.database

import com.example.common.dto.personalaccout.business.AccountDto
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

@Entity
@Table(name = "accounts")
class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_generator")
    @SequenceGenerator(name = "account_id_generator", sequenceName = "account_id_seq", allocationSize = 1)
    var id: Long = 0,

    @field:NotBlank
    @Column(name = "name")
    var name: String,

    @field:PositiveOrZero
    @Column(name = "fines")
    var fines: Int,
    @OneToMany(mappedBy = "fromAccount", cascade = [CascadeType.PERSIST])
    var friends: MutableSet<FriendshipEntity> = HashSet(),
    @OneToMany(mappedBy = "toAccount", cascade = [CascadeType.PERSIST])
    var incomingFriendRequests: MutableSet<FriendshipEntity> = HashSet()
) {
    fun toDto(): AccountDto {
        return AccountDto(
            id,
            name,
            fines
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other is AccountEntity) {
            return other.id == this.id
        }
        return false
    }
}
