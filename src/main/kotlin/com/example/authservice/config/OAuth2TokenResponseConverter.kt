package com.example.authservice.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.stereotype.Component

@Component
class OAuth2TokenResponseConverter : Converter<MutableMap<String, Any>, OAuth2AccessTokenResponse> {
    override fun convert(tokenResponseParameters: MutableMap<String, Any>): OAuth2AccessTokenResponse {
        val accessToken = tokenResponseParameters[OAuth2ParameterNames.ACCESS_TOKEN]
        val accessTokenType: OAuth2AccessToken.TokenType = OAuth2AccessToken.TokenType.BEARER
        val additionalParameters: MutableMap<String, Any> = HashMap()
        tokenResponseParameters.forEach { (s: String, s2: Any) ->
            additionalParameters[s] = s2
        }
        return OAuth2AccessTokenResponse.withToken(accessToken.toString())
            .tokenType(accessTokenType)
            .additionalParameters(additionalParameters)
            .build()
    }
}