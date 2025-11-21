package com.example.summonsimulator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.summonsimulator.GachaSettings

class SSDBHelper
    (context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_RESOURCES_TABLE = "CREATE TABLE " +
                TABLE_RESOURCES + " (" +
                "_id INTEGER PRIMARY KEY," +
                COL_STONE_COUNT + " INTEGER NOT NULL DEFAULT 0" +
                ");"

        val SQL_CREATE_SETTINGS_TABLE = "CREATE TABLE " +
                TABLE_SETTINGS + " (" +
                "_id INTEGER PRIMARY KEY," +
                COL_RATE_1_STAR + " REAL NOT NULL," +
                COL_RATE_2_STAR + " REAL NOT NULL," +
                COL_RATE_3_STAR + " REAL NOT NULL," +
                COL_RATE_FOCUS + " REAL NOT NULL," +
                COL_PITY_COUNT + " INTEGER NOT NULL," +
                COL_COST_SINGLE + " INTEGER NOT NULL," +
                COL_COST_TEN + " INTEGER NOT NULL" +
                ");"

        db.execSQL(SQL_CREATE_RESOURCES_TABLE)
        db.execSQL(SQL_CREATE_SETTINGS_TABLE)
        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESOURCES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val resourceValues = ContentValues()
        resourceValues.put("_id", 1)
        resourceValues.put(COL_STONE_COUNT, 1000)
        db.insert(TABLE_RESOURCES, null, resourceValues)

        val settingValues = ContentValues()
        settingValues.put("_id", 1)
        settingValues.put(COL_RATE_1_STAR, 87.0)
        settingValues.put(COL_RATE_2_STAR, 10.0)
        settingValues.put(COL_RATE_3_STAR, 3.0)
        settingValues.put(COL_RATE_FOCUS, 50.0)
        settingValues.put(COL_PITY_COUNT, 90)
        settingValues.put(COL_COST_SINGLE, 160)
        settingValues.put(COL_COST_TEN, 1600)
        db.insert(TABLE_SETTINGS, null, settingValues)
    }

    // 讀取當前石頭數量
    fun getStoneCount(): Int {
        val db = this.readableDatabase
        var stoneCount = 0
        val query = "SELECT $COL_STONE_COUNT FROM $TABLE_RESOURCES WHERE _id = 1"

        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                stoneCount = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STONE_COUNT))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return stoneCount
    }

    // 更新石頭數量
    fun updateStoneCount(delta: Int): Boolean {
        val db = this.writableDatabase

        // 讀取當前數量並計算新數量
        val currentCount = getStoneCount()
        val newCount = currentCount + delta

        // 檢查：如果嘗試扣除導致負數，則不允許 (通常在 GachaActivity 中先檢查)
        if (newCount < 0 && delta < 0) {
            return false
        }

        val values = ContentValues()
        values.put(COL_STONE_COUNT, newCount)

        val rowsAffected = db.update(
            TABLE_RESOURCES,
            values,
            "_id = ?",
            arrayOf("1")
        )
        return rowsAffected == 1
    }

    fun getGachaSettings(): GachaSettings? {
        val db = this.readableDatabase
        var settings: GachaSettings? = null

        val settingsQuery = "SELECT * FROM $TABLE_SETTINGS WHERE _id = 1"
        var settingsCursor: Cursor? = null

        val stoneCount = getStoneCount()

        try {
            settingsCursor = db.rawQuery(settingsQuery, null)
            if (settingsCursor.moveToFirst()) {
                val r1 = settingsCursor.getDouble(settingsCursor.getColumnIndexOrThrow(COL_RATE_1_STAR))
                val r2 = settingsCursor.getDouble(settingsCursor.getColumnIndexOrThrow(COL_RATE_2_STAR))
                val r3 = settingsCursor.getDouble(settingsCursor.getColumnIndexOrThrow(COL_RATE_3_STAR))
                val rF = settingsCursor.getDouble(settingsCursor.getColumnIndexOrThrow(COL_RATE_FOCUS))
                val pity = settingsCursor.getInt(settingsCursor.getColumnIndexOrThrow(COL_PITY_COUNT))
                val cS = settingsCursor.getInt(settingsCursor.getColumnIndexOrThrow(COL_COST_SINGLE))
                val cT = settingsCursor.getInt(settingsCursor.getColumnIndexOrThrow(COL_COST_TEN))

                settings = GachaSettings(r1, r2, r3, rF, pity, cS, cT, stoneCount)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            settingsCursor?.close()
        }
        return settings
    }

    // 更新所有設定
    fun updateGachaSettings(newSettings: GachaSettings): Boolean {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COL_RATE_1_STAR, newSettings.getRate1Star())
        values.put(COL_RATE_2_STAR, newSettings.getRate2Star())
        values.put(COL_RATE_3_STAR, newSettings.getRate3Star())
        values.put(COL_RATE_FOCUS, newSettings.getRateFocus())
        values.put(COL_PITY_COUNT, newSettings.getPityCount())
        values.put(COL_COST_SINGLE, newSettings.getCostSingle())
        values.put(COL_COST_TEN, newSettings.getCostTen())

        val rowsAffected = db.update(
            TABLE_SETTINGS,
            values,
            "_id = ?",
            arrayOf("1")
        )
        return rowsAffected == 1
    }

    companion object {
        private const val DATABASE_NAME = "gacha_simulator.db"
        private const val DATABASE_VERSION = 1

        //儲存石頭數量
        const val TABLE_RESOURCES: String = "Resources"
        const val COL_STONE_COUNT: String = "stone_count"

        //儲存所有機率和消耗設定
        const val TABLE_SETTINGS: String = "Settings"
        const val COL_RATE_1_STAR: String = "rate_1_star"
        const val COL_RATE_2_STAR: String = "rate_2_star"
        const val COL_RATE_3_STAR: String = "rate_3_star"
        const val COL_RATE_FOCUS: String = "rate_focus"
        const val COL_PITY_COUNT: String = "pity_count"
        const val COL_COST_SINGLE: String = "cost_single"
        const val COL_COST_TEN: String = "cost_ten"
    }
}