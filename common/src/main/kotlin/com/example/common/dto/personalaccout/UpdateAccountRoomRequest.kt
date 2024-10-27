package com.example.common.dto.personalaccout

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateAccountRoomRequest(
    @JsonProperty("room_id")
    val roomId: Long?
)
