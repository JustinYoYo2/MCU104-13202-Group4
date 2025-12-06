package com.example.summonsimulator

import android.content.Context
import kotlin.random.Random

/**
 * 代表一次抽卡操作的結果。
 * @param starLevel 抽到的星級 (1, 2, 或 3)
 * @param characterName 抽到的具體角色名稱
 * @param isFocus 是否為當期主打 (Focus) 角色
 */
// ⚠️ 確保您的 GachaResult.kt 已修改為包含 characterName
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
 * 處理所有抽卡機率計算、保底狀態和資料庫互動。
 */
class GachaManager(private val context: Context) {
    private val dbHelper = SSDBHelper(context)
    private var settings: GachaSettings? = null
    private var drawCountSinceLast3Star: Int = 0

    // 🌟 角色數據庫假設：您需要確保這些名稱與您的遊戲資源匹配
    private val FOCUS_CHARACTERS = listOf("FocusA", "FocusB") // 2 種當池

    private val CHARACTERS_DATA = mapOf(
        // 三星角色: 2個當池 + 3個普通 = 5個
        3 to listOf("FocusA", "FocusB", "3StarX", "3StarY", "3StarZ"),
        // 二星角色: 3個
        2 to listOf("2StarA", "2StarB", "2StarC"),
        // 一星角色: 4個
        1 to listOf("1StarA", "1StarB", "1StarC", "1StarD")
    )

    init {
        loadSettings()
        // 從資料庫載入保底計數
        drawCountSinceLast3Star = dbHelper.getPityCounter()
    }

    /**
     * 從資料庫載入最新的抽卡設定。
     */
    fun loadSettings() {
        settings = dbHelper.getGachaSettings()
    }

    /**
     * 獲取當前的石頭數量。
     */
    fun getStoneCount(): Int {
        return dbHelper.getStoneCount()
    }

    /**
     * 執行一次或多次抽卡操作。
     */
    fun performDraw(count: Int): List<GachaResult>? {
        if (settings == null) {
            loadSettings()
        }
        val currentSettings = settings ?: return null

        val cost = if (count == 1) currentSettings.getCostSingle() else currentSettings.getCostTen()
        val currentStones = getStoneCount()

        // 1. 檢查石頭數量
        if (currentStones < cost) {
            return null // 石頭不足
        }

        // 2. 扣除石頭數量
        dbHelper.updateStoneCount(-cost)

        // 3. 執行機率運算
        val results = mutableListOf<GachaResult>()
        repeat(count) {
            results.add(singleDraw(currentSettings))
        }

        // 4. 將最新的 drawCountSinceLast3Star 存入資料庫以持久化
        dbHelper.updatePityCounter(drawCountSinceLast3Star)

        return results
    }

    /**
     * 執行單次抽卡操作的核心機率計算。（修正為支援多當池）
     */
    private fun singleDraw(settings: GachaSettings): GachaResult {
        drawCountSinceLast3Star++ // 每次抽卡計數器 +1

        val r3StarBaseRate = settings.getRate3Star() / 100.0 // 基礎三星總機率 (3.0%)
        val r2StarBaseRate = settings.getRate2Star() / 100.0 // 基礎二星機率
        val r1StarBaseRate = settings.getRate1Star() / 100.0 // 基礎一星機率
        val rFocusRate = settings.getRateFocus() / 100.0 // 總當池機率 (0.7%)

        val rPityCount = settings.getPityCount() // 保底次數

        // --- 1. 處理保底機率提升 (邏輯保持不變) ---
        val pityStart = rPityCount / 2
        var r3Pity = r3StarBaseRate

        if (drawCountSinceLast3Star >= pityStart) {
            val drawsOverPityStart = drawCountSinceLast3Star - pityStart
            val increaseFactor = (1.0 - r3StarBaseRate) / (rPityCount - pityStart)
            r3Pity = r3StarBaseRate + drawsOverPityStart * increaseFactor
            r3Pity = r3Pity.coerceAtMost(1.0)
        }

        // --- 2. 決定星級 (邏輯保持不變) ---
        val finalStarLevel: Int
        val drawRand = Random.nextDouble() // 0.0 到 1.0 之間的亂數

        if (drawRand < r3Pity) { // 抽到 3 星
            finalStarLevel = 3
            drawCountSinceLast3Star = 0 // 重置保底計數器
        } else {
            // 未中 3 星，在剩餘空間 (1 - r3Pity) 中決定 1 星或 2 星
            val baseRate2And1 = r2StarBaseRate + r1StarBaseRate
            val r2Normalized = (r2StarBaseRate / baseRate2And1) * (1.0 - r3Pity)
            val boundary2Star = r3Pity + r2Normalized

            finalStarLevel = when {
                drawRand < boundary2Star -> 2
                else -> 1
            }
        }

        // --- 3. 決定具體角色 (多當池分配邏輯) ---
        val resultCharacter: String
        var isFocus = false

        when (finalStarLevel) {
            3 -> {
                val nonFocus3StarCount = CHARACTERS_DATA[3]!!.size - FOCUS_CHARACTERS.size // 3 種普通三星

                // 普通三星的總機率 (2.3%)
                val rNonFocus3 = r3StarBaseRate - rFocusRate

                // 單一當池機率 (0.7% / 2 = 0.35%)
                val rPerFocus = rFocusRate / FOCUS_CHARACTERS.size
                // 單一普通三星機率 (2.3% / 3 ≈ 0.767%)
                val rPerNonFocus3 = rNonFocus3 / nonFocus3StarCount

                // 總權重：用於正規化加權隨機
                val totalWeight = (FOCUS_CHARACTERS.size * rPerFocus) + (nonFocus3StarCount * rPerNonFocus3)

                val weightedList = mutableListOf<Pair<String, Double>>()

                // 建立加權列表
                FOCUS_CHARACTERS.forEach { name ->
                    weightedList.add(Pair(name, rPerFocus))
                }
                CHARACTERS_DATA[3]!!.filter { it !in FOCUS_CHARACTERS }.forEach { name ->
                    weightedList.add(Pair(name, rPerNonFocus3))
                }

                // 執行加權隨機
                var currentBoundary = 0.0
                val characterRand = Random.nextDouble() * totalWeight

                resultCharacter = weightedList.first { (_, weight) ->
                    currentBoundary += weight
                    characterRand < currentBoundary
                }.first

                isFocus = resultCharacter in FOCUS_CHARACTERS
            }

            // 2 星和 1 星：在該星級的角色列表中平均分配
            2 -> resultCharacter = CHARACTERS_DATA[2]!!.random()
            1 -> resultCharacter = CHARACTERS_DATA[1]!!.random()
            else -> resultCharacter = "未知角色"
        }

        // 🌟 返回更新後的 GachaResult
        return GachaResult(finalStarLevel, resultCharacter, isFocus)
    }

    /**
     * 讀取目前的保底計數。
     */
    fun getDrawCountSinceLast3Star(): Int {
        return drawCountSinceLast3Star
    }
}