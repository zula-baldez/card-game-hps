package com.example.common.aspects

import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.personalaccount.database.AccountEntity
import com.example.roomservice.repository.RoomEntity
import org.aspectj.lang.JoinPoint

open class BasicValidationAspect {
    private inline fun <reified T> getInstanceFromJoinPoint(joinPoint: JoinPoint): T? {
        return joinPoint.args.find { it is T } as T?
    }
    protected fun validateGameProps(joinPoint: JoinPoint, predicate: ThrowsPredicate) {
        val roomEntity = requireNotNull(getInstanceFromJoinPoint<RoomEntity>(joinPoint))
        val accountEntity = requireNotNull(getInstanceFromJoinPoint<AccountEntity>(joinPoint))
        val game = requireNotNull(getInstanceFromJoinPoint<GameHandler>(joinPoint))
        predicate(roomEntity, accountEntity, game)
    }
}

typealias ThrowsPredicate = (RoomEntity, AccountEntity, GameHandler) -> Unit
