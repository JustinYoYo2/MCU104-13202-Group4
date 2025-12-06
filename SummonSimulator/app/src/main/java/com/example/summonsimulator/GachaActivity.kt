package com.example.summonsimulator

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.util.ArrayList
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
// 確保您已刪除或註解與 VideoView 相關的程式碼和 import

class GachaActivity : AppCompatActivity() {

    // --- 介面元件 ---
    private lateinit var backButton: Button
    private lateinit var stoneCountTextView: TextView
    private lateinit var singleDrawButton: Button
    private lateinit var tenDrawButton: Button
    private lateinit var slideshowImageView: ImageView // 幻燈片 View

    // --- 抽卡邏輯 ---
    private lateinit var gachaManager: GachaManager

    // --- 幻燈片相關屬性 ---
    private val SLIDE_DELAY_MS = 2500L // 3 秒切換
    private val SLIDE_FADE_DURATION = 2000L // 淡入淡出持續時間 0.5 秒

    // 🌟 請確保這些圖片資源存在於 res/drawable 資料夾中
    private val slideImages = listOf(
        R.drawable.mry4,
        R.drawable.rin4,
    )
    private var currentSlideIndex = 0
    private val slideshowHandler = Handler(Looper.getMainLooper())
    private lateinit var slideshowRunnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gacha)

        // 介面元素初始化
        backButton = findViewById(R.id.button_back)
        stoneCountTextView = findViewById(R.id.text_stone_count)
        singleDrawButton = findViewById(R.id.button_single_draw)
        tenDrawButton = findViewById(R.id.button_ten_draw)
        slideshowImageView = findViewById(R.id.gacha_slideshow_view)
        // ⚠️ 已修正：刪除重複的 findViewById 呼叫

        // 初始化 GachaManager
        gachaManager = GachaManager(this)

        // 首次載入時更新
        updateGachaInfoDisplay()

        // 設定按鈕行為
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 🌟 按鈕行為：點擊時顯示確認視窗
        singleDrawButton.setOnClickListener {
            showConfirmationDialog(1)
        }

        tenDrawButton.setOnClickListener {
            showConfirmationDialog(10)
        }

        // 🌟 啟動幻燈片功能
        setupSlideshow()
    }

    // --- 幻燈片輪播邏輯 ---

    /**
     * 設定圖片幻燈片輪播的邏輯。
     */
    private fun setupSlideshow() {
        // 設置 slideshowRunnable
        slideshowRunnable = Runnable {
            // 1. 執行淡出動畫
            val fadeOut = AlphaAnimation(1.0f, 0.0f).apply {
                duration = SLIDE_FADE_DURATION
                fillAfter = true // 保持動畫結束時的狀態 (透明)
            }

            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    // 2. 淡出完成：切換圖片
                    currentSlideIndex = (currentSlideIndex + 1) % slideImages.size
                    slideshowImageView.setImageResource(slideImages[currentSlideIndex])

                    // 3. 執行淡入動畫
                    val fadeIn = AlphaAnimation(0.0f, 1.0f).apply {
                        duration = SLIDE_FADE_DURATION
                    }
                    slideshowImageView.startAnimation(fadeIn)

                    // 4. 設定下一次輪播 (在 SLIDE_DELAY_MS 之後再次執行 Runnable)
                    slideshowHandler.postDelayed(slideshowRunnable, SLIDE_DELAY_MS)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })

            slideshowImageView.startAnimation(fadeOut)
        }

        // 啟動第一次輪播
        // 確保 Activity 載入時先顯示第一張圖片
        slideshowImageView.setImageResource(slideImages[currentSlideIndex])
        // 第一次延遲後開始執行淡出
        slideshowHandler.postDelayed(slideshowRunnable, SLIDE_DELAY_MS)
    }

    /**
     * 處理 Activity 生命周期：暫停和恢復輪播。
     */
    override fun onPause() {
        super.onPause()
        // 暫停輪播以避免記憶體洩漏
        slideshowHandler.removeCallbacks(slideshowRunnable)
    }

    override fun onResume() {
        super.onResume()
        // 恢復輪播
        if (::slideshowRunnable.isInitialized) {
            slideshowHandler.postDelayed(slideshowRunnable, SLIDE_DELAY_MS)
        }
    }

    // --- 抽卡與 UI 邏輯 ---

    /**
     * 更新畫面上的石頭數量、按鈕花費和保底計數
     */
    private fun updateGachaInfoDisplay() {
        val count = gachaManager.getStoneCount()
        stoneCountTextView.text = "石頭數量: $count"

        // 更新按鈕文字以顯示花費
        val settings = SSDBHelper(this).getGachaSettings()
        if (settings != null) {
            singleDrawButton.text = "一抽 (${settings.getCostSingle()} 石)"
            tenDrawButton.text = "十抽 (${settings.getCostTen()} 石)"
        }
    }

    /**
     * 顯示抽卡確認對話框。
     * @param count 抽卡次數 (1或10)
     */
    private fun showConfirmationDialog(count: Int) {
        val settings = SSDBHelper(this).getGachaSettings()
        val cost = if (count == 1) settings?.getCostSingle() ?: 0 else settings?.getCostTen() ?: 0

        val title = if (count == 1) "確認單抽" else "確認十抽"
        val message = "確定花費 $cost 顆石頭進行 $count 次召喚嗎？"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("確認") { dialog, which ->
                performGachaDraw(count)
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * 執行抽卡操作的核心函數
     * @param count 抽卡次數 (1或10)
     */
    private fun performGachaDraw(count: Int) {
        val results = gachaManager.performDraw(count)

        if (results == null) {
            // 石頭不足
            Toast.makeText(this, "石頭不足，無法抽卡！", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. 更新畫面顯示 (顯示新的石頭數量)
        updateGachaInfoDisplay()

        // 3. 準備跳轉到 ResultActivity
        val resultsStringList = ArrayList(results.map { it.getResultDescription() })

        val intent = Intent(this, ResultActivity::class.java).apply {
            // 傳遞抽卡結果的字串列表
            putStringArrayListExtra("EXTRA_GACHA_RESULTS", resultsStringList)
            // 傳遞當前距離保底的次數
            putExtra("EXTRA_PITY_COUNT", gachaManager.getDrawCountSinceLast3Star())
        }

        // 4. 跳轉到 ResultActivity
        startActivity(intent)
    }

    /**
     * 模擬播放抽卡動畫 (已棄用，使用幻燈片)
     */
    private fun playGachaAnimation() {
        // 由於使用幻燈片，此函數內容為空
    }
}