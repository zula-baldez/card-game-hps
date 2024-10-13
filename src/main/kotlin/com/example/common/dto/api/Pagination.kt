package com.example.common.dto.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Pageable

import jakarta.validation.constraints.Positive
import kotlin.math.min

data class Pagination(
    @Positive
    val page: Int = 0,
    @Positive
    @JsonProperty("page_size")
    val pageSize: Int = 10
) {
    fun toPageable(): Pageable {
        return Pageable.ofSize(min(pageSize, 50)).withPage(page)
    }
}