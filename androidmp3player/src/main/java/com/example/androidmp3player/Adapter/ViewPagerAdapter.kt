package com.example.androidmp3player.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    private val fragmentList: ArrayList<Fragment> = ArrayList<Fragment>()

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList.get(position)

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }

    fun removeFragment(fragment: Fragment){
        fragmentList.remove(fragment)
    }
}