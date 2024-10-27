package com.example.common.client

import com.example.common.dto.api.Pagination
import com.example.common.dto.roomservice.RoomDto
import com.example.common.dto.roomservice.AddAccountRequest
import com.example.common.dto.roomservice.CreateRoomRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "room-service")
interface RoomServiceClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["/rooms"])
    fun getRooms(page: Pagination): RoomDto

    @RequestMapping(method = [RequestMethod.GET], value = ["/rooms/{roomId}"])
    fun findById(@PathVariable("roomId") roomId: Long): RoomDto


    @RequestMapping(method = [RequestMethod.POST], value = ["/rooms"])
    fun createRoom(@RequestBody createRoomRequest: CreateRoomRequest): RoomDto


    @RequestMapping(method = [RequestMethod.POST], value = ["/rooms/{roomId}/players"])
    fun addPlayer(@PathVariable("roomId") roomId: Long, @RequestBody addAccountRequest: AddAccountRequest): Void
}