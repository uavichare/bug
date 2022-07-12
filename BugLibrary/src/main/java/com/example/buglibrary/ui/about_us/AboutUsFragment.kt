package com.example.buglibrary.ui.about_us

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.databinding.NewAboutUsMainLayoutBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject

class AboutUsFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = AboutUsFragment()
    }

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private var binding: NewAboutUsMainLayoutBinding? = null
    private lateinit var viewModel: AboutUsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = injectViewModel(viewModelProvider)
        binding = NewAboutUsMainLayoutBinding.inflate(inflater, container, false)



        binding!!.aboutUsRecycleriew.layoutManager = LinearLayoutManager(context)
        val aboutUsAdapter = AboutUsAdapter(this)

        viewModel.prepareList(requireContext()).observe(viewLifecycleOwner, {
            binding!!.aboutUsRecycleriew.adapter = aboutUsAdapter
            aboutUsAdapter.submitList(it)

        })
        return binding!!.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}