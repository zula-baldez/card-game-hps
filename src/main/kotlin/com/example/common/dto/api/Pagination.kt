package com.example.common.dto.api

import org.springframework.data.domain.Pageable

import jakarta.validation.constraints.Positive

data class Pagination(
    @Positive
    val page: Int = 0,
    @Positive
    val pageSize: Int = 10
) {
    fun toPageable(): Pageable {
        return Pageable.ofSize(pageSize).withPage(page)
    }
}