package com.example.summonsimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class FeedbackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editFeedback = view.findViewById<EditText>(R.id.edit_feedback)
        val btnSubmit = view.findViewById<Button>(R.id.btn_submit_feedback)

        btnSubmit.setOnClickListener {
            val content = editFeedback.text.toString()
            if (content.isNotBlank()) {
                // 這裡可以實作存入資料庫或傳送到後台的邏輯
                Toast.makeText(context, "感謝您的回饋！", Toast.LENGTH_SHORT).show()
                editFeedback.text.clear()
            } else {
                Toast.makeText(context, "請輸入內容", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        TimeTracker.start() // 🌟 使用紀錄計時開始
    }

    override fun onPause() {
        super.onPause()
        TimeTracker.stop(requireContext(), "回饋頁面(FeedbackFragment)") // 🌟 結束計時
    }
}