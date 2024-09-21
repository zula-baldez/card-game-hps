package com.example.gamehandlerservice.config

import com.example.personalaccount.database.Account
import com.example.gamehandlerservice.service.game.process.RoomHandler
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component

@Component
class AccountAndGameHandlerArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Account::class.java ||
                parameter.parameterType == RoomHandler::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any {
        val accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor::class.java)
        return if (parameter.parameterType == RoomHandler::class.java)
            accessor?.sessionAttributes?.get("room") as RoomHandler
        else
            return accessor?.sessionAttributes?.get("account") as Account
    }
}