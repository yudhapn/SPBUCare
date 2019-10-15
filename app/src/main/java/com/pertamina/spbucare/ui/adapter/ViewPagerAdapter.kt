package com.pertamina.spbucare.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pertamina.spbucare.ui.UserListFragment

@Suppress("DEPRECATION")
class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> UserListFragment("spbu")
                1 -> UserListFragment("pertamina")
                else -> UserListFragment("spbu")
            }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? =
            when (position) {
                0 -> "SPBU"
                1 -> "Pertamina"
                else -> "SPBU"
            }
}