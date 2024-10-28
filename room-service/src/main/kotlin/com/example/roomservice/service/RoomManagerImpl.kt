package com.example.roomservice.service

import com.example.common.client.PersonalAccountClient
import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.client.ReactiveServiceTokenClient
import com.example.common.dto.api.Pagination
import com.example.common.dto.roomservice.RoomDto
import com.example.roomservice.repository.RoomEntity
import com.example.roomservice.repository.RoomRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RoomManagerImpl(
    private val roomRepository: RoomRepository,
    private val roomAccountManager: RoomAccountManager,
    private val personalAccountClient: ReactivePersonalAccountClient,
) : RoomManager {
    private fun makeRoomDto(roomEntity: RoomEntity): Mono<RoomDto> {
        return roomAccountManager.getAccountsInRoom(roomEntity.id).flatMap { personalAccountClient.getAccountById(it) }
            .collectList()
            .zipWith(
                roomAccountManager.getBannedAccountsInRoom(roomEntity.id)
                    .flatMap { personalAccountClient.getAccountById(it) }.collectList()
            )
            .map { result ->
                return@map RoomDto(
                    id = roomEntity.id,
                    hostId = roomEntity.hostId,
                    name = roomEntity.name,
                    capacity = roomEntity.capacity,
                    currentGameId = roomEntity.currentGameId,
                    players = result.t1,
                    bannedPlayers = result.t2
                )
            }
    }

    override fun createRoom(name: String, hostId: Long, capacity: Int): Mono<RoomDto> {
        val roomEntity = RoomEntity(0, name, hostId, capacity, 0)
        return roomRepository.save(roomEntity).flatMap { makeRoomDto(it) }
    }

    override fun deleteRoom(id: Long): Mono<Void> {
        return roomRepository.deleteById(id)
    }

    override fun getRoom(id: Long): Mono<RoomDto> {
        return roomRepository.findById(id).flatMap { makeRoomDto(it) }
    }

    override fun getRooms(page: Pagination): Flux<RoomDto> {
        return roomRepository
            .findAllByIdNotNull(page.toPageable())
            .flatMap { makeRoomDto(it) }
    }
}