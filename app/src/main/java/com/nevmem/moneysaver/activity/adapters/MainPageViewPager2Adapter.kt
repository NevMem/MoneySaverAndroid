package com.nevmem.moneysaver.activity.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nevmem.moneysaver.fragments.AddFragment
import com.nevmem.moneysaver.fragments.DashboardFragment
import com.nevmem.moneysaver.fragments.HistoryFragment
import com.nevmem.moneysaver.fragments.TemplatesFragment

class MainPageViewPager2Adapter(lifecycle: Lifecycle, fragmentManager: FragmentManager) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DashboardFragment()
            1 -> TemplatesFragment()
            2 -> AddFragment()
            else -> HistoryFragment()
        }
    }

    override fun getItemCount() = 4

}