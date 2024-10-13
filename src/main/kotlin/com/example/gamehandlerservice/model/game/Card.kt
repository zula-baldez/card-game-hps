package com.example.gamehandlerservice.model.game

import com.example.gamehandlerservice.service.game.model.GameData
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

//card id is strength + suit
//f.e. 10 diamonds is 101
data class Card(
    @JsonProperty("suit")
    val suit: Suit,
    @JsonProperty("strenght")
    val strenght: Long,
    @JsonProperty("secret")
    var secret: Boolean
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