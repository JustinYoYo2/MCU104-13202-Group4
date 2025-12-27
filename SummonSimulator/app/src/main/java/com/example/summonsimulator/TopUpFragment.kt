package com.example.summonsimulator // ★請確認這行與你的專案一致

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView // ★記得引用 ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class TopUpFragment : Fragment() {

    private lateinit var tvCurrentStones: TextView
    private lateinit var dbHelper: SSDBHelper // 依照 GachaSettings 教學使用 SSDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 初始化資料庫
        dbHelper = SSDBHelper(requireContext())

        // 2. 綁定介面
        tvCurrentStones = view.findViewById(R.id.tv_current_stones)
        val productContainer = view.findViewById<LinearLayout>(R.id.product_list_container)

        // 3. 建立商品
        // 參數：(容器, 石頭數量, 價格)
        addProductItem(productContainer, 140, 99)
        addProductItem(productContainer, 445, 299)
        addProductItem(productContainer, 755, 490)
        addProductItem(productContainer, 3850, 2490)
        addProductItem(productContainer, 8080, 4990)

        // 4. 更新右上角石頭數量
        updateDisplay()
    }

    // 動態產生每一列商品 (圖片 + 文字 + 按鈕)
    private fun addProductItem(container: LinearLayout, stoneAmount: Int, price: Int) {
        val context = requireContext()

        // --- 1. 外層容器 (水平排列) ---
        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(40, 40, 40, 40)
            gravity = android.view.Gravity.CENTER_VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            layoutParams = params
            setBackgroundColor(android.graphics.Color.parseColor("#F5F5F5"))
        }

        // --- 2. 商品圖片 (新增的部分) ---
        val ivProduct = ImageView(context).apply {
            // ★這裡讀取你剛剛放入的 stone.png
            setImageResource(R.drawable.stone)

            // 設定圖片大小 (例如 150x150)
            val imgParams = LinearLayout.LayoutParams(150, 150)
            imgParams.setMargins(0, 0, 30, 0) // 右邊留點空隙
            layoutParams = imgParams

            // 圖片縮放模式
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        // --- 3. 中間資訊 (價格與石頭量) ---
        val infoLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvPrice = TextView(context).apply {
            text = "NT$ $price"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(android.graphics.Color.BLACK)
        }

        val tvAmount = TextView(context).apply {
            text = "獲得 $stoneAmount 石"
            textSize = 16f
            setTextColor(android.graphics.Color.GRAY)
        }

        infoLayout.addView(tvPrice)
        infoLayout.addView(tvAmount)

        // --- 4. 購買按鈕 ---
        val btnBuy = Button(context).apply {
            text = "購買"
            setOnClickListener {
                performPurchase(stoneAmount)
            }
        }

        // --- 5. 組合起來 (順序：圖片 -> 文字 -> 按鈕) ---
        itemLayout.addView(ivProduct)  // ★先加圖片
        itemLayout.addView(infoLayout) // 再加文字
        itemLayout.addView(btnBuy)     // 最後加按鈕

        container.addView(itemLayout)
    }

    // 核心功能：購買邏輯
    private fun performPurchase(addAmount: Int) {
        // 依照 GachaSettings 的教學規範，使用 updateStoneCount
        // 並且只傳入「增加量」，避免重複加總
        dbHelper.updateStoneCount(addAmount)

        updateDisplay()
        Toast.makeText(context, "購買成功！獲得 $addAmount 石", Toast.LENGTH_SHORT).show()
    }

    private fun updateDisplay() {
        // 讀取石頭數量
        val current = dbHelper.getStoneCount()
        tvCurrentStones.text = current.toString()
    }

    override fun onResume() {
        super.onResume()
        updateDisplay()
        TimeTracker.start() // 🌟 開始計時
    }

    override fun onPause() {
        super.onPause()
        TimeTracker.stop(requireContext(), "儲值頁面") // 🌟 結束計時
    }

}