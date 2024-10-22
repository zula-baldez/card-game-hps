package com.example.common.exceptions

class RoomNotFoundException(val roomId: Long) : RuntimeException("Room with id $roomId not found")