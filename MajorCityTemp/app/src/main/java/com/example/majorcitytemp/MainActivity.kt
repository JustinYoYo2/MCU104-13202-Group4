package com.example.majorcitytemp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

val TAIWAN_CITIES = listOf(
    "Taipei", "New Taipei City", "Taoyuan City", "Hsinchu City",
    "Miaoli City", "Taichung City", "Changhua City", "Nantou City",
    "Yunlin City", "Chiayi City", "Tainan City", "Kaohsiung City",
    "Pingtung City", "Yilan City", "Hualien City", "Taitung City",
    "Keelung City", "Penghu City", "Kinmen City", "Lienchiang City"
)

data class WeatherResponse(val main: MainData, val name: String)
data class MainData(val temp: Double, val humidity: Int)

class MainActivity : AppCompatActivity() {

    private lateinit var citySpinner: Spinner
    private lateinit var tvWeatherResult: TextView
    private lateinit var btnQuery: Button

    private var selectedCity: String = TAIWAN_CITIES[0] // 預設選中第一個城市

    private val apiKey = "910b7ea7be0bd6be4c771a15830c1f8c"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        citySpinner = findViewById(R.id.citySpinner)
        btnQuery = findViewById(R.id.btnQuery)
        tvWeatherResult = findViewById(R.id.tvWeatherResult)

        setupCitySpinner()

        btnQuery.setOnClickListener {
            fetchWeather(selectedCity)
        }
    }

    // 設置下拉選單
    private fun setupCitySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, TAIWAN_CITIES)
        citySpinner.adapter = adapter

        // 監聽選擇事件
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCity = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun fetchWeather(city: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$apiKey"
        var resultText: String

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val weatherData = Gson().fromJson(responseData, WeatherResponse::class.java)

                    resultText = "城市: ${weatherData.name}\n"+
                            "溫度: ${String.format("%.1f", weatherData.main.temp)}°C\n" +
                            "濕度: ${weatherData.main.humidity}%"

                } else {
                    resultText = "查詢失敗 (Code: ${response.code})."
                }
            } catch (e: IOException) {
                e.printStackTrace()
                resultText = "網路錯誤: 無法連線到伺服器。"
            } catch (e: Exception) {
                e.printStackTrace()
                resultText = "資料解析錯誤。"
            }

            // 切換回 Main Thread 更新結果顯示
            withContext(Dispatchers.Main) {
                tvWeatherResult.text = resultText
                if (!resultText.contains("失敗") && !resultText.contains("錯誤")) {
                    Toast.makeText(this@MainActivity, "${city} 查詢成功！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}