package com.example.buglibrary.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.example.buglibrary.SDKActivity
import com.example.buglibrary.R
import com.example.buglibrary.databinding.GuideFragmentBinding

class GuideFragment : Fragment() {

    companion object {
        fun newInstance() = GuideFragment()
    }

    private lateinit var viewModel: GuideViewModel

    private var _binding: GuideFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = GuideFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(GuideViewModel::class.java)
        val imageList =
            arrayOf(R.drawable.help_1, R.drawable.help_2, R.drawable.help_3)
        binding.viewPagerGuide.adapter = GuidePagerAdapter(this, imageList)
        TabLayoutMediator(binding.tabLayoutGuide, binding.viewPagerGuide) { _, _ ->
        }.attach()
        (activity as SDKActivity).setToolBarVisibility(View.GONE)
        binding.buttonBack.setOnClickListener {
            (activity as SDKActivity).setToolBarVisibility(View.VISIBLE)
            findNavController().popBackStack()
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}