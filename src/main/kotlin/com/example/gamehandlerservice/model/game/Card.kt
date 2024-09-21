package com.example.gamehandlerservice.model.game

import com.example.gamehandlerservice.service.game.process.cards.CardHandler

//card id is strength + suit
//f.e. 10 diamonds is 101
class Card(val suit: Suit, val strenght: Long, var secret: Boolean) {
    val id: Long = strenght * 10 + suit.id

    fun compareTo(cardHandler: CardHandler, other: Card): CardCompareResult {
        if (suit == other.suit) {
            if (strenght - other.strenght > 0)  CardCompareResult.MORE else CardCompareResult.LESS
        }
        if (suit == cardHandler.trump && other.suit != Suit.Spades) {
            return CardCompareResult.MORE
        }
        if (other.suit == cardHandler.trump && suit != Suit.Spades) {
            return CardCompareResult.LESS
        }
        return CardCompareResult.NOT_COMPARABLE
    }
}