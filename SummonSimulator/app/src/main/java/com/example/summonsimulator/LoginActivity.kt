package com.example.summonsimulator

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etAccount: EditText
    private lateinit var etPassword: EditText
    private lateinit var dbHelper: SSDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 初始化
        etAccount = findViewById(R.id.etAccount)
        etPassword = findViewById(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCancel = findViewById<Button>(R.id.btnCancel) // 新增取消按鈕

        dbHelper = SSDBHelper(this)

        // 登入驗證邏輯
        btnLogin.setOnClickListener {
            val account = etAccount.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (account == "G0rood" && password == "GS") {
                showStoneDialog()
            } else {
                Toast.makeText(this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show()
            }
        }

        // 取消返回邏輯
        btnCancel.setOnClickListener {
            finish() // 直接結束 Activity，回到 MainContentFragment
        }
    }

    private fun showStoneDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("修改石頭數量")
        builder.setMessage("請輸入")

        val input = EditText(this)
        input.hint = "輸入數量"
        input.inputType = InputType.TYPE_CLASS_NUMBER

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(60, 20, 60, 0)
        input.layoutParams = params
        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton("確定") { _, _ ->
            val stoneStr = input.text.toString().trim()
            if (stoneStr.isNotEmpty()) {
                try {
                    val stoneAmount = stoneStr.toInt()
                    if (stoneAmount in 0..10000000) {
                        if (dbHelper.resetStoneCount(stoneAmount)) {
                            Toast.makeText(this, "修改成功，新數額：$stoneAmount", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "資料庫更新失敗", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "請輸入有效範圍內的數字", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "格式錯誤", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("取消", null)
        builder.show()
    }
}