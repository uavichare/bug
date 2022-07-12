package com.example.buglibrary.ui.newcontactus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.buglibrary.R
import com.example.buglibrary.data.Result
import com.example.buglibrary.databinding.FeedbackFragmentBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.utils.CommonUtils.Companion.isValidEmail
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject


class FeedbackFragment : Fragment(), Injectable {
    companion object {
        fun newInstance() = FeedbackFragment()
    }

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private lateinit var viewModel: NewContactUsViewModel

    private var _binding: FeedbackFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FeedbackFragmentBinding.inflate(inflater, container, false)
        viewModel = injectViewModel(viewModelProvider)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchFeedbackType(LocaleManager.getLanguage(requireContext()))
            .observe(viewLifecycleOwner, {
                when (it.status) {
                    Result.Status.SUCCESS -> {
                        it.data?.let { type ->
                            val arrayList = ArrayList<String>()
                            arrayList.add(getString(R.string.type))
                            arrayList.addAll(type.GetFeedBackSubjectsResult)
                            val dataAdapter = ArrayAdapter<String>(
                                requireContext(),
                                R.layout.spinner_item,
                                arrayList
                            )
                            dataAdapter.setDropDownViewResource(R.layout.spinner_item)

                            binding.etType.adapter = dataAdapter

                        }

                    }
                }
            })
        binding.btnSubmit.setOnClickListener {
            binding.txtError.visibility = View.INVISIBLE

            when {
                binding.etEmail.text.trim().isNullOrEmpty() -> {
                    binding.txtError.text = ("Enter email please")
                    binding.txtError.visibility = View.VISIBLE

                }
                isValidEmail(binding.etEmail.text).not() -> {
                    binding.txtError.text = getString(R.string.error_email)
                    binding.txtError.visibility = View.VISIBLE

                }
                binding.etName.text.trim().isNullOrEmpty() -> {
                    binding.txtError.text = ("Please Enter Name")
                    binding.txtError.visibility = View.VISIBLE
                }
                binding.etType.selectedItem.toString() == "Type" -> {
                    binding.txtError.text = ("Please Select Type")
                    binding.txtError.visibility = View.VISIBLE
                }
                binding.etSubject.text.trim().isNullOrEmpty() -> {
                    binding.txtError.text = ("Please Enter Subject")
                    binding.txtError.visibility = View.VISIBLE
                }
                binding.etFeedback.text.trim().isNullOrEmpty() -> {
                    binding.txtError.text = ("Please Enter Message")
                    binding.txtError.visibility = View.VISIBLE
                }
                /* binding.ratingBarFeedback.rating < 1 -> {
                     binding.txtError.text = ("please select rating")
                     binding.txtError.visibility = View.VISIBLE

                 }*/
                else -> {

                    val observer = viewModel.submitFeedback(
                        binding.etName.text.toString(),
                        binding.etEmail.text.toString(),
                        binding.etSubject.text.toString(),
                        binding.etFeedback.text.toString(),
                        binding.etType.selectedItem.toString()
                    )
                    observer.observe(viewLifecycleOwner, { result ->
                        when (result.status) {
                            Result.Status.LOADING -> {
                                showLoader(true)

                            }
                            Result.Status.SUCCESS -> {

                                showLoader(false)
                                result.data?.let {
                                    if (it.isNotEmpty()) {
                                        val message =
                                            getString(R.string.feedback_thank_msg, it[0].RefNumber)
                                        confirmationDialog(message)
                                    }
                                }


                            }
                            Result.Status.ERROR -> {

                            }

                        }
                    })
                }
            }

        }

    }

    private fun confirmationDialog(message: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(
            "OK"
        ) { _, _ ->
            findNavController().popBackStack()
        }

        alertDialog.show()

    }

    private fun showLoader(isShow: Boolean) {
        if (isShow) {
            binding.txtSubmit.visibility = View.GONE
            binding.progressBarLoader.visibility = View.VISIBLE
        } else {
            binding.txtSubmit.visibility = View.VISIBLE
            binding.progressBarLoader.visibility = View.GONE
        }

    }

}
