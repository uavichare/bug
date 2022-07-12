package com.example.buglibrary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.buglibrary.R
import com.example.buglibrary.databinding.FragmentTripSummaryDialogBinding
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.utils.IntentUtils


/**
 * A simple [Fragment] subclass.
 * Use the [TripSummaryDialogFragment] factory method to
 * create an instance of this fragment.
 */
class TripSummaryDialogFragment : DialogFragment() {

    private var _binding: FragmentTripSummaryDialogBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTripSummaryDialogBinding.inflate(inflater, container, false)
        val args = TripSummaryDialogFragmentArgs.fromBundle(requireArguments())
        val poi = args.poi
        val poiName = when (LocaleManager.getLanguage(requireContext())) {
            LocaleManager.LANGUAGE_ARABIC -> {
                poi?.poiMultilingual?.arabic?.name

            }

            else -> {
                poi?.poiMultilingual?.en?.name

            }
        }
        binding.arrivedText.text = getString(R.string.arrived_at, "\n $poiName")
        binding.textDistance.text = "${args.distance.toInt()} m"
        binding.textStep.text = "${(args.distance * 1.7).toInt()}"
        binding.buttonShareExp.setOnClickListener {
            findNavController()
                .navigate(R.id.happinessFragment)
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonShare.setOnClickListener {
            IntentUtils.shareText(requireContext(), "Hey Check my trip info from ", "Trip Info")
        }
        return binding.root
    }

}