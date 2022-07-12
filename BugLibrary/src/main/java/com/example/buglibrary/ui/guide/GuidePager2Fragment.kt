package com.example.buglibrary.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.buglibrary.R
import com.example.buglibrary.databinding.FragmentGuidePager2Binding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GuidePager2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuidePager2Fragment : Fragment() {
    private var _binding: FragmentGuidePager2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGuidePager2Binding.inflate(inflater, container, false)
        binding.textDetail.text = HtmlCompat.fromHtml(
            getString(R.string.guide_desc),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        return binding.root
    }


}