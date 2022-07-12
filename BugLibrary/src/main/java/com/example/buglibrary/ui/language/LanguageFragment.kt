package com.example.buglibrary.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.databinding.FragmentLanguageBinding


/**
 * A simple [Fragment] subclass.
 * Use the [LanguageFragment] factory method to
 * create an instance of this fragment.
 */
class LanguageFragment : Fragment() {

    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        binding.recyclerViewLanguage.layoutManager = LinearLayoutManager(context)
        val list = ArrayList<String>()
        list.add("English")
        list.add("Arabic - العربية")
        binding.recyclerViewLanguage.adapter = LangAdapter(list)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}