package com.example.gamehandlerservice.model.game

class Player(override val id: Long) : CardHolder() {
    override val cardHolderType: CardHolderType = CardHolderType.Player

}
