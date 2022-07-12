package com.example.buglibrary.ui.terms

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.buglibrary.data.PrivacyTerms
import com.example.buglibrary.databinding.TermsPrivacyFragmentBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.utils.FileUtils
import com.example.buglibrary.utils.ext.injectViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TermsPrivacyFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = TermsPrivacyFragment()
    }

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private var _binding: TermsPrivacyFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TermsPrivacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = injectViewModel(viewModelProvider)
        _binding = TermsPrivacyFragmentBinding.inflate(inflater, container, false)
        val type = arguments?.getInt(Intent.EXTRA_TEXT)

        var fileName = "privacy_policy.json"
        if (type == 3) {
            fileName = "TermsAndConditions.json"
            binding.textTitle.text = getString(R.string.menu_terms_condition)
//            viewModel.getTerms(getString(R.string.app_token))
        } else {
            binding.textTitle.text = getString(R.string.menu_privacy)
//            viewModel.getPrivacyAndAbout(getString(R.string.app_token))
        }
        val jsonFile = FileUtils.loadJSONFromAsset(requireContext(), fileName)


      //  binding.layoutVersion.version.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.US)

/*
        binding.layoutVersion.date.text =
            dateFormat.format(Date(BuildConfig.BUILD_TIME.toLong()))
*/
        val listType =
            object : TypeToken<PrivacyTerms>() {}.type
        val privacyTerms = Gson().fromJson<PrivacyTerms>(jsonFile, listType)
        val adapter = TermsPrivacyAdapter()
        binding.recyclerTermsPrivacy.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTermsPrivacy.setHasFixedSize(true)
        binding.recyclerTermsPrivacy.adapter = adapter
        adapter.submitList(privacyTerms.Sections)
        /* observer.observe(viewLifecycleOwner, { result ->
             when (result.status) {
                 Result.Status.LOADING -> {
                     binding.shimmer.shimmer.startShimmer()
                 }
                 Result.Status.SUCCESS -> {

                     when (type) {
                         1 -> {
                             binding.aboutUsInsideLayout.visibility = View.GONE
                             binding.textTitle.text = getString(R.string.about_us_text)
                             binding.aboutTitle.text = getString(R.string.about_app)
                             binding.aboutDesc.text =
                                 if (LocaleManager.getLanguage(requireContext()) == LocaleManager.LANGUAGE_ARABIC) {
                                     resources.getString(R.string.about_us_description_text)
                                 } else {
                                     resources.getString(R.string.about_us_description_text)
                                 }
                         }
                         2 -> {
                             binding.aboutUsInsideLayout.visibility = View.VISIBLE


                             binding.aboutTitle.text = getString(R.string.menu_privacy)
                             binding.aboutDesc.text = getString(R.string.privacy_policy_text)
                             binding.aboutTitleSecond.text = getString(R.string.privacy_policy_2)
                             binding.aboutDescSecond.text =
                                 getString(R.string.privacy_policy_2_descp)

                         }
                         3 -> {
                             binding.aboutUsInsideLayout.visibility = View.GONE


                             binding.aboutTitle.text = getString(R.string.menu_terms_condition)
                             binding.aboutDesc.text =
                                 if (LocaleManager.getLanguage(requireContext()) == LocaleManager.LANGUAGE_ARABIC) {
                                     result.data?.data?.text?.arabic
                                 } else {
                                     result.data?.data?.text?.en
                                 }
                         }
                     }

                     binding.shimmer.shimmer.visibility = View.GONE
                     binding.shimmer.shimmer.hideShimmer()
                 }
                 Result.Status.ERROR -> {
                     binding.shimmer.shimmer.visibility = View.GONE
                     binding.shimmer.shimmer.hideShimmer()
                 }

             }
         })*/
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}