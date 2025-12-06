package com.example.summonsimulator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.util.Log
import android.graphics.Matrix
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.Space

class ResultActivity : AppCompatActivity() {

    // 🌟 角色圖片映射 (保持不變)
    private val characterImageMap = mapOf(
        "FocusA" to R.drawable.mry4, "FocusB" to R.drawable.rin4, "3StarX" to R.drawable.elg3,
        "3StarY" to R.drawable.tke3, "3StarZ" to R.drawable.hw3, "2StarA" to R.drawable.ntb2,
        "2StarB" to R.drawable.qlg2, "2StarC" to R.drawable.cdb2, "1StarA" to R.drawable.ds1,
        "1StarB" to R.drawable.esg1, "1StarC" to R.drawable.ftg1, "1StarD" to R.drawable.tc1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // 🌟 關鍵：查找主容器。如果仍有錯誤，請執行 Build -> Clean/Rebuild Project
        val resultsContainer: LinearLayout = findViewById(R.id.results_container)
        val pityInfoTextView: TextView = findViewById(R.id.text_pity_info)
        val backButton: Button = findViewById(R.id.button_back_to_gacha)

        // 收到結果數據
        val resultsStringList = intent.getStringArrayListExtra("EXTRA_GACHA_RESULTS")
        val pityCount = intent.getIntExtra("EXTRA_PITY_COUNT", 0)

        if (!resultsStringList.isNullOrEmpty()) {
            displayGachaResults(resultsContainer, resultsStringList)
        } else {
            // 處理結果為空的狀況
            displayGachaResults(resultsContainer, arrayListOf())
            Log.e("ResultActivity", "抽卡結果列表為空或傳輸失敗。")
        }

        pityInfoTextView.text = "距離上次保底: $pityCount 抽"
        backButton.setOnClickListener { finish() }
    }

    /**
     * 動態在垂直 LinearLayout 中顯示 3+3+3+1 佈局，使用水平 LinearLayout 實現居中。
     */
    private fun displayGachaResults(container: LinearLayout, results: ArrayList<String>) {
        container.removeAllViews()

        val density = resources.displayMetrics.density
        // 🌟 使用 dimen 中的值，如果 dimen 增加，這裡就會變大
        val imageSizePx = resources.getDimensionPixelSize(R.dimen.result_image_size)
        val itemSpacingDp = 16
        val rowMarginDp = 24

        val itemSpacingPx = (itemSpacingDp * density).toInt() // 項目之間的間距 (16dp)
        val rowMarginPx = (rowMarginDp * density).toInt()   // 行之間的間距 (24dp)

        // 🌟 關鍵修改：3+3+3+1 結構
        val layoutGroups = listOf(3, 3, 3, 1)
        var currentIndex = 0

        layoutGroups.forEachIndexed { groupIndex, count ->
            // 如果結果已經顯示完畢，則不再創建後續組的容器
            if (currentIndex >= results.size && count > 0) return@forEachIndexed

            // 1. 創建一個水平的 LinearLayout 來容納當前組的圖片，並負責居中
            val horizontalLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER // 🌟 關鍵：實現水平居中
                layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    if (groupIndex > 0) topMargin = rowMarginPx
                }
            }

            // 2. 填充當前組的圖片
            (0 until count).forEach { itemIndex ->

                // 創建垂直容器 (圖片和星級垂直堆疊)
                val itemContainer = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        imageSizePx, // 🌟 使用變大後的固定圖片尺寸
                        LayoutParams.WRAP_CONTENT
                    ).apply {
                        if (itemIndex > 0) marginStart = itemSpacingPx
                    }
                }

                if (currentIndex < results.size) {
                    val resultString = results[currentIndex]
                    val starLevel = resultString.substringBefore("星").toIntOrNull() ?: 1
                    val characterName = resultString.substringAfter(" - ").substringBefore(" (").trim()
                    val imageResourceId = characterImageMap[characterName] ?: R.drawable.head

                    // 創建 ImageView (圖片)
                    val imageView = ImageView(this).apply {
                        setImageResource(imageResourceId)
                        scaleType = ImageView.ScaleType.MATRIX
                        setBackgroundResource(R.drawable.image_border)
                        layoutParams = LinearLayout.LayoutParams(imageSizePx, imageSizePx)

                        post {
                            // 🌟 關鍵：確保圖片縮放和裁切正確，避免跑版
                            adjustImageScale(this)
                        }
                    }
                    itemContainer.addView(imageView)

                    // 創建 TextView (星級顯示)
                    val starTextView = TextView(this).apply {
                        text = "⭐".repeat(starLevel)
                        textSize = 12f
                        layoutParams = LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = itemSpacingPx / 4
                            bottomMargin = itemSpacingPx / 2
                        }
                    }
                    itemContainer.addView(starTextView)

                    currentIndex++
                } else {
                    // 如果結果不足，仍需要一個空的佔位元素，確保佈局穩定
                    val placeholder = Space(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            imageSizePx,
                            // 保持佔位高度與圖片+星級約一致
                            (imageSizePx + (16 * density).toInt())
                        )
                    }
                    itemContainer.addView(placeholder)
                }

                horizontalLayout.addView(itemContainer)
            }

            // 將水平容器加入到垂直主容器中
            container.addView(horizontalLayout)
        }
    }

    /**
     * 獨立的圖片縮放和裁切函數，確保長條形圖片上半部填滿 View，避免跑版。
     */
    private fun adjustImageScale(imageView: ImageView) {
        val drawable = imageView.drawable ?: return

        val viewWidth = imageView.width.toFloat()
        val drawableWidth = drawable.intrinsicWidth.toFloat()

        if (drawableWidth == 0f || viewWidth == 0f) return

        // 1. 計算縮放比例：讓圖片寬度填滿 View 寬度
        val scale: Float = viewWidth / drawableWidth

        val matrix = Matrix()
        matrix.setScale(scale, scale)
        // 2. 設置平移 (0,0)：確保圖片的左上角對齊 View 的左上角，從而裁切並顯示上半部。
        matrix.postTranslate(0f, 0f)

        imageView.imageMatrix = matrix
    }
}