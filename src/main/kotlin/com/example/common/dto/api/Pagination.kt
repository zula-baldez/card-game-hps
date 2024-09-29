package com.example.common.dto.api

import jakarta.validation.constraints.Positive

data class Pagination(

    @Positive
    val page: Int,

    @Positive
    val pageSize: Int
)