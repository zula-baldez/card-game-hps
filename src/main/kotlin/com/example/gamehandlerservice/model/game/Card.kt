package com.example.gamehandlerservice.model.game

//card id is strength + suit
//f.e. 10 diamonds is 101
class Card(val suit: Suit, val strenght: Long, var secret: Boolean) {
    val id: Long = strenght * 10 + suit.id

}