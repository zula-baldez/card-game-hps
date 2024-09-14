package com.example.gamehandlerservice.model.game

class Table(override val id: Long) : CardHolder() {
    override val cardHolderType: CardHolderType = CardHolderType.Player
}
