package com.example.w3_tutorial

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    var state = R.drawable.assignment_turned_in_24dp_e8eaed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imageView = findViewById<ImageView>(R.id.imageView)

        imageView.setOnLongClickListener {
            state = when (state) {
                R.drawable.assignment_turned_in_24dp_e8eaed -> R.drawable.assignment_late_24dp_e8eaed
                R.drawable.assignment_late_24dp_e8eaed -> R.drawable.assignment_turned_in_24dp_e8eaed
                else -> R.drawable.assignment_turned_in_24dp_e8eaed
            }
            imageView.setImageDrawable(getDrawable(state))
            return@setOnLongClickListener false
        }
        imageView.setOnClickListener {
            state = when (state) {
                R.drawable.assignment_turned_in_24dp_e8eaed -> R.drawable.assignment_late_24dp_e8eaed
                R.drawable.assignment_late_24dp_e8eaed -> R.drawable.assignment_turned_in_24dp_e8eaed
                else -> R.drawable.assignment_turned_in_24dp_e8eaed
            }
            imageView.setImageDrawable(getDrawable(state))

        }

    }
}