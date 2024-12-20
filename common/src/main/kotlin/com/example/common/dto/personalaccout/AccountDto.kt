package com.example.common.dto.personalaccout

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AccountDto(
    val id: Long,

    @field:NotBlank
    val name: String,

    @field:Min(0)
    var fines: Int,

    var avatar: String,

    @JsonProperty("room_id")
    val roomId: Long?
)