package com.example.homework5

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ProgressFragment : Fragment(R.layout.fragment_progress) {
    private val vm: WorkViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bar = view.findViewById<ProgressBar>(R.id.progressBar)
        val text = view.findViewById<TextView>(R.id.txt)
        val btnCancelProgress = view.findViewById<Button>(R.id.btnCancelProgress)

        bar.isIndeterminate = true

        vm.status.observe(viewLifecycleOwner) { s ->
            text.text = s
            if (s.startsWith("Working")) bar.isIndeterminate = false
            else bar.isIndeterminate = true
        }

        vm.progress.observe(viewLifecycleOwner) { p ->
            if (!bar.isIndeterminate) {
                bar.max = 100
                bar.progress = p
                if (vm.status.value?.startsWith("Working") == true) {
                    text.text = "Working… %d%%".format(p)
                }
            }
        }

        btnCancelProgress.setOnClickListener {
            vm.cancel()
        }
    }
}