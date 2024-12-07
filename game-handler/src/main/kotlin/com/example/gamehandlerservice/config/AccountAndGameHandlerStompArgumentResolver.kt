package com.example.gamehandlerservice.config

import com.example.common.client.PersonalAccountClient
import com.example.common.client.RoomServiceClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import javax.security.sasl.AuthenticationException

@Component
class AccountAndGameHandlerStompArgumentResolver(
    private val accountClient: PersonalAccountClient,
    private val roomServiceClient: RoomServiceClient,

    ) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AccountDto::class.java ||
                parameter.parameterType == RoomDto::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any? {
        val accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor::class.java)
        val sessionAttributes = accessor?.sessionAttributes ?: return null
        val accountId = sessionAttributes["x-user-id"] as? Long
            ?: throw IllegalArgumentException("No accountId found in session attributes")
        val user: UsernamePasswordAuthenticationToken =
            getUsernamePasswordAuthenticationToken(accountId)

        SecurityContextHolder.getContext().authentication = user

        return when (parameter.parameterType) {
            RoomDto::class.java -> {
                val roomId = sessionAttributes["x-room-id"] as? Long
                    ?: throw IllegalArgumentException("No roomId found in session attributes")
                return roomServiceClient.findById(roomId)
            }

            AccountDto::class.java -> {
                return accountClient.getAccountById(accountId)
            }

            else -> null
        }
    }

    @Throws(AuthenticationException::class)
    fun getUsernamePasswordAuthenticationToken(uid: Long): UsernamePasswordAuthenticationToken {

        return UsernamePasswordAuthenticationToken(
            uid,
            null,
            listOf(GrantedAuthority { "USER" })
        )
    }
}