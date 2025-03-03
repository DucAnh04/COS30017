package com.example.w5_tutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView

const val KEY_IMAGE = "image_key"

class MainActivity : AppCompatActivity() {
    private lateinit var image: ImageView

    override fun onStop() {
        super.onStop()
        Log.i("LIFECYCLE", "stopped")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById(R.id.imageView)

        savedInstanceState?.let {
            image.contentDescription = it.getString(KEY_IMAGE)
            updateImage(image.contentDescription.toString())
            Log.i("LIFECYCLE", "onRestoreInstanceState")
        }

        findViewById<Button>(R.id.station).setOnClickListener {
            updateImage("station")
        }

        findViewById<Button>(R.id.theatre).setOnClickListener {
            updateImage("theatre")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_IMAGE, image.contentDescription.toString())
        Log.i("LIFECYCLE", "onSaveInstanceState")
    }

    fun onClickCollege(v: View) {
        updateImage("college")
    }

    private fun updateImage(description: String) {
        val drawableRes = when (description) {
            "station" -> R.drawable.station
            "college" -> R.drawable.college
            "theatre" -> R.drawable.theatre
            else -> R.drawable.station // Assuming there's a default image
        }
        image.setImageDrawable(getDrawable(drawableRes))
        image.contentDescription = description
    }
}