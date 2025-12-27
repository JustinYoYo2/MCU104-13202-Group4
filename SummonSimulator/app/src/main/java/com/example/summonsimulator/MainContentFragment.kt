package com.example.summonsimulator

import android.content.Intent // 需要新增
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button // 需要新增

class MainContentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startButton: Button = view.findViewById(R.id.btn_start_gacha)
        startButton.setOnClickListener {
            val intent = Intent(activity, GachaActivity::class.java)
            startActivity(intent)
        }

        val staffButton: Button = view.findViewById(R.id.btn_staff_list)
        staffButton.setOnClickListener {
            val intent = Intent(activity, StaffActivity::class.java)
            startActivity(intent)
        }

        val redeemButton: Button = view.findViewById(R.id.btn_redeem)
        redeemButton.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        TimeTracker.start()
    }

    // 🌟 新增：當使用者點擊按鈕跳轉或切換分頁時結束計時
    override fun onPause() {
        super.onPause()
        TimeTracker.stop(requireContext(), "主首頁(MainContentFragment)")
    }
}