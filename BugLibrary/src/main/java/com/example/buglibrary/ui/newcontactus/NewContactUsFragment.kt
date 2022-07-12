package com.example.buglibrary.ui.newcontactus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.databinding.NewContactUsMainLayoutBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject

class NewContactUsFragment : Fragment(), Injectable {
    companion object {
        fun newInstance() = NewContactUsFragment()
    }

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private var binding: NewContactUsMainLayoutBinding? = null
    private lateinit var viewModel: NewContactUsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = injectViewModel(viewModelProvider)
        binding = NewContactUsMainLayoutBinding.inflate(inflater, container, false)
        binding!!.contactUsRecyclerview.layoutManager = LinearLayoutManager(context)

        val aboutUsAdapter = NewContactUsAdapter(this)
        viewModel.contactUsList.observe(viewLifecycleOwner, {
            binding!!.contactUsRecyclerview.adapter = aboutUsAdapter
            aboutUsAdapter.submitList(it)
            aboutUsAdapter.notifyDataSetChanged()
        })
        return binding!!.root
    }
}
