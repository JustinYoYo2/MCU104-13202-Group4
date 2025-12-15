package com.example.summonsimulator

import android.content.Context
import kotlin.random.Random
import android.util.Log

/**
 * 代表一次抽卡操作的結果。 (保持不變)
 */
data class GachaResult(
    val starLevel: Int,
    val characterName: String,
    val isFocus: Boolean = false
) {
    fun getResultDescription(): String {
        val focusText = if (isFocus) " (當池角色!)" else ""
        return "${starLevel}星 - $characterName$focusText"
    }
}


/**
 * 處理所有抽卡機率計算和資料庫互動。
 * 🌟 已修正為「硬保底」機制 (X抽必中 3星)。
 */
class GachaManager(private val context: Context) {

    companion object {
        private const val TAG = "GachaManager"
    }

    private val dbHelper = SSDBHelper(context)
    private var settings: GachaSettings? = null
    // 🌟 重新引入保底計數器
    private var drawCountSinceLast3Star: Int = 0

    // 🌟 角色數據庫假設 (保持不變)
    private val FOCUS_CHARACTERS = listOf("FocusA", "FocusB")

    private val CHARACTERS_DATA = mapOf(
        3 to listOf("FocusA", "FocusB", "3StarX", "3StarY", "3StarZ"),
        2 to listOf("2StarA", "2StarB", "2StarC"),
        1 to listOf("1StarA", "1StarB", "1StarC", "1StarD")
    )

    init {
        loadSettings()
        // 🌟 從資料庫載入保底計數
        drawCountSinceLast3Star = dbHelper.getPityCounter()
    }

    // ... (loadSettings, getStoneCount 保持不變) ...
    fun loadSettings() {
        settings = dbHelper.getGachaSettings()
    }

    fun getStoneCount(): Int {
        return dbHelper.getStoneCount()
    }

    fun performDraw(count: Int): List<GachaResult>? {
        if (settings == null) {
            loadSettings()
        }
        val currentSettings = settings ?: return null

        val cost = if (count == 1) currentSettings.getCostSingle() else currentSettings.getCostTen()
        val currentStones = getStoneCount()

        if (currentStones < cost) {
            return null
        }

        dbHelper.updateStoneCount(-cost)

        val results = mutableListOf<GachaResult>()
        repeat(count) {
            results.add(singleDraw(currentSettings))
        }

        // 🌟 儲存最新的保底計數
        dbHelper.updatePityCounter(drawCountSinceLast3Star)

        return results
    }


    /**
     * 執行單次抽卡操作的核心機率計算。（硬保底邏輯）
     */
    private fun singleDraw(settings: GachaSettings): GachaResult {
        // 🌟 每次抽卡計數器 +1
        drawCountSinceLast3Star++

        val r3StarBaseRate = settings.getRate3Star() / 100.0 // 三星總機率
        val r2StarBaseRate = settings.getRate2Star() / 100.0 // 二星總機率
        val r1StarBaseRate = settings.getRate1Star() / 100.0 // 一星總機率

        // 🌟 抓取保底次數 X
        val pityCount = settings.getPityCount()

        val totalRate = r3StarBaseRate + r2StarBaseRate + r1StarBaseRate
        if (totalRate > 1.0 + 1e-6 || totalRate < 1.0 - 1e-6) {
            Log.e(TAG, "機率總和不等於 100%!")
        }


        // --- 1. 決定星級（硬保底邏輯） ---
        val finalStarLevel: Int
        var isPityHit = false // 標記是否為保底觸發

        if (drawCountSinceLast3Star >= pityCount) {
            // 🌟 達到或超過保底次數，強制抽到 3 星
            finalStarLevel = 3
            isPityHit = true
        } else {
            // 未達到保底次數，按基礎機率抽選
            val drawRand = Random.nextDouble() // 0.0 到 1.0 之間的亂數

            // 累積機率邊界
            val boundary3Star = r3StarBaseRate
            val boundary2Star = boundary3Star + r2StarBaseRate

            finalStarLevel = when {
                drawRand < boundary3Star -> 3
                drawRand < boundary2Star -> 2
                else -> 1
            }
        }

        // 🌟 只要抽到 3 星，重置保底計數器
        if (finalStarLevel == 3) {
            drawCountSinceLast3Star = 0
        }


        // --- 2. 決定具體角色 (保持不變) ---

        val rFocusRate = settings.getRateFocus() / 100.0

        val resultCharacter: String
        var isFocus = false

        when (finalStarLevel) {
            3 -> {
                // 三星角色分配邏輯：無論是保底命中還是機率命中，分配邏輯相同
                val nonFocus3StarCount = CHARACTERS_DATA[3]!!.size - FOCUS_CHARACTERS.size
                val rNonFocus3 = r3StarBaseRate - rFocusRate
                val rPerFocus = rFocusRate / FOCUS_CHARACTERS.size
                val rPerNonFocus3 = rNonFocus3 / nonFocus3StarCount

                // 注意：這裡使用的 r3StarBaseRate 是基礎機率，不是 100%，但在 3 星這個類別內部分配是正確的
                val totalWeight = (FOCUS_CHARACTERS.size * rPerFocus) + (nonFocus3StarCount * rPerNonFocus3)

                val weightedList = mutableListOf<Pair<String, Double>>()
                FOCUS_CHARACTERS.forEach { name ->
                    weightedList.add(Pair(name, rPerFocus))
                }
                CHARACTERS_DATA[3]!!.filter { it !in FOCUS_CHARACTERS }.forEach { name ->
                    weightedList.add(Pair(name, rPerNonFocus3))
                }

                var currentBoundary = 0.0
                val characterRand = Random.nextDouble() * totalWeight

                resultCharacter = weightedList.first { (_, weight) ->
                    currentBoundary += weight
                    characterRand < currentBoundary
                }.first

                isFocus = resultCharacter in FOCUS_CHARACTERS
            }
            // 2 星和 1 星：在該星級的角色列表中平均分配 (保持不變)
            2 -> resultCharacter = CHARACTERS_DATA[2]!!.random()
            1 -> resultCharacter = CHARACTERS_DATA[1]!!.random()
            else -> resultCharacter = "未知角色"
        }

        // 返回更新後的 GachaResult
        return GachaResult(finalStarLevel, resultCharacter, isFocus)
    }

    /**
     * 讀取目前的保底計數。
     */
    fun getDrawCountSinceLast3Star(): Int {
        return drawCountSinceLast3Star
    }
}