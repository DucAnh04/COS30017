package com.example.w3_tutorial

import android.os.Bundle
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
    var operator = "plus"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.caculator_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val number1 = findViewById<EditText>(R.id.number1)
        val number2 = findViewById<EditText>(R.id.number2)
        val answer = findViewById<TextView>(R.id.answer)

        val equals = findViewById<Button>(R.id.equals)
        equals.setOnClickListener {
            val result = when (operator) {
                "plus" -> add(number1.text.toString(), number2.text.toString())
                "mult" -> mult(number1.text.toString(), number2.text.toString())
                else -> add(number1.text.toString(), number2.text.toString())
            }
            // TODO: show result on the screen

            answer.text = result.toString()
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
    private fun add(number1: String, number2: String):Int = number1.toInt() + number2.toInt()
    private fun mult(number1: String, number2: String):Int = number1.toInt() * number2.toInt()

}