package com.example.w3_tutorial

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Card(val rank: String, val suit: String) {
    var flip: Boolean = true

    fun flip() {
        flip = !flip
    }

    fun printDetails(): String {
        return if (flip) {
            "$rank of $suit"
        } else {
            "----"
        }
    }
}

class CardActivity : AppCompatActivity() {
    private lateinit var card: Card
    private lateinit var cardDetails: TextView
    private lateinit var flipButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_layout)

        card = Card("ACE", "HEARTS")
        cardDetails = findViewById(R.id.cardDetails)
        flipButton = findViewById(R.id.flipButton)

        cardDetails.text = card.printDetails()

        flipButton.setOnClickListener {
            card.flip()
            cardDetails.text = card.printDetails()
        }
    }
}

fun main() {
    val card = Card("ACE", "HEARTS")
    println(card.printDetails())
    card.flip()
    println(card.printDetails())
}