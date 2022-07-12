package com.example.buglibrary.ui.guide

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.buglibrary.ui.guide.GuideImageFragment
import com.example.buglibrary.ui.guide.GuidePager1Fragment
import com.example.buglibrary.ui.guide.GuidePager2Fragment
import com.example.buglibrary.ui.guide.GuidePager3Fragment

class GuidePagerAdapter(fragment: Fragment, private val picUrl: Array<Int>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = picUrl.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                GuidePager1Fragment()
            }
            1 -> {
                GuidePager2Fragment()
            }
            2 -> {
                GuidePager3Fragment()
            }
            else -> {
                GuideImageFragment.newInstance(picUrl[position])
            }
        }

    }

}