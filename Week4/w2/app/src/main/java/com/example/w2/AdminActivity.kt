package com.example.w2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

lateinit var txtAdmin: TextView
lateinit var btnExit: Button

class AdminActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.imagelayout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val intent = getIntent()
        val value = intent.getStringExtra("Username")

        txtAdmin = findViewById<TextView>(R.id.txtAdmin)
        txtAdmin.text = "Welcome $value"
        btnExit = findViewById(R.id.btnExit)
        btnExit.setOnClickListener(this)

        Toast.makeText(this, "Welcome $value", Toast.LENGTH_LONG).show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnExit -> {
                Log.d("Checking", "End Admin");
                finish()
            }
        }
    }
}
