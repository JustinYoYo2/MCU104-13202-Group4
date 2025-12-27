package com.example.summonsimulator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // 🌟 修改為 4 個分頁
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SettingFragment()
            1 -> MainContentFragment()
            2 -> TopUpFragment()
            3 -> FeedbackFragment() // 🌟 新增回饋頁面
            else -> MainContentFragment()
        }
    }
}