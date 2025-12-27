package com.example.summonsimulator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SSDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // 1. 資源表 (增加保底計數)
        val SQL_CREATE_RESOURCES_TABLE = "CREATE TABLE $TABLE_RESOURCES (" +
                "_id INTEGER PRIMARY KEY," +
                "$COL_STONE_COUNT INTEGER NOT NULL DEFAULT 0," +
                "$COL_PITY_COUNTER INTEGER NOT NULL DEFAULT 0" +
                ");"

        // 2. 設定表
        val SQL_CREATE_SETTINGS_TABLE = "CREATE TABLE $TABLE_SETTINGS (" +
                "_id INTEGER PRIMARY KEY," +
                "$COL_RATE_1_STAR REAL NOT NULL," +
                "$COL_RATE_2_STAR REAL NOT NULL," +
                "$COL_RATE_3_STAR REAL NOT NULL," +
                "$COL_RATE_FOCUS REAL NOT NULL," +
                "$COL_PITY_COUNT INTEGER NOT NULL," +
                "$COL_COST_SINGLE INTEGER NOT NULL," +
                "$COL_COST_TEN INTEGER NOT NULL" +
                ");"

        // 3. 使用者紀錄表 (TimeTracker 使用)
        val SQL_CREATE_LOGS_TABLE = """
            CREATE TABLE $TABLE_USER_LOGS (
                $COL_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PAGE_NAME TEXT NOT NULL,
                $COL_START_TIME TEXT NOT NULL,
                $COL_DURATION INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(SQL_CREATE_RESOURCES_TABLE)
        db.execSQL(SQL_CREATE_SETTINGS_TABLE)
        db.execSQL(SQL_CREATE_LOGS_TABLE)
        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_RESOURCES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_LOGS")
            onCreate(db)
        }
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val resourceValues = ContentValues().apply {
            put("_id", 1)
            put(COL_STONE_COUNT, 1000)
            put(COL_PITY_COUNTER, 0)
        }
        db.insert(TABLE_RESOURCES, null, resourceValues)

        val settingValues = ContentValues().apply {
            put("_id", 1)
            put(COL_RATE_1_STAR, 87.0)
            put(COL_RATE_2_STAR, 10.0)
            put(COL_RATE_3_STAR, 3.0)
            put(COL_RATE_FOCUS, 50.0)
            put(COL_PITY_COUNT, 90)
            put(COL_COST_SINGLE, 160)
            put(COL_COST_TEN, 1600)
        }
        db.insert(TABLE_SETTINGS, null, settingValues)
    }

    // --- 石頭與保底方法 ---
    fun getStoneCount(): Int {
        val db = this.readableDatabase
        var count = 0
        val cursor = db.rawQuery("SELECT $COL_STONE_COUNT FROM $TABLE_RESOURCES WHERE _id = 1", null)
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }

    fun updateStoneCount(delta: Int): Boolean {
        val db = this.writableDatabase
        val newCount = getStoneCount() + delta
        if (newCount < 0 && delta < 0) return false
        val values = ContentValues().apply { put(COL_STONE_COUNT, newCount) }
        return db.update(TABLE_RESOURCES, values, "_id = 1", null) == 1
    }

    fun resetStoneCount(newAmount: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COL_STONE_COUNT, newAmount) }
        return db.update(TABLE_RESOURCES, values, "_id = 1", null) == 1
    }

    fun getPityCounter(): Int {
        val db = this.readableDatabase
        var count = 0
        val cursor = db.rawQuery("SELECT $COL_PITY_COUNTER FROM $TABLE_RESOURCES WHERE _id = 1", null)
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }

    fun updatePityCounter(count: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply { put(COL_PITY_COUNTER, count) }
        return db.update(TABLE_RESOURCES, values, "_id = 1", null) == 1
    }

    // --- 設定檔讀寫 ---
    fun getGachaSettings(): GachaSettings? {
        val db = this.readableDatabase
        var settings: GachaSettings? = null
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SETTINGS WHERE _id = 1", null)
        val currentStones = getStoneCount()

        if (cursor.moveToFirst()) {
            settings = GachaSettings(
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RATE_1_STAR)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RATE_2_STAR)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RATE_3_STAR)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RATE_FOCUS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_PITY_COUNT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_COST_SINGLE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_COST_TEN)),
                currentStones
            )
        }
        cursor.close()
        return settings
    }

    fun updateGachaSettings(s: GachaSettings): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_RATE_1_STAR, s.rate1Star)
            put(COL_RATE_2_STAR, s.rate2Star)
            put(COL_RATE_3_STAR, s.rate3Star)
            put(COL_RATE_FOCUS, s.rateFocus)
            put(COL_PITY_COUNT, s.pityCount)
            put(COL_COST_SINGLE, s.costSingle)
            put(COL_COST_TEN, s.costTen)
        }
        return db.update(TABLE_SETTINGS, values, "_id = 1", null) == 1
    }

    // --- 使用紀錄 ---
    fun insertUsageLog(pageName: String, startTime: String, duration: Long) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_PAGE_NAME, pageName)
            put(COL_START_TIME, startTime)
            put(COL_DURATION, duration)
        }
        db.insert(TABLE_USER_LOGS, null, values)
    }

    companion object {
        private const val DATABASE_NAME = "gacha_simulator.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_RESOURCES = "Resources"
        const val COL_STONE_COUNT = "stone_count"
        const val COL_PITY_COUNTER = "pity_counter"

        const val TABLE_SETTINGS = "Settings"
        const val COL_RATE_1_STAR = "rate_1_star"
        const val COL_RATE_2_STAR = "rate_2_star"
        const val COL_RATE_3_STAR = "rate_3_star"
        const val COL_RATE_FOCUS = "rate_focus"
        const val COL_PITY_COUNT = "pity_count"
        const val COL_COST_SINGLE = "cost_single"
        const val COL_COST_TEN = "cost_ten"

        const val TABLE_USER_LOGS = "UserLogs"
        const val COL_LOG_ID = "_id"
        const val COL_PAGE_NAME = "page_name"
        const val COL_START_TIME = "start_time"
        const val COL_DURATION = "duration_seconds"
    }
}