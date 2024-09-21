package com.example.gamehandlerservice.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class HostOnlyAnnotationAspect : BasicValidationAspect() {
    @Before("@annotation(com.example.gamehandlerservice.aspects.HostOnly)")
    fun validateHostOnly(joinPoint: JoinPoint) {
        validateGameProps(joinPoint) { room, account, _ ->
            require(account.id == room.hostId) { "Account is not the host." }
        }
    }
}
