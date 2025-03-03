package com.example.w8_tutorials

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val list = mutableListOf<MyWord>()
        resources.openRawResource(R.raw.input).bufferedReader()
            .forEachLine {
                val temp = it.split(",")
                list.add(MyWord(temp[0], temp[1].toInt()))
            }

        list.forEach(){
            Log.i("FILELINE","${it.word} -- ${it.num}")
        }
    }
}
data class MyWord(val word: String, val num: Int)