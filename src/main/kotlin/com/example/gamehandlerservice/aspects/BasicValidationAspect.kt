package com.example.gamehandlerservice.aspects

import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.personalaccount.database.Account
import com.example.roomservice.repository.Room
import org.aspectj.lang.JoinPoint

open class BasicValidationAspect {
    private inline fun <reified T> getInstanceFromJoinPoint(joinPoint: JoinPoint): T? {
        return joinPoint.args.find { it is T } as T?
    }
    protected fun validateGameProps(joinPoint: JoinPoint, predicate: ThrowsPredicate) {
        val room = requireNotNull(getInstanceFromJoinPoint<Room>(joinPoint))
        val account = requireNotNull(getInstanceFromJoinPoint<Account>(joinPoint))
        val game = requireNotNull(getInstanceFromJoinPoint<GameHandler>(joinPoint))
        predicate(room, account, game)
    }
}

typealias ThrowsPredicate = (Room, Account, GameHandler) -> Unit
