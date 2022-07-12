package com.example.buglibrary.ui.home

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.buglibrary.R
import com.example.buglibrary.data.Poi
import com.example.buglibrary.databinding.FragmentAudioPlayerBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioPlayerFragment : BottomSheetDialogFragment() {

    private var mediaPlayer: MediaPlayer? = null
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private var poiDetail: Poi? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        val args = AudioPlayerFragmentArgs.fromBundle(requireArguments())

        poiDetail = args.poi
        lifecycleScope.launch {
            delay(300)
            Log.d("TAG", "c: ${poiDetail?.audioListUrl?.get(0)?.originalUrl!!}")
            playMedia(poiDetail?.audioListUrl?.get(0)?.originalUrl!!)

            binding.textTime.text = mediaPlayer?.duration.toString()
            binding.seekBar.max = mediaPlayer?.duration!!
        }


        binding.buttonPlay.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                   binding.buttonPlay.setIconResource(R.drawable.ic_round_pause)
                }else{
                    it.start()
                    binding.buttonPlay.setIconResource(R.drawable.ic_round_play_arrow)
                }
            }

        }
        binding.buttonCancel.setOnClickListener { dismiss() }


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onPause() {
        super.onPause()
        mediaPlayer?.release()
        mediaPlayer = null

    }

    override fun dismiss() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.dismiss()
    }

    private fun playMedia(url: String) {

        mediaPlayer = MediaPlayer().apply {

            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepareAsync() // might take long! (for buffering, etc)
            setOnPreparedListener {
                val executorService = Executors.newScheduledThreadPool(1)
                executorService.scheduleWithFixedDelay({
                    binding.seekBar.progress = it.currentPosition
                    Log.d("TAG", "playMedia: ${it.currentPosition}")
//                    binding.textTime.text = mediaPlayer?.duration.toString()
                }, 2, 2, TimeUnit.SECONDS)

                binding.textTime.text =  DateUtils.formatElapsedTime(mediaPlayer?.duration!!.toLong()/1000)
                binding.seekBar.max = mediaPlayer?.duration!!
                start()
            }

        }

    }
}