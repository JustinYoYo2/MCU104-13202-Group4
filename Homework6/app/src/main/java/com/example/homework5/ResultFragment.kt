package com.example.homework5

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ResultFragment : Fragment(R.layout.fragment_result) {
    private val vm: WorkViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val statusText = view.findViewById<TextView>(R.id.txtResult)
        val btnReset = view.findViewById<Button>(R.id.btnReset)

        vm.status.observe(viewLifecycleOwner) { it ->
            when {
                it == "Preparing…" -> statusText.text = "準備中"
                it.startsWith("Working") -> statusText.text = "工作中"
                it == "背景工作結朿！" -> statusText.text = "背景工作結朿"
                it == "Canceled" -> statusText.text = "Canceled"
                else -> statusText.text = "等待開始..."
            }
        }

        btnReset.setOnClickListener {
            vm.reset()
        }
    }
}