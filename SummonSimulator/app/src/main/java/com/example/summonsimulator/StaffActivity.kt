package com.example.summonsimulator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class StaffActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)

// TextView：組員名單
        val member1: TextView = findViewById(R.id.tvMember1)
        val member2: TextView = findViewById(R.id.tvMember2)
        val member3: TextView = findViewById(R.id.tvMember3)
        val member4: TextView = findViewById(R.id.tvMember4)

        member1.text = "趙宥程"
        member2.text = "趙健羽"
        member3.text = "翁帆侑"
        member4.text = "林暉恩"

// 返回主頁
        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}