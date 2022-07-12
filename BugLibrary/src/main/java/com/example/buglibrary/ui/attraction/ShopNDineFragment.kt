package com.example.buglibrary.ui.attraction

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.data.Result
import com.example.buglibrary.databinding.ShopNDineFragmentBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject

class ShopNDineFragment : Fragment(), Injectable {
    companion object {
        fun newInstance(position: Int): ShopNDineFragment {
            val args = Bundle()
            args.putInt(Intent.EXTRA_TEMPLATE, position)
            val fragment = ShopNDineFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    lateinit var viewModel: ShopNDineViewModel
    private var _binding: ShopNDineFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShopNDineFragmentBinding.inflate(inflater, container, false)
        viewModel = injectViewModel(viewModelProvider)
        when (arguments?.getInt(Intent.EXTRA_TEMPLATE)) {
            0 -> {

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
            }
            1 -> {
                viewModel.getPoiBySubCategory("retail").observe(viewLifecycleOwner, {
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
            }
            2 -> {
                viewModel.getPoiMultipleSubCategory(
                    listOf(
                        "Artistic houses for professional talents",
                        "Houses of safeguarding the Emirati heritage ( museum)"
                    )
                )
                    .observe(viewLifecycleOwner, {
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
            }
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}