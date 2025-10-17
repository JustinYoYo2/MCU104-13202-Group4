package com.example.orderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup

class SideDishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_side_dish)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupSides)
        val buttonDone = findViewById<Button>(R.id.btnDone)

        buttonDone.setOnClickListener {

            val selectedId = radioGroup.checkedRadioButtonId

            if (selectedId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val selectedMealText = selectedRadioButton.text.toString()
                MenuData.sideDishes = selectedMealText
            } else {
                MenuData.sideDishes = null
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}