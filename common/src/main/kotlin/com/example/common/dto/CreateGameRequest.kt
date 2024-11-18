package com.example.common.dto

data class CreateGameRequest(
    val roomId: Long,
    val name: String
) {
}