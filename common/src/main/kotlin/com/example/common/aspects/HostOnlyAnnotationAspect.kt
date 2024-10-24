package com.example.common.aspects

import com.example.common.dto.personalaccout.business.AccountDto
import com.example.common.dto.personalaccout.business.RoomDto
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class HostOnlyAnnotationAspect {
    @Before("@annotation(com.example.common.aspects.HostOnly)")
    fun validateHostOnly(joinPoint: JoinPoint) {
        val roomDto = requireNotNull(getInstanceFromJoinPoint<RoomDto>(joinPoint))
        val accountDto = requireNotNull(getInstanceFromJoinPoint<AccountDto>(joinPoint))
        require(accountDto.id == roomDto.hostId) { "Account is not the host." }
    }

    private inline fun <reified T> getInstanceFromJoinPoint(joinPoint: JoinPoint): T? {
        return joinPoint.args.find { it is T } as T?
    }
}