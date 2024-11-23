package com.example.roomservice.service

import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.dto.api.Pagination
import com.example.common.dto.roomservice.RoomDto
import com.example.common.kafkaconnections.RoomUpdateEvent
import com.example.common.kafkaconnections.RoomUpdateEvent.Companion.RoomUpdateEventType
import com.example.roomservice.repository.RoomEntity
import com.example.roomservice.repository.RoomRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RoomManagerImpl(
    private val roomRepository: RoomRepository,
    private val roomAccountManager: RoomAccountManager,
    private val personalAccountClient: ReactivePersonalAccountClient,
    private val roomUpdateEventSender: RoomUpdateEventSender
) : RoomManager {
    private fun makeRoomDto(roomEntity: RoomEntity): Mono<RoomDto> {
        return roomAccountManager.getAccountsInRoom(roomEntity.id).flatMap { personalAccountClient.getAccountById(it) }
            .collectList().zipWith(
                roomAccountManager.getBannedAccountsInRoom(roomEntity.id)
                    .flatMap { personalAccountClient.getAccountById(it) }.collectList()
            ).map { result ->
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
        val createRoomEntity = RoomEntity(0, name, hostId, capacity, 0)
        return roomRepository.save(createRoomEntity).flatMap { roomEntity ->
            roomEntity.currentGameId = roomEntity.id
            return@flatMap roomRepository.save(roomEntity)
                .flatMap {
                    makeRoomDto(it)
                }.map { roomDto ->
                    roomUpdateEventSender.sendRoomUpdateEvent(
                        RoomUpdateEvent(
                            roomId = roomDto.id, eventType = RoomUpdateEventType.ROOM_CREATED, roomDto = roomDto
                        )
                    )
                    return@map roomDto
                }
        }
    }

    override fun deleteRoom(id: Long): Mono<Void> {
        return roomRepository.deleteById(id).then(
            Mono.fromRunnable {
                roomUpdateEventSender.sendRoomUpdateEvent(
                    RoomUpdateEvent(
                        id, RoomUpdateEventType.ROOM_DELETED
                    )
                )
            }
        )
    }

    override fun getRoom(id: Long): Mono<RoomDto> {
        return roomRepository.findById(id).flatMap { makeRoomDto(it) }
    }

    override fun getRooms(page: Pagination): Flux<RoomDto> {
        return roomRepository.findAllByIdNotNull(page.toPageable()).flatMap { makeRoomDto(it) }
    }
}