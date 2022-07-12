package com.example.buglibrary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.buglibrary.R
import com.example.buglibrary.data.PoiMedia


private const val ARG_POI_MEDIA = "poiMedia"

/**
 * A simple [Fragment] subclass.
 * Use the [PoiBannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoiBannerFragment : Fragment() {

    private var poiMedia: PoiMedia? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            poiMedia = it.getParcelable(ARG_POI_MEDIA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_poi_banner, container, false)
/*
        GlideApp.with(requireContext()).load(poiMedia?.imgurl)
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_placeholder)
            .into(view as ImageView)
*/
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param poiMedia Parameter 1.
         * @return A new instance of fragment PoiBannerFragment.
         */
        @JvmStatic
        fun newInstance(poiMedia: PoiMedia) =
            PoiBannerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_POI_MEDIA, poiMedia)
                }
            }
    }
}