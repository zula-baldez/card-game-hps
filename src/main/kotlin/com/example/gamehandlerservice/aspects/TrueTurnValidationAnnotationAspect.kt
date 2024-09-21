package com.example.gamehandlerservice.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class TrueTurnValidationAnnotationAspect : BasicValidationAspect() {
    @Before("@annotation(com.example.gamehandlerservice.aspects.TrueTurnValidation)")
    fun validateTrueTurn(joinPoint: JoinPoint) {
        validateGameProps(joinPoint) { _, account, gameHandler ->
            require(gameHandler.turningPlayer() == account) { "Not true time to turn." }
        }
    }
}
