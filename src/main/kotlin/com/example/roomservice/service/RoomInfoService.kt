package com.example.roomservice.service

import com.example.gamehandlerservice.database.Account
import org.springframework.stereotype.Component

@Component
class RoomInfoServiceImpl(private val roomManager: RoomManager) : RoomInfoService {
    override fun getRoomInfo(roomId : Int, playerId: Long) : List<Account>? {
        val players = roomManager.getRoom(roomId.toLong())?.getAllPlayers()?.toMutableList()
        //TODO ЧЗХ???
        val playerToMove = players?.find { it.id == playerId }

        if (playerToMove != null) {
            players.removeAt(players.indexOf(playerToMove))
            players.add(0, playerToMove)
        }
        return players
    }
}

 interface RoomInfoService {
     fun getRoomInfo(roomId : Int, playerId: Long) : List<Account>?
}
