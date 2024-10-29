package com.example.gamehandlerservice.model.game

import com.example.gamehandlerservice.service.game.model.GameData
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

//card id is strength + suit
//f.e. 10 diamonds is 101
data class Card(
    var suit: Suit = Suit.SPADES,
    var strenght: Long = 0,
    var secret: Boolean = false
) {
    @JsonIgnore
    val id: Long = strenght * 10 + suit.id

    fun compareTo(gameData: GameData, other: Card): CardCompareResult {
        return when {
            suit == other.suit -> {
                if (strenght > other.strenght) CardCompareResult.MORE else CardCompareResult.LESS
            }

            suit == gameData.trump && other.suit != Suit.SPADES -> CardCompareResult.MORE
            other.suit == gameData.trump && suit != Suit.SPADES -> CardCompareResult.LESS
            else -> CardCompareResult.NOT_COMPARABLE
        }
    }
}