package com.example.buglibrary.ui.attraction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.example.buglibrary.AlFahidiWayFindingApp
import com.example.buglibrary.R
import com.example.buglibrary.databinding.AttractionFragmentBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject

class AttractionFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private lateinit var viewModel: AttractionViewModel
    private var _binding: AttractionFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var app: AlFahidiWayFindingApp

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AttractionFragmentBinding.inflate(inflater, container, false)
        viewModel = injectViewModel(viewModelProvider)
        app = activity?.application as AlFahidiWayFindingApp
        setPager()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        app.poi?.let {
            findNavController().popBackStack()
        }
    }

    private fun setPager() {
        val tabs =
            arrayOf(getString(R.string.food), getString(R.string.shop), getString(R.string.heritage))
        val tabsIcon =
            arrayOf(R.drawable.ic_food_drink, R.drawable.ic_shop, R.drawable.ic_heritage)
        binding.pagerAttraction.adapter = ShopNDinePagerAdapter(this)
        TabLayoutMediator(binding.tabLayoutAttraction, binding.pagerAttraction) { tab, position ->
            tab.text = tabs[position]
            tab.setIcon(tabsIcon[position])

        }.attach()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}