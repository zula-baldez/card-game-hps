package com.example.common
import com.example.common.aspects.TrueTurnValidationAnnotationAspect
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.personalaccount.database.AccountEntity
import com.example.roomservice.repository.RoomEntity
import org.aspectj.lang.JoinPoint
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class TrueTurnValidationAnnotationAspectTest {
    private val aspect = TrueTurnValidationAnnotationAspect()

    @Mock
    private lateinit var gameHandler: GameHandler

    @Test
    fun `validateTrueTurn should pass when it's the player's turn`() {
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
        gameHandler.apply {
            `when`(turningPlayer()).thenReturn(accountEntity)
        }

        `when`(mockJoinPoint.args).thenReturn(arrayOf(roomEntity, accountEntity, gameHandler))
        aspect.validateTrueTurn(mockJoinPoint)
    }
}