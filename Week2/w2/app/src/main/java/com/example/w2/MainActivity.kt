package com.example.w2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

lateinit var btnOK: Button
lateinit var btnCancel: Button
lateinit var txtName: EditText
lateinit var txtNum1: EditText
lateinit var txtNum2: EditText
lateinit var btnPlus: Button
lateinit var btnSub: Button
lateinit var btnMult: Button
lateinit var btnDiv: Button
lateinit var txtResult: TextView

class Car(var brand: String, var model: String, var year: Int) {
    fun GetBrand() = brand
}

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnOK = findViewById<Button>(R.id.btnOK)
        btnCancel = findViewById<Button>(R.id.btnCancel)
        txtName = findViewById<EditText>(R.id.txtName)

        btnOK.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        txtNum1 = findViewById<EditText>(R.id.txtNum1)
        txtNum2 = findViewById<EditText>(R.id.txtNum2)
        btnPlus = findViewById<Button>(R.id.btnPlus)
        btnSub = findViewById<Button>(R.id.btnSub)
        btnMult = findViewById<Button>(R.id.btnMult)
        btnDiv = findViewById<Button>(R.id.btnDiv)
        txtResult = findViewById<TextView>(R.id.txtResult)
        btnPlus.setOnClickListener(this)
        btnSub.setOnClickListener(this)
        btnMult.setOnClickListener(this)

        btnDiv.setOnClickListener(this)
    }

    fun test1(a: Int, b: Int) = a + b
    fun test2(a: Int, b: Int): Int {
        return a + b
    }

    override fun onClick(v: View) {
        val text = txtName.text.toString()
        val num1 = txtNum1.text.toString().toDouble()
        val num2 = txtNum2.text.toString().toDouble()
        val result: Double
        when (v.id) {
            R.id.btnOK -> {
//                val cars = arrayOf("Toyota", "Honda", "Ford")
//                var s = ""
//                checkpoint@ for (i in cars.indices){
//                    if(cars.get(i) == "Honda") break@checkpoint
//                    s += cars.get(i)
//                }
//                checkpoint@ for (i in cars.indices){
//                    if(cars.get(i) == "Honda") continue@checkpoint
//                    s += cars.get(i)
//                }
//                txtName.setText(cars.joinToString(","))
//                txtName.setText(s)
//                txtName.setText(test2(1,2).toString())
//                val cl = Car( "Ford", "Mustang", 2021)
//                txtName.setText(cl.GetBrand())
                txtName.setText("Hello $text")
            }

            R.id.btnCancel -> {
                txtName.setText("Cancel")
            }

            R.id.btnPlus -> {
                result = num1 + num2
                txtResult.setText("Result: $result")
            }

            R.id.btnSub -> {
                result = num1 - num2
                txtResult.setText("Result: $result")
            }

            R.id.btnMult -> {
                result = num1 * num2
                txtResult.setText("Result: $result")
            }

            R.id.btnDiv -> {
                result = if (num2 != 0.0) num1 / num2 else Double.NaN
                txtResult.setText("Result: $result")
            }
        }
    }
}