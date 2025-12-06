package com.example.homework8

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 定義資料庫名稱、版本和表格名稱
class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "CarDB", null, 1) {

    companion object {
        private const val DB_NAME = "CarDB"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "CarTable"
        private const val COL_ID = "_id"
        private const val COL_BRAND = "brand"
        private const val COL_YEAR = "car_year"
        private const val COL_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_BRAND TEXT NOT NULL, " +
                "$COL_YEAR INTEGER NOT NULL, " +
                "$COL_PRICE INTEGER NOT NULL)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    val TABLE_CAR = TABLE_NAME
    val COLUMNS = arrayOf(COL_ID, COL_BRAND, COL_YEAR, COL_PRICE)
}