package com.example.buglibrary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.example.buglibrary.AlFahidiWayFindingApp
import com.example.buglibrary.SDKActivity
import com.example.buglibrary.R
import com.example.buglibrary.data.Poi
import com.example.buglibrary.databinding.PoiDetailFragmentBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.ui.attraction.ShopNDineViewModel
import com.example.buglibrary.utils.CommonUtils
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject


class PoiDetailFragment : Fragment(), Injectable, View.OnClickListener {


    //    private lateinit var viewModel: PoiDetailViewModel

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    lateinit var viewModel: ShopNDineViewModel
    private var _binding: PoiDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private var poiDetail: Poi? = null
    private var isFav = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoiDetailFragmentBinding.inflate(inflater, container, false)
        viewModel = injectViewModel(viewModelProvider)
        val poiDetailFragmentArgs = PoiDetailFragmentArgs.fromBundle(requireArguments())
        poiDetail = poiDetailFragmentArgs.poi
        isFav = poiDetailFragmentArgs.isFav

        if (poiDetail?.isFavourite!! || isFav) {
            binding.buttonFavourite.setIconResource(R.drawable.ic_favorite_filled)

        }
        when (LocaleManager.getLanguage(requireContext())) {
            LocaleManager.LANGUAGE_ARABIC -> {
                binding.textPoiName.text = poiDetail?.poiMultilingual?.arabic?.name
                poiDetail?.poiMultilingual?.arabic?.description?.let {
                    binding.textDetail.text = HtmlCompat.fromHtml(
                        it,
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                }
            }

            else -> {
                binding.textPoiName.text = poiDetail?.poiMultilingual?.en?.name
                poiDetail?.poiMultilingual?.en?.description?.let {
                    binding.textDetail.text = HtmlCompat.fromHtml(
                        it,
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                }


            }
        }
        poiDetail?.audioListUrl?.let {
            if (it.isEmpty()) {
                binding.buttonMic.visibility = View.GONE
            }
        } ?: kotlin.run {
            binding.buttonMic.visibility = View.GONE
        }
        poiDetail?.videoListUrl?.let {
            if (it.isEmpty()) {
                binding.buttonVideo.visibility = View.GONE
            }
        } ?: kotlin.run {
            binding.buttonVideo.visibility = View.GONE
        }
        poiDetail?.picUrl?.let {
            if (it.isNotEmpty()) {
                binding.viewPagerpoiImg.adapter = PoiDetailPagerAdapter(this, it)
                TabLayoutMediator(binding.tabIndicator, binding.viewPagerpoiImg) { _, _ ->
                }.attach()
            } else {
                binding.imgPlaceHolder.visibility = View.VISIBLE
            }
        }
        binding.buttonDrawRoute.setOnClickListener(this)
        binding.buttonFavourite.setOnClickListener(this)
        binding.buttonMic.setOnClickListener(this)
        binding.buttonVideo.setOnClickListener(this)
        (activity as SDKActivity).setSearchVisibility(View.GONE)
        return binding.root

    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.button_draw_route -> {

                val app = activity?.application as AlFahidiWayFindingApp
                app.poi = poiDetail

                findNavController().popBackStack()

            }
            R.id.button_mic -> {
                val directions = PoiDetailFragmentDirections.actionAudioPlayerFragment(poiDetail)
                findNavController().navigate(directions)

            }
            R.id.button_video -> {


                // val intent = Intent(Intent.ACTION_VIEW)
                //intent.setDataAndType(
                // Uri.parse(poiDetail?.videoListUrl?.get(0)?.originalUrl!!),
                // "video/*"
                //)
                //startActivity(intent)


                val bundle = bundleOf("videoLink" to poiDetail?.videoListUrl?.get(0)?.originalUrl!!)
                Navigation.findNavController(requireView())
                    .navigate(R.id.attraction_videoplay_fragment, bundle)


            }
            R.id.button_favourite -> {
                if (isFav) {
                    poiDetail?.isFavourite = true

                }
                poiDetail?.isFavourite = poiDetail?.isFavourite?.not()!!
                val deviceId = CommonUtils.getDeviceId(requireContext())
/*
                viewModel.favourite(poiDetail!!, deviceId)
                    .observe(viewLifecycleOwner, {
                        when (it.status) {
                            Result.Status.SUCCESS -> {
                                if (poiDetail?.isFavourite!!) {
                                    binding.buttonFavourite.setIconResource(R.drawable.ic_favorite_filled)
                                } else {
                                    binding.buttonFavourite.setIconResource(R.drawable.ic_favorite)
                                }
                                viewModel.updatedDb(poiDetail!!)


                            }
                            else -> {

                            }
                        }
                    })
*/
            }

        }

    }


}