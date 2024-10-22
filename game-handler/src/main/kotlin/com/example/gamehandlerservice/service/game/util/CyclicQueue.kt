package com.example.gamehandlerservice.service.game.util

class CyclicQueue<T>(items: List<T>) {
    private val items: MutableList<T> = items.toMutableList()
    private var currentIndex = 0

    fun getSize(): Int = items.size

    fun getAll(): List<T> = items

    fun current(): T = items[currentIndex]

    fun next(): T {
        currentIndex = (currentIndex + 1) % items.size
        return items[currentIndex]
    }

    fun first(): T = items[0]

    fun delete(t: T): T? {
        val index = items.indexOf(t)
        if (index == -1) return null

        val deletedElement = items.removeAt(index)

        if (index < currentIndex) {
            currentIndex--
        } else if (index == currentIndex && items.isNotEmpty()) {
            currentIndex %= items.size
        }

        return deletedElement
    }

    fun reset() {
        currentIndex = 0
    }
}
