package com.example.common

import com.example.common.aspects.HostOnlyAnnotationAspect
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.personalaccount.database.AccountEntity
import com.example.roomservice.repository.RoomEntity
import org.aspectj.lang.JoinPoint
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class HostOnlyAnnotationAspectTest {
    private val aspect = HostOnlyAnnotationAspect()
    @Mock
    private lateinit var gameHandler: GameHandler

    @Test
    fun `validateHostOnly should pass when account is host`() {
        MockitoAnnotations.openMocks(this)
        val mockJoinPoint = mock(JoinPoint::class.java)
        val roomEntity = RoomEntity(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf()
        )
        val accountEntity = AccountEntity(
            name = "User1",
            fines = 0,
            id = 1L
        )

        `when`(mockJoinPoint.args).thenReturn(arrayOf(roomEntity, accountEntity, gameHandler))
        aspect.validateHostOnly(mockJoinPoint)
    }
}
