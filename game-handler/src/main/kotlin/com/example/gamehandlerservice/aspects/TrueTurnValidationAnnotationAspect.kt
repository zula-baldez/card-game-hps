package com.example.gamehandlerservice.aspects

import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class TrueTurnValidationAnnotationAspect {
    @Before("@annotation(com.example.gamehandlerservice.aspects.TrueTurnValidation)")
    fun validateTrueTurn(joinPoint: JoinPoint) {
        val accountDto = requireNotNull(getInstanceFromJoinPoint<AccountDto>(joinPoint))
        val game = requireNotNull(getInstanceFromJoinPoint<GameHandler>(joinPoint))

        require(game.turningPlayer()?.id == accountDto.id) { "Not true time to turn." }
    }

    private inline fun <reified T> getInstanceFromJoinPoint(joinPoint: JoinPoint): T? {
        return joinPoint.args.find { it is T } as T?
    }
}