package com.example.homework2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Button

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textViewResult = findViewById<TextView>(R.id.textViewResult)

        // 接收 Intent 傳來的資料
        val receivedNumber = intent.getStringExtra("PHONE_NUMBER")

        if (receivedNumber != null && receivedNumber.isNotEmpty()) {
            textViewResult.text = "\n$receivedNumber"
        } else {
            textViewResult.text = "未輸入電話號碼"
        }
        val buttonHangUp = findViewById<Button>(R.id.buttonHangUpResult)

        buttonHangUp.setOnClickListener {
            finish()
        }
    }
}