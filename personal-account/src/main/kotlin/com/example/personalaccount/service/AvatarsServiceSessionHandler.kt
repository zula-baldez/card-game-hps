package com.example.personalaccount.service

import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter

class AvatarsServiceSessionHandler : StompSessionHandlerAdapter() {
    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        println("Connected!")
    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable
    ) {
        println("exception")
        exception.printStackTrace()
    }

    override fun handleTransportError(session: StompSession, exception: Throwable) {
        println("transport")
        exception.printStackTrace()
    }

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        println("frame")
    }
}