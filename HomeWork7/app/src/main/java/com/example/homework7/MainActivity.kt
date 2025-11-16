package com.example.homework7

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val TAIWAN_START = LatLng(25.033611, 121.565000) // 台北 101 (起點)
    private val TAIWAN_END = LatLng(25.047794, 121.517395)   // 台北車站 (終點)
    private var currentPolyline: com.google.android.gms.maps.model.Polyline? = null

    private val API_KEY = "AIzaSyAN0tIjXqJUA6WrPMmjUfeUs06J03JipUE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.addMarker(MarkerOptions().position(TAIWAN_START).title("起點：台北 101"))
        googleMap.addMarker(MarkerOptions().position(TAIWAN_END).title("終點：台北車站"))

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TAIWAN_START, 13f))

        drawRoute("driving")
    }

    fun onModeSelected(view: View) {
        val mode = when (view.id) {
            R.id.btn_driving -> "driving"
            R.id.btn_walking -> "walking"
            R.id.btn_bicycling -> "bicycling"
            else -> return
        }
        drawRoute(mode)
    }

    private fun drawRoute(mode: String) {
        currentPolyline?.remove()

        val originStr = "${TAIWAN_START.latitude},${TAIWAN_START.longitude}"
        val destinationStr = "${TAIWAN_END.latitude},${TAIWAN_END.longitude}"
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=$originStr&" +
                "destination=$destinationStr&" +
                "mode=$mode&" +
                "key=$API_KEY"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonResponse = URL(url).readText()
                val polyline = parseRoute(jsonResponse)

                withContext(Dispatchers.Main) {
                    if (polyline.isNotEmpty()) {
                        val polylineOptions = PolylineOptions()
                            .addAll(decodePoly(polyline))
                            .width(15f)
                            .color(Color.BLUE)

                        currentPolyline = googleMap.addPolyline(polylineOptions)
                        Toast.makeText(this@MainActivity, "已繪製 ${
                            when (mode) {
                                "driving" -> "開車"
                                "walking" -> "走路"
                                "bicycling" -> "單車"
                                else -> ""
                            }
                        } 路線", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "無法取得 $mode 路線", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "網路請求失敗: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun parseRoute(jsonString: String): String {
        return try {
            val jsonObject = JSONObject(jsonString)
            val routes = jsonObject.getJSONArray("routes")
            if (routes.length() > 0) {
                routes.getJSONObject(0)
                    .getJSONObject("overview_polyline")
                    .getString("points")
            } else ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }
}