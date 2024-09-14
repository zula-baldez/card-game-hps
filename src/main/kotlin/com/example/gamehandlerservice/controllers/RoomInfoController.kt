package com.example.gamehandlerservice.controllers

import com.example.gamehandlerservice.database.Account
import com.example.roomservice.service.RoomInfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class RoomInfoController(private val roomInfoService: RoomInfoService) {
    @GetMapping("/all-players")
    fun getPlayersInRoom(
        @RequestParam roomId: Int,
        principal: Principal
    ): List<Account>? {
        val id = principal.name.toLong()
        return roomInfoService.getRoomInfo(roomId, id)
    }
}