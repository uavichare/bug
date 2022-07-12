package com.example.buglibrary.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.buglibrary.data.PoiMedia
import com.example.buglibrary.ui.home.PoiBannerFragment

class PoiDetailPagerAdapter(fragment: Fragment, private val picUrl: List<PoiMedia>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = picUrl.size

    override fun createFragment(position: Int): Fragment {
        return PoiBannerFragment.newInstance(picUrl[position])
    }

}