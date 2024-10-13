package com.example.common.dto.api

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Positive

data class ScrollPositionDto(

    @Positive
    @JsonProperty("offset")
    var offset: Long?
)