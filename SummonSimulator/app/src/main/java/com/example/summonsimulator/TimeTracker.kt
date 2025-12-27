package com.example.summonsimulator

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object TimeTracker {
    private var startTime: Long = 0
    private var formattedStart: String = ""

    fun start() {
        startTime = System.currentTimeMillis()
        formattedStart = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun stop(context: Context, pageName: String) {
        if (startTime == 0L) return
        val duration = (System.currentTimeMillis() - startTime) / 1000
        SSDBHelper(context).insertUsageLog(pageName, formattedStart, duration)
        startTime = 0
    }
}