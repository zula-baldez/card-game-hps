package com.example.roomservice.kafkaconnections

import com.example.common.exceptions.AccountNotFoundException
import com.example.common.exceptions.RoomNotFoundException
import com.example.common.kafkaconnections.ConnectionMessage
import com.example.common.kafkaconnections.ConnectionMessageType
import com.example.roomservice.repository.AccountInRoomRepository
import com.example.roomservice.repository.RoomRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class ConnectionListener(
    private val accountsInRoomRepository: AccountInRoomRepository,
    private val roomRepository: RoomRepository
) {
    @KafkaListener(topics = ["game-connections"])
    fun listen(data: ConnectionMessage): Mono<Void> {
        val accountId = data.accountDto.id

        if (data.type == ConnectionMessageType.DISCONNECT) {
            return accountsInRoomRepository.findById(accountId)
                .switchIfEmpty(Mono.error(AccountNotFoundException(accountId)))
                .flatMap { account ->
                    accountsInRoomRepository.delete(account)
                        .then(roomRepository.findById(account.roomId))
                        .switchIfEmpty(Mono.error(RoomNotFoundException(account.roomId)))
                        .flatMap { room ->
                            val updatedRoom = room.copy(capacity = room.capacity - 1)
                            roomRepository.save(updatedRoom)
                        }
                }
                .then()
        }

        return Mono.empty()
    }
}
