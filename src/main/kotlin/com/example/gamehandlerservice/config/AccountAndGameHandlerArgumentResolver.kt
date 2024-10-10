package com.example.gamehandlerservice.config

import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.repository.RoomEntity
import com.example.roomservice.repository.RoomRepository
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component

@Component
class AccountAndGameHandlerArgumentResolver(
    private val accountRepository: AccountRepository,
    private val roomRepository: RoomRepository,
    private val gameHandlerRegistry: GameHandlerRegistry

) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AccountEntity::class.java ||
                parameter.parameterType == GameHandler::class.java ||
                parameter.parameterType == RoomEntity::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any? {
        val accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor::class.java)
        val sessionAttributes = accessor?.sessionAttributes ?: return null

        return when (parameter.parameterType) {
            GameHandler::class.java -> {
                val gameId = sessionAttributes["gameId"] as? Long ?: throw IllegalArgumentException("No gameId found in session attributes")
                gameHandlerRegistry.getGame(gameId)
            }
            RoomEntity::class.java -> {
                val roomId = sessionAttributes["roomId"] as? Long ?: throw IllegalArgumentException("No roomId found in session attributes")
                roomRepository.findById(roomId).orElse(null)
            }
            AccountEntity::class.java -> {
                val accountId = sessionAttributes["accountId"] as? Long ?: throw IllegalArgumentException("No accountId found in session attributes")
                accountRepository.findById(accountId).orElse(null)
            }
            else -> null
        }
    }
}