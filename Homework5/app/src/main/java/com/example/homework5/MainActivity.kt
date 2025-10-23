package com.example.homework5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainActivity : AppCompatActivity() {
    private val vm: WorkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 2

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Start"
                1 -> "Progress"
                2 -> "Result"
                else -> throw IllegalStateException("Invalid position")
            }
        }.attach()

        vm.status.observe(this) { status ->
            if (status.startsWith("Preparing") || status.startsWith("Working")) {
                viewPager.setCurrentItem(1, true) // 切換到 Progress Fragment
            } else if (status == "背景工作結朿！" || status == "Canceled") {
                viewPager.setCurrentItem(2, true) // 切換到 Result Fragment
            }
        }
    }
}

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // 總共有 Start, Progress, Result 三個 Fragment

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StartFragment()
            1 -> ProgressFragment()
            2 -> ResultFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}