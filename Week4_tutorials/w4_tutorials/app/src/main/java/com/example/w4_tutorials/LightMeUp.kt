package com.example.w4_tutorials

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel

class LightMeUp : AppCompatActivity() {

    val imageViewModel: ImageViewModel by viewModels()

    companion object {
        private const val KEY_IMAGE = "IMAGE"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.light_me_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setImageDrawable(getDrawable(imageViewModel.state))

    //        savedInstanceState?.let {
    //            state = it.getInt(KEY_IMAGE)
    //            imageView.setImageDrawable(getDrawable(state))
    //        }

        imageView.setOnLongClickListener {
            //update the image state
            imageViewModel.nextImage()
            //then show the image
            imageView.setImageDrawable(getDrawable(imageViewModel.state))
            return@setOnLongClickListener false
        }
//        imageView.setOnClickListener {
//            state = when (state) {
//                R.drawable.assignment_turned_in_24dp_e8eaed -> R.drawable.assignment_late_24dp_e8eaed
//                R.drawable.assignment_late_24dp_e8eaed -> R.drawable.assignment_turned_in_24dp_e8eaed
//                else -> R.drawable.assignment_turned_in_24dp_e8eaed
//            }
//            imageView.setImageDrawable(getDrawable(state))
//
//        }

    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt(KEY_IMAGE, state)
//    }
}

class ImageViewModel: ViewModel() {
    // state is used to keep track of the current image state
    var state = R.drawable.assignment_turned_in_24dp_e8eaed

    fun nextImage(){
        state = when (state) {
            R.drawable.assignment_turned_in_24dp_e8eaed -> R.drawable.assignment_late_24dp_e8eaed
            R.drawable.assignment_late_24dp_e8eaed -> R.drawable.assignment_returned_24dp_e8eaed
            R.drawable.assignment_returned_24dp_e8eaed -> R.drawable.assignment_turned_in_24dp_e8eaed
            else -> R.drawable.assignment_turned_in_24dp_e8eaed
        }
    }
}