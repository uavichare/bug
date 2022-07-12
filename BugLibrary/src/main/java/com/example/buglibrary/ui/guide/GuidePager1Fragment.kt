package com.example.buglibrary.ui.guide

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.buglibrary.R
import com.example.buglibrary.data.Result
import com.example.buglibrary.databinding.FragmentGuidePager1Binding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.ui.home.HomeViewModel
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GuidePager1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuidePager1Fragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    @Inject
    lateinit var homeViewModel: HomeViewModel

    private var _binding: FragmentGuidePager1Binding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGuidePager1Binding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync {
            it.uiSettings.setAllGesturesEnabled(false)
            setMapBox(it)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()

        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    override fun onDestroy() {
        super.onDestroy()
        // due to prevent back loading instead on onDestroyView called in onDestroy
        binding.mapView.onDestroy()
        _binding = null
    }

    private fun setMapBox(mapboxMap: MapboxMap) {

        val ar = ArrayList<Pair<String, Bitmap>>()

        homeViewModel.poiImageMap().forEach {
            ar.add(Pair(it.toString(), BitmapFactory.decodeResource(resources, it)))
        }
        mapboxMap.setStyle(
            Style.Builder()
                .fromUri(getString(R.string.mapbox_style_url))
                .withBitmapImages(*ar.toTypedArray())
        ) {
            addPois(mapboxMap)

        }
    }

    private fun addPois(mapboxMap: MapboxMap) {
        homeViewModel.getAllPoi().observe(viewLifecycleOwner, { result ->

            when (result.status) {
                Result.Status.SUCCESS -> {
                    if (result.data.isNullOrEmpty().not()) {

                        lifecycleScope.launch {

                            val features = homeViewModel.poiFeatureList(result.data!!)
                            homeViewModel.addSymbolLayer(
                                mapboxMap, "source_floor_1",
                                "layer_floor_1", features,
                                requireContext()
                            )
                        }
                    }
                }
            }

        })
    }

}