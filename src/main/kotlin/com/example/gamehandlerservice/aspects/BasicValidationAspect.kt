package com.example.gamehandlerservice.aspects

import com.example.gamehandlerservice.database.Account
import com.example.gamehandlerservice.service.game.process.RoomHandler
import org.aspectj.lang.JoinPoint

open class BasicValidationAspect {
    private inline fun <reified T> getInstanceFromJoinPoint(joinPoint: JoinPoint): T? {
        return joinPoint.args.find { it is T } as T?
    }
    protected fun validateAccountAndRoomProps(joinPoint: JoinPoint, predicate: ThrowsPredicate) {
        val room = requireNotNull(getInstanceFromJoinPoint<RoomHandler>(joinPoint))
        val account = requireNotNull(getInstanceFromJoinPoint<Account>(joinPoint))
        predicate(room, account)
    }
}

typealias ThrowsPredicate = (RoomHandler, Account) -> Unit
