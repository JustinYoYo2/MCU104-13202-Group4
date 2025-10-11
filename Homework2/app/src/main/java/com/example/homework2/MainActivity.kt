package com.example.homework2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var phoneNumber: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val buttonIds = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9, R.id.button10, R.id.button12,
            R.id.buttonClear
        )
        phoneNumber = findViewById(R.id.PhoneNumber)

        val myListener = View.OnClickListener { v ->

            val currentText = phoneNumber.text.toString()

            when (v.id) {

                R.id.button12 -> {
                    if (currentText.isNotEmpty()) {
                        phoneNumber.setText(currentText.substring(0, currentText.length - 1))
                    }
                }

                R.id.buttonClear -> {
                    val phoneNumberText = phoneNumber.text.toString()

                    val intent = Intent(this@MainActivity, MainActivity2::class.java)

                    intent.putExtra("PHONE_NUMBER", phoneNumberText)

                    startActivity(intent)

                    phoneNumber.setText("")
                }

                else -> {
                    val button = v as Button
                    val buttonText = button.text.toString()
                    phoneNumber.append(buttonText)
                }
            }
        }

        // *** 關鍵步驟：使用迴圈將 myListener 註冊到所有按鈕上 ***
        for (id in buttonIds) {
            findViewById<Button>(id).setOnClickListener(myListener)
        }
    }
}