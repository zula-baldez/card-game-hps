package com.example.gamehandlerservice.model.game

abstract class CardHolder {
    abstract val id: Long
    val cards: List<Card> = ArrayList()
    abstract val cardHolderType: CardHolderType
}