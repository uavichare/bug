package com.example.buglibrary.ui.onbording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.buglibrary.R

private const val ARG_PAGER_POSTION = "position"

/**
 * A simple [Fragment] subclass.
 * Use the [OnBoardingPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoardingPagerFragment : Fragment() {

    private var param1: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PAGER_POSTION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_on_boarding_pager, container, false)
        val boardingTitle: TextView = view.findViewById(R.id.boarding_title)
        val boardingText: TextView = view.findViewById(R.id.boarding_title_message)
        val boardingImage: ImageView = view.findViewById(R.id.boarding_image)
        when (param1) {
            0 -> {
                boardingTitle.text = getString(R.string.on_boarding_title_1)
                boardingText.text = getString(R.string.on_boarding_desc_1)

                boardingImage.setImageResource(R.drawable.img_onboarding_1)
            }
            1 -> {
                boardingTitle.text = getString(R.string.on_boarding_title_2)
                boardingText.text = getString(R.string.on_boarding_desc_2)

                boardingImage.setImageResource(R.drawable.img_way_finding)

            }
            2 -> {
                boardingTitle.text = getString(R.string.on_boarding_title_3)
                boardingText.text = getString(R.string.on_boarding_desc_3)

                boardingImage.setImageResource(R.drawable.img_onboarding_3)

            }
            3 -> {
                boardingTitle.text = getString(R.string.on_boarding_title_4)
                boardingText.text = getString(R.string.on_boarding_desc_4)
                boardingImage.setImageResource(R.drawable.img_onboarding_4)

            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment OnBoardingPagerFragment.
         */
        @JvmStatic
        fun newInstance(param1: Int) =
            OnBoardingPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PAGER_POSTION, param1)
                }
            }
    }
}