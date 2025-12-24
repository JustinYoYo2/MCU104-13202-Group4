package com.example.summonsimulator

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class GachaMusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    // Service 必須實作的方法，因為我們不需要與 Activity 綁定，所以回傳 null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("MusicService","") // 加入這行
        mediaPlayer = MediaPlayer.create(this, R.raw.bg2)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(1.0f, 1.0f)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 當啟動服務時開始播放
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
        // START_STICKY 表示如果系統因記憶體不足殺掉服務，會在資源足夠時嘗試重啟
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // 當呼叫 stopService 時，停止並釋放資源
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}