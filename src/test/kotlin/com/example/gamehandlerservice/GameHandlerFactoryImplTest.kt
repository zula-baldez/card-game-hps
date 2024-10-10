package com.example.gamehandlerservice

import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.game.GameHandlerFactoryImpl
import com.example.gamehandlerservice.service.game.stage.StageStateMachineHandler
import com.example.gamehandlerservice.util.id.generator.IdGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.ObjectFactory


class GameHandlerFactoryImplTest {

    private lateinit var idGenerator: IdGenerator
    private lateinit var beanFactoryGameHandler: ObjectFactory<GameHandler>
    private lateinit var beanFactoryStateMachine: ObjectFactory<StageStateMachineHandler>
    private lateinit var gameHandlerFactory: GameHandlerFactoryImpl

    @BeforeEach
    fun setUp() {
        idGenerator = MockIdGenerator()
        beanFactoryGameHandler = mock(ObjectFactory::class.java) as ObjectFactory<GameHandler>
        beanFactoryStateMachine = mock(ObjectFactory::class.java) as ObjectFactory<StageStateMachineHandler>
        gameHandlerFactory = GameHandlerFactoryImpl(idGenerator, beanFactoryGameHandler, beanFactoryStateMachine)
    }

    @Test
    fun `should create a game handler with a unique ID`() {
        val name = "Test Game"
        val roomId = 123L
        val mockGameHandler = mock(GameHandler::class.java)
        val mockStateHandler = mock(StageStateMachineHandler::class.java)

        `when`(beanFactoryGameHandler.getObject()).thenReturn(mockGameHandler)
        `when`(beanFactoryStateMachine.getObject()).thenReturn(mockStateHandler)

        val gameHandler = gameHandlerFactory.instantGameHandler(name, roomId)

        assertEquals(mockGameHandler, gameHandler)
        Mockito.verify(mockGameHandler).configureGameHandler(name, 1L, roomId, mockStateHandler)
    }

    @Test
    fun `should not share game handlers between calls`() {
        val name1 = "First Game"
        val roomId1 = 1L
        val mockGameHandler1 = mock(GameHandler::class.java)
        val mockStateHandler1 = mock(StageStateMachineHandler::class.java)

        val name2 = "Second Game"
        val roomId2 = 2L
        val mockGameHandler2 = mock(GameHandler::class.java)
        val mockStateHandler2 = mock(StageStateMachineHandler::class.java)

        `when`(beanFactoryGameHandler.getObject()).thenReturn(mockGameHandler1).thenReturn(mockGameHandler2)
        `when`(beanFactoryStateMachine.getObject()).thenReturn(mockStateHandler1).thenReturn(mockStateHandler2)

        val gameHandler1 = gameHandlerFactory.instantGameHandler(name1, roomId1)
        val gameHandler2 = gameHandlerFactory.instantGameHandler(name2, roomId2)

        assertEquals(mockGameHandler1, gameHandler1)
        assertEquals(mockGameHandler2, gameHandler2)
        Mockito.verify(mockGameHandler1).configureGameHandler(name1, 1L, roomId1, mockStateHandler1)
        Mockito.verify(mockGameHandler2).configureGameHandler(name2, 2L, roomId2, mockStateHandler2)
    }
}

class MockIdGenerator: IdGenerator {
    private var id = 0L
    override fun generateId(): Long {
        id++
        return id
    }
}