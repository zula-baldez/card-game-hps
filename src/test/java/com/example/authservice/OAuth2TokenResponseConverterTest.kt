package com.example.authservice

import com.example.authservice.config.OAuth2TokenResponseConverter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.core.OAuth2AccessToken

class OAuth2TokenResponseConverterTest {

    private val converter = OAuth2TokenResponseConverter()

    @Test
    fun `should convert token response parameters to OAuth2AccessTokenResponse successfully`() {
        val tokenResponseParameters = mutableMapOf<String, Any>(
            "access_token" to "sampleAccessToken",
            "expires_in" to 3600
        )

        val result = converter.convert(tokenResponseParameters)

        assertEquals("sampleAccessToken", result.accessToken.tokenValue)
        assertEquals(OAuth2AccessToken.TokenType.BEARER, result.accessToken.tokenType)
        assertTrue(result.additionalParameters.containsKey("expires_in"))
        assertEquals(3600, result.additionalParameters["expires_in"])
    }
}