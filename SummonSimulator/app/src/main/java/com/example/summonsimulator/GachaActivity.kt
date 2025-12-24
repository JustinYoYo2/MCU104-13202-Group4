package com.example.summonsimulator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList

class GachaActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var stoneCountTextView: TextView
    private lateinit var singleDrawButton: Button
    private lateinit var tenDrawButton: Button
    private lateinit var slideshowImageView: ImageView
    private lateinit var gachaManager: GachaManager

    private val SLIDE_DELAY_MS = 2500L
    private val SLIDE_FADE_DURATION = 2000L
    private val slideImages = listOf(R.drawable.mry4, R.drawable.rin4)
    private var currentSlideIndex = 0
    private val slideshowHandler = Handler(Looper.getMainLooper())
    private lateinit var slideshowRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gacha)

        // 🌟 啟動 bg2 音樂服務
        startService(Intent(this, GachaMusicService::class.java))

        backButton = findViewById(R.id.button_back)
        stoneCountTextView = findViewById(R.id.text_stone_count)
        singleDrawButton = findViewById(R.id.button_single_draw)
        tenDrawButton = findViewById(R.id.button_ten_draw)
        slideshowImageView = findViewById(R.id.gacha_slideshow_view)

        gachaManager = GachaManager(this)
        updateGachaInfoDisplay()

        // 🌟 返回按鈕：停止 bg2，回到 MainActivity 播放 bg1
        backButton.setOnClickListener {
            stopService(Intent(this, GachaMusicService::class.java))
            finish()
        }

        singleDrawButton.setOnClickListener { showConfirmationDialog(1) }
        tenDrawButton.setOnClickListener { showConfirmationDialog(10) }

        setupSlideshow()
    }

    private fun setupSlideshow() {
        slideshowRunnable = Runnable {
            val fadeOut = AlphaAnimation(1.0f, 0.0f).apply {
                duration = SLIDE_FADE_DURATION
                fillAfter = true
            }
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    currentSlideIndex = (currentSlideIndex + 1) % slideImages.size
                    slideshowImageView.setImageResource(slideImages[currentSlideIndex])
                    val fadeIn = AlphaAnimation(0.0f, 1.0f).apply { duration = SLIDE_FADE_DURATION }
                    slideshowImageView.startAnimation(fadeIn)
                    slideshowHandler.postDelayed(slideshowRunnable, SLIDE_DELAY_MS)
                }
                override fun onAnimationRepeat(animation: Animation) {}
            })
            slideshowImageView.startAnimation(fadeOut)
        }
        slideshowImageView.setImageResource(slideImages[currentSlideIndex])
        slideshowHandler.postDelayed(slideshowRunnable, SLIDE_DELAY_MS)
    }

    override fun onPause() {
        super.onPause()
        slideshowHandler.removeCallbacks(slideshowRunnable)
    }

    override fun onResume() {
        super.onResume()
        if (::slideshowRunnable.isInitialized) {
            slideshowHandler.postDelayed(slideshowRunnable, SLIDE_DELAY_MS)
        }
    }

    private fun updateGachaInfoDisplay() {
        val count = gachaManager.getStoneCount()
        stoneCountTextView.text = "石頭數量: $count"
        val settings = SSDBHelper(this).getGachaSettings()
        if (settings != null) {
            singleDrawButton.text = "一抽 (${settings.getCostSingle()} 石)"
            tenDrawButton.text = "十抽 (${settings.getCostTen()} 石)"
        }
    }

    private fun showConfirmationDialog(count: Int) {
        val settings = SSDBHelper(this).getGachaSettings()
        val cost = if (count == 1) settings?.getCostSingle() ?: 0 else settings?.getCostTen() ?: 0
        AlertDialog.Builder(this)
            .setTitle(if (count == 1) "確認單抽" else "確認十抽")
            .setMessage("確定花費 $cost 顆石頭進行 $count 次召喚嗎？")
            .setPositiveButton("確認") { _, _ -> performGachaDraw(count) }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun performGachaDraw(count: Int) {
        val results = gachaManager.performDraw(count)
        if (results == null) {
            Toast.makeText(this, "石頭不足！", Toast.LENGTH_SHORT).show()
            return
        }
        updateGachaInfoDisplay()
        val resultsStringList = ArrayList(results.map { it.getResultDescription() })
        val intent = Intent(this, ResultActivity::class.java).apply {
            putStringArrayListExtra("EXTRA_GACHA_RESULTS", resultsStringList)
            putExtra("EXTRA_PITY_COUNT", gachaManager.getDrawCountSinceLast3Star())
        }
        startActivity(intent)
    }
}