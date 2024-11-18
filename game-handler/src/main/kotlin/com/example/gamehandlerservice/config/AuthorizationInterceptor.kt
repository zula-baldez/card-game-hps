package com.example.gamehandlerservice.config

import com.example.common.client.PersonalAccountClient
import com.example.common.client.RoomServiceClient
import com.example.common.security.TokenParser
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import javax.security.sasl.AuthenticationException

@Service
class AuthorizationInterceptor(
    private val roomManager: RoomServiceClient,
    private val accountClient: PersonalAccountClient,
    private val tokenParser: TokenParser
) : ChannelInterceptor {

    @Throws(AuthenticationException::class)
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        if (StompCommand.CONNECT == accessor.command) {
            val token = accessor.getFirstNativeHeader("Authorization")?.removePrefix("Bearer ") ?: throw IllegalArgumentException()

            val user: UsernamePasswordAuthenticationToken =
                getUsernamePasswordAuthenticationToken(tokenParser.getIdFromToken(token)!!.toLong())

            SecurityContextHolder.getContext().authentication = user

            accessor.user = user

            val userId = tokenParser.getIdFromToken(token)
                ?: throw IllegalArgumentException("Invalid token")

            val account = accountClient.getAccountById(userId.toLong())

            val roomId = accessor.sessionAttributes?.get("x-room-id") as Long

            val room = roomManager.findById(roomId)

            if (room.bannedPlayers.contains(account)) {
                throw IllegalArgumentException("User is banned from this room")
            }

            // Store data into session attributes for further use
            accessor.sessionAttributes?.let { sessionAttributes ->
                sessionAttributes["x-room-id"] = roomId
                sessionAttributes["x-game-id"] = room.currentGameId
                sessionAttributes["x-user-id"] = account.id
            }
        }

        return message
    }


    @Throws(AuthenticationException::class)
    fun getUsernamePasswordAuthenticationToken(uid: Long): UsernamePasswordAuthenticationToken {

        // null credentials, we do not pass the password along
        return UsernamePasswordAuthenticationToken(
            uid,
            null,
            listOf(GrantedAuthority { "USER" }) // MUST provide at least one role
        )
    }
}