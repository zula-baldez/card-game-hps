package com.example.gamehandlerservice.service.game

import kotlinx.coroutines.*
import org.springframework.stereotype.Service

@Service
class Timer {

    suspend fun startTimer(): Nothing = withContext(Dispatchers.IO) {
        delay(30000)
        throw RuntimeException()
    }
}