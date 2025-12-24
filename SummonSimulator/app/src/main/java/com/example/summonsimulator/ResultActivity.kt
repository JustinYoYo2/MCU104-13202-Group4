package com.example.summonsimulator

import android.graphics.Matrix
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    private val characterImageMap = mapOf(
        "FocusA" to R.drawable.mry4, "FocusB" to R.drawable.rin4, "3StarX" to R.drawable.elg3,
        "3StarY" to R.drawable.tke3, "3StarZ" to R.drawable.hw3, "2StarA" to R.drawable.ntb2,
        "2StarB" to R.drawable.qlg2, "2StarC" to R.drawable.cdb2, "1StarA" to R.drawable.ds1,
        "1StarB" to R.drawable.esg1, "1StarC" to R.drawable.ftg1, "1StarD" to R.drawable.tc1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultsContainer: LinearLayout = findViewById(R.id.results_container)
        val pityInfoTextView: TextView = findViewById(R.id.text_pity_info)
        val backButton: Button = findViewById(R.id.button_back_to_gacha)

        val resultsStringList = intent.getStringArrayListExtra("EXTRA_GACHA_RESULTS")
        val pityCount = intent.getIntExtra("EXTRA_PITY_COUNT", 0)

        if (!resultsStringList.isNullOrEmpty()) {
            displayGachaResults(resultsContainer, resultsStringList)
        }

        pityInfoTextView.text = "距離上次保底: $pityCount 抽"

        // 🌟 finish() 會回到 GachaActivity，Service 會繼續播 bg2
        backButton.setOnClickListener { finish() }
    }

    private fun displayGachaResults(container: LinearLayout, results: ArrayList<String>) {
        container.removeAllViews()
        val density = resources.displayMetrics.density
        val imageSizePx = resources.getDimensionPixelSize(R.dimen.result_image_size)
        val itemSpacingPx = (16 * density).toInt()
        val rowMarginPx = (24 * density).toInt()

        val layoutGroups = listOf(3, 3, 3, 1)
        var currentIndex = 0

        layoutGroups.forEachIndexed { groupIndex, count ->
            if (currentIndex >= results.size) return@forEachIndexed
            val horizontalLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    if (groupIndex > 0) topMargin = rowMarginPx
                }
            }

            (0 until count).forEach { itemIndex ->
                val itemContainer = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(imageSizePx, LayoutParams.WRAP_CONTENT).apply {
                        if (itemIndex > 0) marginStart = itemSpacingPx
                    }
                }

                if (currentIndex < results.size) {
                    val resultString = results[currentIndex]
                    val starLevel = resultString.substringBefore("星").toIntOrNull() ?: 1
                    val characterName = resultString.substringAfter(" - ").substringBefore(" (").trim()
                    val imageResourceId = characterImageMap[characterName] ?: R.drawable.head

                    val imageView = ImageView(this).apply {
                        setImageResource(imageResourceId)
                        scaleType = ImageView.ScaleType.MATRIX
                        setBackgroundResource(R.drawable.image_border)
                        layoutParams = LinearLayout.LayoutParams(imageSizePx, imageSizePx)
                        post { adjustImageScale(this) }
                    }
                    itemContainer.addView(imageView)

                    val starTextView = TextView(this).apply {
                        text = "⭐".repeat(starLevel)
                        textSize = 12f
                        layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                            topMargin = itemSpacingPx / 4
                        }
                    }
                    itemContainer.addView(starTextView)
                    currentIndex++
                }
                horizontalLayout.addView(itemContainer)
            }
            container.addView(horizontalLayout)
        }
    }

    private fun adjustImageScale(imageView: ImageView) {
        val drawable = imageView.drawable ?: return
        val viewWidth = imageView.width.toFloat()
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        if (drawableWidth == 0f || viewWidth == 0f) return
        val scale: Float = viewWidth / drawableWidth
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        imageView.imageMatrix = matrix
    }
}