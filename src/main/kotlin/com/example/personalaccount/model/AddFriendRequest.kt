package com.example.personalaccount.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class AddFriendRequest(
    @get:NotNull(message = "Friend ID must not be null")
    @get:Positive(message = "Friend ID must be positive")
    @JsonProperty("friend_id")
    val friendId: Long
)
