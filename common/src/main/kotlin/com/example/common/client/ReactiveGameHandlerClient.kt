package com.example.common.client

import com.example.common.dto.CreateGameRequest
import com.example.common.dto.CreateGameResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient("game-handler", configuration = [ReactiveUserTokenFeignClientConfiguration::class], primary = false)
interface ReactiveGameHandlerClient {
    @RequestMapping(method = [RequestMethod.POST], value = ["/create"])
    fun createGame(createGameRequest: CreateGameRequest): Mono<CreateGameResponse>
}