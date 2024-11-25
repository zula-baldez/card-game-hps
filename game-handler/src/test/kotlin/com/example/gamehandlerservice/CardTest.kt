package com.example.gamehandlerservice

import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.CardCompareResult
import com.example.gamehandlerservice.model.game.Suit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardTest {

    @Test
    fun `compareTo should return MORE when card has higher strength and suit matches`() {
        val card1 = Card(Suit.SPADES, 10)
        val card2 = Card(Suit.SPADES, 5)

        val result = card1.compareTo(Suit.SPADES, card2)

        assertEquals(CardCompareResult.MORE, result)
    }

    @Test
    fun `compareTo should return LESS when card has lower strength and suit matches`() {
        val card1 = Card(Suit.SPADES, 5)
        val card2 = Card(Suit.SPADES, 10)

        val result = card1.compareTo(Suit.SPADES, card2)

        assertEquals(CardCompareResult.LESS, result)
    }

    @Test
    fun `compareTo should return EQUALS when both cards have equal strength and suit matches`() {
        val card1 = Card(Suit.SPADES, 10)
        val card2 = Card(Suit.SPADES, 10)

        val result = card1.compareTo(Suit.SPADES, card2)

        assertEquals(CardCompareResult.EQUALS, result)
    }

    @Test
    fun `compareTo should return MORE when card's suit is trump suit`() {
        val trumpCardSuit = Suit.HEARTS
        val card1 = Card(Suit.HEARTS, 5)
        val card2 = Card(Suit.SPADES, 10)

        val result = card1.compareTo(trumpCardSuit, card2)

        assertEquals(CardCompareResult.MORE, result)
    }

    @Test
    fun `compareTo should return NOT_COMPARABLE when suits do not match and neither is trump suit`() {
        val trumpCardSuit = Suit.HEARTS
        val card1 = Card(Suit.SPADES, 5)
        val card2 = Card(Suit.DIAMONDS, 10)

        val result = card1.compareTo(trumpCardSuit, card2)

        assertEquals(CardCompareResult.NOT_COMPARABLE, result)
    }

    @Test
    fun `compareTo should return NOT_COMPARABLE when suits do not match and first card is trump suit`() {
        val trumpCardSuit = Suit.HEARTS
        val card1 = Card(Suit.HEARTS, 5)
        val card2 = Card(Suit.SPADES, 10)

        val result = card1.compareTo(trumpCardSuit, card2)

        assertEquals(CardCompareResult.MORE, result)
    }
}