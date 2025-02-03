package com.example.w4_tutorials

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CaculatorActivity : AppCompatActivity() {
    var opResult: Int = 0
    var operator = "plus"

    override fun onStart() {
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
    override fun onDestroy() {
        super.onDestroy()
        Log.d("Checking", "onDestroy")
    }
    override fun onRestart() {
        super.onRestart()
        Log.d("LIFECYCLE", "onRestart")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.caculator_layout)
        Log.d("Checking", "onCreate")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val number1 = findViewById<EditText>(R.id.number1)
        val number2 = findViewById<EditText>(R.id.number2)
        val answer = findViewById<TextView>(R.id.answer)

        savedInstanceState?.let {
            opResult = it.getInt("Answer")
            answer.text = opResult.toString()
        }

        val equals = findViewById<Button>(R.id.equals)
        equals.setOnClickListener {
            if (number1.text.toString().isEmpty() || number2.text.toString().isEmpty()) {
                return@setOnClickListener
            }
            opResult = when (operator) {
                "plus" -> add(number1.text.toString(), number2.text.toString())
                "mult" -> mult(number1.text.toString(), number2.text.toString())
                else -> add(number1.text.toString(), number2.text.toString())
            }
            // TODO: show result on the screen

            answer.text = opResult.toString()
        }
    }
    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioPlus ->
                    if (checked) {
                        operator ="plus"
                    }
                R.id.radioMult ->
                    if (checked) {
                        operator ="mult"
                    }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("Answer", opResult)
        Log.d("Checking", "onSaveInstanceState $opResult")
    }

    // Add and mult
    private fun add(number1: String, number2: String):Int = number1.toInt() + number2.toInt()
    private fun mult(number1: String, number2: String):Int = number1.toInt() * number2.toInt()

}