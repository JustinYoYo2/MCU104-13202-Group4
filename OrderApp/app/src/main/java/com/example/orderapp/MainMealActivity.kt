package com.example.orderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Button
import android.widget.RadioGroup

class MainMealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_meal)

        val radioGroup = findViewById<RadioGroup>(R.id.radioMain)
        val buttonDone = findViewById<Button>(R.id.btnDone)

        buttonDone.setOnClickListener {

            val selectedId = radioGroup.checkedRadioButtonId

            if (selectedId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val selectedMealText = selectedRadioButton.text.toString()
                MenuData.mainMeal = selectedMealText
            } else {
                MenuData.mainMeal = null
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}