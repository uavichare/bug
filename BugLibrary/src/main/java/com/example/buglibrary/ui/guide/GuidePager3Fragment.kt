package com.example.buglibrary.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.data.Result
import com.example.buglibrary.databinding.FragmentGuidePager3Binding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.ui.attraction.ShopNDineAdapter
import com.example.buglibrary.ui.attraction.ShopNDineViewModel
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GuidePager3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuidePager3Fragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    lateinit var viewModel: ShopNDineViewModel
    private var _binding: FragmentGuidePager3Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGuidePager3Binding.inflate(inflater, container, false)

        viewModel = injectViewModel(viewModelProvider)
        viewModel.getPoiBySubCategory("restaurants").observe(viewLifecycleOwner, {
            when (it.status) {
                Result.Status.SUCCESS -> {
                    binding.recyclerViewShopDine.layoutManager =
                        LinearLayoutManager(context)
                    it.data?.let { poiList ->
                        binding.recyclerViewShopDine.adapter =
                            ShopNDineAdapter(this, poiList)
                    }
                }
                else -> {
                }
            }

        })
        return binding.root
    }


}