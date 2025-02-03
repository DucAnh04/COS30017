package com.example.w4_tutorials

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CardTest {

    @Test
    fun testFlip() {
        // Given
        val card = Card("ACE", "HEARTS")

        // When
        card.flip()

        // Then
        assertEquals(false, card.flip)
        assertEquals("----", card.printDetails())

        // When
        card.flip()

        // Then
        assertEquals(true, card.flip)
        assertEquals("ACE of HEARTS", card.printDetails())
    }
}