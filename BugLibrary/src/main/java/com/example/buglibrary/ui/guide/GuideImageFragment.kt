package com.example.buglibrary.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.buglibrary.R
import com.example.buglibrary.ui.home.PoiBannerFragment


private const val ARG_POI_MEDIA = "poiMedia"

/**
 * A simple [Fragment] subclass.
 * Use the [PoiBannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuideImageFragment : Fragment() {

    private var picUrl: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            picUrl = it.getInt(ARG_POI_MEDIA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_poi_banner, container, false)
        (view as ImageView).setImageResource(picUrl!!)
        view.scaleType = ImageView.ScaleType.FIT_XY

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param picUrl Parameter 1.
         * @return A new instance of fragment PoiBannerFragment.
         */
        @JvmStatic
        fun newInstance(picUrl: Int) =
            GuideImageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POI_MEDIA, picUrl)
                }
            }
    }
}