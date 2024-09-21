package com.example.gamehandlerservice.model.game

import com.example.gamehandlerservice.service.game.model.GameData

//card id is strength + suit
//f.e. 10 diamonds is 101
class Card(val suit: Suit, val strenght: Long, var secret: Boolean) {
    val id: Long = strenght * 10 + suit.id

    fun compareTo(gameData: GameData, other: Card): CardCompareResult {
        return when {
            suit == other.suit -> {
                if (strenght > other.strenght) CardCompareResult.MORE else CardCompareResult.LESS
            }

            suit == gameData.trump && other.suit != Suit.Spades -> CardCompareResult.MORE
            other.suit == gameData.trump && suit != Suit.Spades -> CardCompareResult.LESS
            else -> CardCompareResult.NOT_COMPARABLE
        }
    }
}