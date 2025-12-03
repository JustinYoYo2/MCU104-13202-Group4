package com.example.summonsimulator

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView


class GachaActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var stoneCountTextView: TextView
    private lateinit var gachaAnimationView: VideoView
    private lateinit var singleDrawButton: Button
    private lateinit var tenDrawButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gacha)

        backButton = findViewById(R.id.button_back)
        stoneCountTextView = findViewById(R.id.text_stone_count)
        gachaAnimationView = findViewById(R.id.gacha_animation_view)
        singleDrawButton = findViewById(R.id.button_single_draw)
        tenDrawButton = findViewById(R.id.button_ten_draw)

        // 設定左上角返回按鈕的行為
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // TODO: 在這裡實作您的抽卡邏輯、石頭數量更新和動畫播放

        // 範例: 抽卡按鈕行為
        singleDrawButton.setOnClickListener {
            performGachaDraw(1)
        }

        tenDrawButton.setOnClickListener {
            performGachaDraw(10)
        }
    }

    /**
     * 執行抽卡操作的核心函數
     * @param count 抽卡次數 (1或10)
     */
    private fun performGachaDraw(count: Int) {
        // 1. 檢查石頭數量是否足夠 (TODO: 需要連接到您的 ViewModel/Repository)
        // 2. 扣除石頭數量
        // 3. 播放抽卡動畫 (TODO: 載入並播放 gachaAnimationView 的影片)
        // 4. 執行機率運算 (TODO: 根據設定的機率產生結果)
        // 5. 顯示抽卡結果

        stoneCountTextView.text = "石頭數量: XXXX" // 範例: 更新數量顯示

        // 可以在這裡呼叫 ViewModel 處理 Gacha 邏輯
    }

    // TODO: 您可能還需要一個函數來處理 VideoView 的播放、停止和重設
}