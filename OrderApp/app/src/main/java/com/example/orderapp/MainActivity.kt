package com.example.orderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonMain = findViewById<Button>(R.id.btnChooseMainMeal)
        buttonMain.setOnClickListener {
            val intent = Intent(this, MainMealActivity::class.java)
            startActivity(intent)
        }
        val buttonSide = findViewById<Button>(R.id.btnChooseSideDishes)
        buttonSide.setOnClickListener {
            val intent = Intent(this, SideDishActivity::class.java)
            startActivity(intent)
        }
        val buttonDrink = findViewById<Button>(R.id.btnChooseDrink)
        buttonDrink.setOnClickListener {
            val intent = Intent(this, DrinkActivity::class.java)
            startActivity(intent)
        }
        val resultTextView = findViewById<TextView>(R.id.tvSelection)
        val summaryText = """
            Main Meal: ${MenuData.mainMeal}
            Side Dishes: ${MenuData.sideDishes}
            Drink: ${MenuData.drink}
        """.trimIndent()

        resultTextView.text = summaryText

        val buttonOrder = findViewById<Button>(R.id.btnOrder)
        buttonOrder.setOnClickListener {
            val intent = Intent(this, FinalActivity::class.java)
            startActivity(intent)
        }
    }
}
