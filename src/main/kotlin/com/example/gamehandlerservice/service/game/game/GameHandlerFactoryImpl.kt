package com.example.gamehandlerservice.service.game.game

import com.example.gamehandlerservice.service.game.stage.StageStateMachineHandler
import com.example.gamehandlerservice.util.id.generator.IdGenerator
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class GameHandlerFactoryImpl(
    private val idGenerator: IdGenerator,
    private val beanFactoryGameHandler: ObjectFactory<GameHandler>,
    private val beanFactoryStateMachine: ObjectFactory<StageStateMachineHandler>

) : GameHandlerFactory {
    override fun instantGameHandler(name: String, roomId: Long): GameHandler {
        val id = idGenerator.generateId()
        val gameHandler: GameHandler = beanFactoryGameHandler.getObject()
        val stateMachineHandler: StageStateMachineHandler = beanFactoryStateMachine.getObject()

        gameHandler.configureGameHandler(
            name, id, roomId, stateMachineHandler
        )
        return gameHandler
    }
}