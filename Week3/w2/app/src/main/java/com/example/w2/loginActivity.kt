package com.example.w2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

lateinit var btnLogin: Button
lateinit var txtUser: EditText
lateinit var txtPass: EditText

class loginActivity: AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.loginlayout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnLogin = findViewById<Button>(R.id.btnLogin)
        txtUser = findViewById<EditText>(R.id.txtUser)
        txtPass = findViewById<EditText>(R.id.txtPass)
        btnLogin.setOnClickListener(this)
    }



override fun onClick(v: View?) {
    when (v?.id) {
        R.id.btnLogin -> {
            if (txtUser.text.toString() == "DA" && txtPass.text.toString() == "123") {
                Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show()
                var intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Not Correct", Toast.LENGTH_LONG).show()
            }
        }
    }
    class Animal(val name: String) {

    }

    class Razorback() : Animal("Razor") {
        fun cheer() = "Go Swinburne!"
    }

    val r = Razorback()
    println(r.cheer())
}
}