package com.example.gamehandlerservice.config

import com.example.common.client.RoomServiceClient
import com.example.common.dto.personalaccout.business.AccountDto
import com.example.common.dto.personalaccout.business.RoomDto
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component

@Component
class AccountAndGameHandlerStompArgumentResolver(
    //private val accountRepository: AccountRepository,
    private val roomServiceClient: RoomServiceClient,
    private val gameHandlerRegistry: GameHandlerRegistry

) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AccountDto::class.java ||
                parameter.parameterType == GameHandler::class.java ||
                parameter.parameterType == RoomDto::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any? {
        val accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor::class.java)
        val sessionAttributes = accessor?.sessionAttributes ?: return null

        return when (parameter.parameterType) {
            GameHandler::class.java -> {
                val gameId = sessionAttributes["x-game-id"] as? Long ?: throw IllegalArgumentException("No gameId found in session attributes")
                return gameHandlerRegistry.getGame(gameId)
            }
            RoomDto::class.java -> {
                val roomId = sessionAttributes["x-room-id"] as? Long ?: throw IllegalArgumentException("No roomId found in session attributes")
                return roomServiceClient.findById(roomId)
            }
            AccountDto::class.java -> {
                val accountId = sessionAttributes["x-user-id"] as? Long ?: throw IllegalArgumentException("No accountId found in session attributes")
                //return accountRepository.findById(accountId).map { i -> i.toDto() }.orElse(null)
            }
            else -> null
        }
    }
}