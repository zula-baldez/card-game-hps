package com.example.gamehandlerservice.model.game

import com.fasterxml.jackson.annotation.JsonIgnore

//card id is strength + suit
//f.e. 10 diamonds is 101
data class Card(
    var suit: Suit = Suit.SPADES,
    var strength: Int = 0
) {
    @JsonIgnore
    val id: Int = strength * 10 + suit.id

    fun compareTo(trumpSuit: Suit, other: Card): CardCompareResult {
        return when (suit) {
            other.suit -> {
                if (strength > other.strength)
                    CardCompareResult.MORE
                else if (strength < other.strength)
                    CardCompareResult.LESS
                else
                    CardCompareResult.EQUALS
            }
            trumpSuit -> CardCompareResult.MORE
            else -> CardCompareResult.NOT_COMPARABLE
        }
    }
}