package com.example.common

import com.example.common.security.TokenParser
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock


class TokenParserTest {

    private var tokenParser: TokenParser = mock(TokenParser::class.java)

    @Test
    fun `should return null for invalid token`() {
        val token = "invalid_token"
        val id = tokenParser.getIdFromToken(token)
        assertNull(id)
    }

    @Test
    fun `should return null for null token`() {
        val id = tokenParser.getIdFromToken(null)
        assertNull(id)
    }

}