package com.example.w2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider



class loginActivity: AppCompatActivity(), View.OnClickListener {
    lateinit var btnLogin: Button
    lateinit var txtUser: EditText
    lateinit var txtPass: EditText

    lateinit var viewModel: SampleViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.loginlayout)

        viewModel = ViewModelProvider(this).get(SampleViewModel::class.java)
        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnLogin = findViewById<Button>(R.id.btnLogin)
        txtUser = findViewById<EditText>(R.id.txtUser)
        txtPass = findViewById<EditText>(R.id.txtPass)
        btnLogin.setOnClickListener(this)
        Log.d("Checking", "onCreate")
    }
    private fun observeViewModel(){
        viewModel.badgeCount.observe(this, Observer {
            showToast(it)
        })
    }
    internal fun showToast(value: Int){
        Toast.makeText(this, value.toString(), Toast.LENGTH_SHORT).show()
    }



    public override fun onStart() {
        super.onStart()
        Log.d("Checking", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Checking", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Checking", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Checking", "onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Checking", "onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Checking", "onDestroy")
    }

    override fun onClick(v: View?) {

    when (v?.id) {
        R.id.btnLogin -> {
            if (txtUser.text.toString() == "DA" && txtPass.text.toString() == "123") {
                viewModel.incrementBadgeCount()
//                Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show()
                val intent = Intent(this, AdminActivity::class.java)
                intent.putExtra("Username", txtUser.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Not Correct", Toast.LENGTH_SHORT).show()
            }
        }
    }
//    open class Animal(val name: String) {
//
//    }
//
//    class Razorback() : Animal("Razor") {
//        fun cheer() = "Go Swinburne!"
//    }
//
//    val r = Razorback()
//    println(r.cheer())
    }
}