package com.example.buglibrary.ui.registration

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.buglibrary.R
import com.example.buglibrary.data.User
import com.example.buglibrary.databinding.FragmentThanksBinding
import com.example.buglibrary.utils.CommonUtils


class ThanksFragment : Fragment() {
    private var _binding: FragmentThanksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentThanksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val user = arguments?.getParcelable(Intent.EXTRA_TEXT) as User?
        user?.let {
            if (user.firstName.isNotEmpty()) {
                val userLength = user.firstName.split(" ")
                if (userLength.size >= 2) {
                    binding.txtInitial.text = userLength[0].take(1) + userLength[1].take(1)
                } else {
                    binding.txtInitial.text = user.firstName.take(1)
                }
                if (user.profilePic.isNotEmpty()) {
                    val roundCorner = CommonUtils.pxFromDp(view.context, 64f)
                    Glide.with(requireContext()).load(user.profilePic)
                        .transform(CenterCrop(), RoundedCorners(roundCorner.toInt()))
                        .into(binding.imgUser)
                    binding.imgUser.visibility = View.VISIBLE
                    binding.txtInitial.visibility = View.INVISIBLE
                } else {
                    binding.imgUser.visibility = View.INVISIBLE
                }
            }
            val span = SpannableStringBuilder()
                .append("Hello ")
                .color(ContextCompat.getColor(requireContext(), R.color.purple_700)) {
                    bold {
                        append(user.firstName)
                    }
                }
            binding.name.text = span
        }

        binding.doneButton.setOnClickListener {
            Navigation.findNavController(requireView())
                .popBackStack(R.id.nav_home, false)

        }

    }
}