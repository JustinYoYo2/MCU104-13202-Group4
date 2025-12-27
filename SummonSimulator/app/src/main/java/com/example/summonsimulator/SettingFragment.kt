package com.example.summonsimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast

class SettingFragment : Fragment() {


    private lateinit var editOneStarRate: EditText
    private lateinit var editTwoStarRate: EditText
    private lateinit var editThreeStarRate: EditText
    private lateinit var editPoolRate: EditText
    private lateinit var editPity: EditText
    private lateinit var editOnePullCost: EditText
    private lateinit var editTenPullCost: EditText

    private lateinit var dbHelper: SSDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_setting, container, false)


        editOneStarRate = view.findViewById(R.id.edit_one_star_rate)
        editTwoStarRate = view.findViewById(R.id.edit_two_star_rate)
        editThreeStarRate = view.findViewById(R.id.edit_three_star_rate)
        editPoolRate = view.findViewById(R.id.edit_pool_rate)
        editPity = view.findViewById(R.id.edit_pity)
        editOnePullCost = view.findViewById(R.id.edit_one_pull_cost)
        editTenPullCost = view.findViewById(R.id.edit_ten_pull_cost)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        context?.let {
            dbHelper = SSDBHelper(it)

            loadSettingsToUi()
        }
    }


    private fun loadSettingsToUi() {
        val settings = dbHelper.getGachaSettings()

        if (settings != null) {
            // 機率設定
            editOneStarRate.setText(settings.getRate1Star().toString())
            editTwoStarRate.setText(settings.getRate2Star().toString())
            editThreeStarRate.setText(settings.getRate3Star().toString())
            editPoolRate.setText(settings.getRateFocus().toString())

            // 次數/花費設定
            editPity.setText(settings.getPityCount().toString())
            editOnePullCost.setText(settings.getCostSingle().toString())
            editTenPullCost.setText(settings.getCostTen().toString())

        } else {
            Toast.makeText(context, "無法讀取設定檔，請檢查資料庫！", Toast.LENGTH_LONG).show()
        }
    }


    private fun saveSettingsFromUi() {

        val rate1Star = editOneStarRate.text.toString().toDoubleOrNull() ?: 0.0
        val rate2Star = editTwoStarRate.text.toString().toDoubleOrNull() ?: 0.0
        val rate3Star = editThreeStarRate.text.toString().toDoubleOrNull() ?: 0.0
        val rateFocus = editPoolRate.text.toString().toDoubleOrNull() ?: 0.0

        val pityCount = editPity.text.toString().toIntOrNull() ?: 0
        val costSingle = editOnePullCost.text.toString().toIntOrNull() ?: 0
        val costTen = editTenPullCost.text.toString().toIntOrNull() ?: 0


        val currentStoneCount = dbHelper.getStoneCount()


        val newSettings = GachaSettings(
            rate1Star, rate2Star, rate3Star, rateFocus,
            pityCount, costSingle, costTen,
            currentStoneCount // 傳入現有石頭數量
        )


        val success = dbHelper.updateGachaSettings(newSettings)

        if (success) {
            Toast.makeText(context, "設定已儲存！", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "儲存失敗！", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        TimeTracker.start() // 🌟 開始計時
    }

    override fun onPause() {
        super.onPause()
        saveSettingsFromUi()
        TimeTracker.stop(requireContext(), "設定頁面") // 🌟 結束計時
    }
}