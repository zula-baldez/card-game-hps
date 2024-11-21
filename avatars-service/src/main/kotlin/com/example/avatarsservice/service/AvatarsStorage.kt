package com.example.avatarsservice.service

interface AvatarsStorage {
    fun storeFile(id: String, bytes: ByteArray): String
}