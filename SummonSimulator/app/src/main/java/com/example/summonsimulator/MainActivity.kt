package com.example.summonsimulator

import android.media.MediaPlayer // 新增
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null // 宣告播放器

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化音樂播放器
        setupMusic()

        val viewPager = findViewById<ViewPager2>(R.id.viewPager2)
        viewPager.adapter = MainViewPagerAdapter(this)
        viewPager.setCurrentItem(1, false)
    }

    private fun setupMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bg1)
            mediaPlayer?.isLooping = true

            mediaPlayer?.setVolume(1.0f, 1.0f)
            mediaPlayer?.start()
        }
    }

    override fun onResume() {
        super.onResume()
        // 回到 App 時繼續播放 (例如從 LoginActivity 返回後)
        mediaPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        // 當 App 進入背景或跳轉到其他 Activity (如 LoginActivity) 時暫停
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 徹底關閉時釋放資源
        mediaPlayer?.release()
        mediaPlayer = null
    }
}