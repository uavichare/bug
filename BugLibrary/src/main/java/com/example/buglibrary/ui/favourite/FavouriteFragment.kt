package com.example.buglibrary.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.R
import com.example.buglibrary.data.Result
import com.example.buglibrary.databinding.FavouriteFragmentBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.ui.attraction.ShopNDineAdapter
import com.example.buglibrary.utils.CommonUtils
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject

class FavouriteFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private var _binding: FavouriteFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: FavouriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        if (_binding == null) {
        _binding = FavouriteFragmentBinding.inflate(inflater, container, false)
        viewModel = injectViewModel(viewModelProvider)
        binding.recyclerViewFavourite.layoutManager =
            LinearLayoutManager(context)
        fetchFavourite()
//        }

        return binding.root
    }

    private fun fetchFavourite() {
        viewModel.fetchAllFavourite(CommonUtils.getDeviceId(requireContext()))
            .observe(viewLifecycleOwner) { result ->
                when (result.status) {
                    Result.Status.SUCCESS -> {
                        binding.shimmer.visibility = View.GONE
                        binding.shimmer.hideShimmer()

                        result.data?.data?.let { poiList ->
                            if (poiList.isEmpty()) {
                                setEmptyState()
                            }
                            binding.recyclerViewFavourite.adapter =
                                ShopNDineAdapter(this, poiList)
                        } ?: kotlin.run {

                            setEmptyState()
                        }
                    }
                    Result.Status.LOADING -> {
                        binding.shimmer.visibility = View.VISIBLE
                        binding.shimmer.startShimmer()
                    }
                    Result.Status.ERROR -> {
                        binding.shimmer.visibility = View.GONE
                        binding.shimmer.hideShimmer()
                        setEmptyState()

                    }
                }
            }
    }

    fun reload() {
        fetchFavourite()
    }

    private fun setEmptyState() {
        binding.errorLayout.errorLayout.visibility = View.VISIBLE
        binding.errorLayout.imgError.setImageResource(R.drawable.empty_favourite)
//        binding.errorLayout.errorDetail.text = getString(R.string.favourite_empty_state)
    }
}