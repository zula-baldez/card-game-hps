package com.example.common.dto.api

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.domain.Pageable
import kotlin.math.min

data class Pagination(
    @field:PositiveOrZero
    val page: Int = 0,
    @field:Positive
    @JsonProperty("page_size")
    val pageSize: Int = 10
) {
    fun toPageable(): Pageable {
        return Pageable.ofSize(min(pageSize, 50)).withPage(page)
    }
}