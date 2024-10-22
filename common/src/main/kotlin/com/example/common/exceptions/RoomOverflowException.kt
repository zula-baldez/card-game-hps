package com.example.common.exceptions

class RoomOverflowException(val roomId: Long) : RuntimeException("Room with id $roomId is overflow")