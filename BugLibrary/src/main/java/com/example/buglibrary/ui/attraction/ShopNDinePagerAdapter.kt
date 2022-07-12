package com.example.buglibrary.ui.attraction

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.buglibrary.ui.attraction.ShopNDineFragment

class ShopNDinePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return ShopNDineFragment.newInstance(position)
    }

}