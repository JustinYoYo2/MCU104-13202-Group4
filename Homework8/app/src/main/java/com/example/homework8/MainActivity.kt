package com.example.homework8

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbrw: SQLiteDatabase
    private lateinit var adapter: ArrayAdapter<String>
    private val items = ArrayList<String>()

    private lateinit var edBrand: EditText
    private lateinit var edYear: EditText
    private lateinit var edPrice: EditText
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbrw = MyDBHelper(this).writableDatabase

        edBrand = findViewById(R.id.edBrand)
        edYear = findViewById(R.id.edYear)
        edPrice = findViewById(R.id.edPrice)
        listView = findViewById(R.id.listView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        queryAll()

        findViewById<Button>(R.id.btnInsert).setOnClickListener { insert() }
        findViewById<Button>(R.id.btnUpdate).setOnClickListener { update() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { delete() }
        findViewById<Button>(R.id.btnQuery).setOnClickListener { query() }

        listView.setOnItemClickListener { _, _, pos, _ ->
            val item = items[pos]
            val brand = item.split(",")[0].split(":")[1].trim()
            edBrand.setText(brand)

            edYear.setText("")
            edPrice.setText("")
        }
    }

    private fun insert() {
        val brand = edBrand.text.toString()
        val year = edYear.text.toString()
        val price = edPrice.text.toString()

        if (brand.isEmpty() || year.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "廠牌、年份、價格皆不可為空", Toast.LENGTH_SHORT).show()
            return
        }

        val values = ContentValues().apply {
            put("brand", brand)
            put("car_year", year.toInt())
            put("price", price.toInt())
        }

        val id = dbrw.insert("CarTable", null, values)
        if (id > 0) {
            Toast.makeText(this, "新增成功：${brand}", Toast.LENGTH_SHORT).show()
            edBrand.setText("")
            edYear.setText("")
            edPrice.setText("")
            queryAll() // 重新查詢顯示最新資料
        } else {
            Toast.makeText(this, "新增失敗", Toast.LENGTH_SHORT).show()
        }
    }

    private fun update() {
        val brand = edBrand.text.toString()
        val newYear = edYear.text.toString()
        val newPrice = edPrice.text.toString()

        if (brand.isEmpty()) {
            Toast.makeText(this, "請輸入要修改的廠牌", Toast.LENGTH_SHORT).show()
            return
        }
        if (newYear.isEmpty() && newPrice.isEmpty()) {
            Toast.makeText(this, "請至少輸入要修改的年份或價格", Toast.LENGTH_SHORT).show()
            return
        }

        val values = ContentValues().apply {
            if (newYear.isNotEmpty()) put("car_year", newYear.toInt())
            if (newPrice.isNotEmpty()) put("price", newPrice.toInt())
        }

        val rowsAffected = dbrw.update(
            "CarTable",
            values,
            "brand LIKE ?",
            arrayOf(brand)
        )

        if (rowsAffected > 0) {
            Toast.makeText(this, "修改成功：影響 ${rowsAffected} 筆資料", Toast.LENGTH_SHORT).show()
            edBrand.setText("")
            edYear.setText("")
            edPrice.setText("")
            queryAll()
        } else {
            Toast.makeText(this, "修改失敗，找不到該廠牌資料", Toast.LENGTH_SHORT).show()
        }
    }

    private fun delete() {
        val brand = edBrand.text.toString()

        if (brand.isEmpty()) {
            Toast.makeText(this, "請輸入要刪除的廠牌", Toast.LENGTH_SHORT).show()
            return
        }

        val rowsAffected = dbrw.delete(
            "CarTable",
            "brand LIKE ?",
            arrayOf(brand)
        )

        if (rowsAffected > 0) {
            Toast.makeText(this, "刪除成功：影響 ${rowsAffected} 筆資料", Toast.LENGTH_SHORT).show()
            edBrand.setText("")
            queryAll()
        } else {
            Toast.makeText(this, "刪除失敗，找不到該廠牌資料", Toast.LENGTH_SHORT).show()
        }
    }
    private fun query() {
        val brand = edBrand.text.toString()

        if (brand.isEmpty()) {
            queryAll()
            return
        }

        val c = dbrw.query(
            "CarTable",
            null,
            "brand LIKE ?",
            arrayOf(brand),
            null, null, null
        )

        showQueryResult(c, "查詢 ${brand} 完成")
    }

    private fun queryAll() {
        val c = dbrw.query(
            "CarTable",
            null, null, null, null, null, null
        )
        showQueryResult(c, "顯示所有資料")
    }

    private fun showQueryResult(c: Cursor, message: String) {
        c.moveToFirst()
        items.clear()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        for (i in 0 until c.count) {
            val brandIndex = c.getColumnIndex("brand")
            val yearIndex = c.getColumnIndex("car_year")
            val priceIndex = c.getColumnIndex("price")

            val brand = c.getString(brandIndex)
            val year = c.getInt(yearIndex)
            val price = c.getInt(priceIndex)

            items.add("廠牌: $brand, 年份: $year, 價格: $price 萬元")
            c.moveToNext()
        }
        adapter.notifyDataSetChanged()
        c.close()
    }

    override fun onDestroy() {
        dbrw.close()
        super.onDestroy()
    }
}