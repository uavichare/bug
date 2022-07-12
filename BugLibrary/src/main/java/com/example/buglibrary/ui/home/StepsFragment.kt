package com.example.buglibrary.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.data.NavDetails
import com.example.buglibrary.databinding.FragmentStepsBinding


private const val ARG_NAV_DETAIL = "navDetail"

class StepsFragment : Fragment() {
    private var navDetail: ArrayList<NavDetails>? = null
    private lateinit var binding: FragmentStepsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navDetail = it.getParcelableArrayList(ARG_NAV_DETAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStepsBinding.inflate(inflater, container, false)

        binding.textEta.text = arguments?.getString(Intent.EXTRA_TEXT, "")
        binding.recyclerViewSteps.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSteps.adapter = StepsAdapter(navDetail!!)
        return binding.root
    }


}