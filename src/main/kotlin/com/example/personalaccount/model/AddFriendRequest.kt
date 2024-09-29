package com.example.personalaccount.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class AddFriendRequest(
    @NotNull(message = "Friend ID must not be null")
    @Positive(message = "Friend ID must be positive")
    val friendId: Long
)
