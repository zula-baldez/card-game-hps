package com.example.common.security

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component("AuthUtils")
class RequestAuthorizeUtils {
    fun jwtClaimEquals(principal: Any, claim: String, expectedValue: String): Boolean {
        return if (principal is Jwt && principal.claims.containsKey(claim)) {
            principal.claims.get(claim) == expectedValue
        } else {
            false
        }
    }

    fun test(): Boolean {
        return true
    }
}