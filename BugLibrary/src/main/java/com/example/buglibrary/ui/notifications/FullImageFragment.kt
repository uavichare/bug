package com.example.buglibrary.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.buglibrary.databinding.FragmentFullImageBinding


/**
 * A simple [Fragment] subclass.
 * Use the [FullImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FullImageFragment : Fragment() {


    private var _binding: FragmentFullImageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFullImageBinding.inflate(inflater, container, false)
        val args = FullImageFragmentArgs.fromBundle(requireArguments())

        val mediaUrl = args.poiImage
/*
        GlideApp.with(requireContext()).load(mediaUrl)
            .placeholder(R.drawable.img_placeholder)
            .into(binding.imgFullscreen)
*/
        return binding.root
    }


}