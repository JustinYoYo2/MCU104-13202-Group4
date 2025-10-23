package com.example.homework5

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class StartFragment : Fragment(R.layout.fragment_start) {
    private val vm: WorkViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnStart).setOnClickListener {
            vm.start()
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            vm.reset()
        }
    }
}