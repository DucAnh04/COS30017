package com.example.w8

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.emptyLongSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var writeFile: Button
    lateinit var editText: EditText
    lateinit var readFile: Button
    lateinit var txtInfor: TextView
    lateinit var clearFile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editText = findViewById(R.id.editTextText)
        writeFile = findViewById(R.id.writeFile)
        readFile = findViewById(R.id.readFile)
        clearFile = findViewById(R.id.clearFile)
        txtInfor = findViewById(R.id.txtInfor)

        writeFile.setOnClickListener {
            //Internal Storage
//            var file = openFileOutput("test.txt", Context.MODE_APPEND)
//            var message = editText.text.toString()
//            file.write(message.toByteArray())
//            file.close()
            //External Storage
            val file = File(Environment.getExternalStorageDirectory(), "/Documents/test.txt")
            file.createNewFile()
            file.writeText("Hello External Storage!") //Write file
        }

        readFile.setOnClickListener {
            //Internal Storage
            // Method 1
//            var file = openFileInput("test.txt")
//            val data = ByteArray(1024)
//            file.read(data)
//            txtInfor.text = data.toString(Charsets.UTF_8)
//            file.close()
            // Method 2
//            val file = File(applicationContext.filesDir, "test.txt")
//            val contents = file.readText() //Read file
//            txtInfor.text = contents

            //External Storage
            // Method 1
//            val file = File(Environment.getExternalStorageDirectory(), "/Documents/test.txt")
//            txtInfor.text = file.readText()
            //Method 2
            val file_b = File(Environment.getExternalStorageDirectory(), "/Documents/test.txt").exists()
            if (file_b) {
                val file = File(Environment.getExternalStorageDirectory(), "/Documents/test.txt")
                txtInfor.text = file.readText()
            }
            else
                Toast.makeText(this, "No exists", Toast.LENGTH_SHORT).show()
        }
        clearFile.setOnClickListener {
            //Internal Storage
            var file = openFileOutput("test.txt", Context.MODE_PRIVATE)
            file.close()
        }
    }
}