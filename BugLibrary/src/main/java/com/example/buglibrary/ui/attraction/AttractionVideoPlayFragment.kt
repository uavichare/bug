package com.example.buglibrary.ui.attraction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.buglibrary.databinding.AttractionVideoLayoutBinding
import com.example.buglibrary.di.Injectable

class AttractionVideoPlayFragment : Fragment(), Injectable {

    private var _binding: AttractionVideoLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AttractionVideoLayoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playVideo(arguments?.getString("videoLink"))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun playVideo(stream: String?) {
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.VideoViewPlayer)
        binding.VideoViewPlayer.setMediaController(mediaController)
        binding.VideoViewPlayer.setVideoURI(stream!!.toUri())
        binding.VideoViewPlayer.requestFocus()
        binding.VideoViewPlayer.start()
        binding.VideoViewPlayer.setOnPreparedListener {
            binding.progressBar.visibility = View.GONE
        }
    }

}