package com.example.gamehandlerservice.security

import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.text.ParseException

@Component
class TokenParser {
    fun getIdFromToken(token: String?): String? {
        try {
            val decodedJWT = SignedJWT.parse(token)
            return decodedJWT.jwtClaimsSet.claims["sub"].toString()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

}