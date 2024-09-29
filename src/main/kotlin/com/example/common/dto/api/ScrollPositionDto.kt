package com.example.common.dto.api

import jakarta.validation.constraints.Positive

data class ScrollPositionDto(

    @Positive
    var offset: Long?
)