package com.example.buglibrary.ui.newcontactus


import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.R
import com.example.buglibrary.databinding.*

import com.example.buglibrary.utils.IntentUtils
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions


class NewContactUsAdapter(val homeFragment: NewContactUsFragment) :
    ListAdapter<NewContactUsModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (NewContactUsComponent.values()[viewType]) {
            NewContactUsComponent.MAP_SECTION -> {
                val binding = NewContactUsLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MapSectionViewHolder(binding)
            }
            NewContactUsComponent.REACH_US -> {
                val itemInfoBinding =
                    ReachUsLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                ReachUsViewHolder(itemInfoBinding)
            }

            NewContactUsComponent.WORKING_HOURS -> {
                val itemSaveFlightBinding = WorkHourMainLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                WorkingHourViewHolder(itemSaveFlightBinding)
            }

            NewContactUsComponent.SUGGESSTIONS_AND_COMPLAINS -> {
                val itemSaveFlightBinding = SuggestionComplaintLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SuggestionComplaintViewHolder(itemSaveFlightBinding)
            }


            NewContactUsComponent.SOCIAL_MEDIA -> {
                val itemSaveFlightBinding = SocialMediaLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SocialMediaViewHolder(itemSaveFlightBinding)
            }


        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is MapSectionViewHolder -> {
                holder.bind()
            }
            is ReachUsViewHolder -> {
                holder.bind()
            }

            is WorkingHourViewHolder -> {
                holder.bind()
            }
            is SuggestionComplaintViewHolder -> {
                holder.bind()
            }
            is SocialMediaViewHolder -> {
                holder.bind()
            }


            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return getItem(position).component.ordinal
    }

    inner class MapSectionViewHolder(private val binding: NewContactUsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

            binding.contactUsMapbox.getMapAsync { mapboxMap ->

                mapboxMap.setStyle(
                    Style.LIGHT
                ) { style ->

                    val symbolManager = SymbolManager(binding.contactUsMapbox, mapboxMap, style)
                    symbolManager.iconAllowOverlap = true
                    style.addImage(
                        "myMarker",
                        BitmapFactory.decodeResource(
                            binding.root.context.resources,
                            R.drawable.marker_pin
                        )
                    )
                    symbolManager.create(
                        SymbolOptions()
                            .withLatLng(LatLng(25.187762, 55.297091))
                            .withIconImage("myMarker")

                    )
                }


            }

        }
    }


    inner class WorkingHourViewHolder(binding: WorkHourMainLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {


        }
    }


    inner class SuggestionComplaintViewHolder(binding: SuggestionComplaintLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val context = binding.root.context

        init {
            binding.esuggest.setOnClickListener(this)
            binding.eComplain.setOnClickListener(this)
        }

        fun bind() {
        }

        override fun onClick(p0: View?) {
            when (p0?.id) {
                R.id.esuggest -> {
                    IntentUtils.urlIntent(context, "https://esuggest.dubai.gov.ae/")
                }

                R.id.e_complain -> {


                    IntentUtils.urlIntent(context, "https://ecomplain.dubai.gov.ae/")
                }
            }

        }


    }


    inner class SocialMediaViewHolder(private val binding: SocialMediaLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

            binding.facebook.setOnClickListener {
                IntentUtils.urlIntent(
                    homeFragment.requireContext(),
                    "https://www.facebook.com/DubaiCulture/"
                )
            }
            binding.instgram.setOnClickListener {

                IntentUtils.urlIntent(
                    homeFragment.requireContext(),
                    "https://www.instagram.com/dubaiculture/".split(",").joinToString()
                )


            }
            binding.twitter.setOnClickListener {
                IntentUtils.urlIntent(
                    homeFragment.requireContext(),
                    "twitter.com/Dubaiculture".split(",").joinToString()
                )
                /* IntentUtils.openWebPage(
                     homeFragment.requireContext(),
                     "twitter.com/Dubaiculture".split(",").joinToString()
                 )*/
            }
            binding.youtube.setOnClickListener {
                IntentUtils.openWebPage(
                    homeFragment.requireContext(),
                    "https://www.youtube.com/user/DubaiCulture".split(",").joinToString()
                )
            }

        }
    }


    inner class ReachUsViewHolder(private val binding: ReachUsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

            binding.phoneText.setOnClickListener {
                IntentUtils.dialPhoneNumber(homeFragment.requireContext(), "80033222")
            }

            binding.emailText.setOnClickListener {

                IntentUtils.composeEmail(
                    homeFragment.requireContext(),
                    "info@dubaiculture.ae".split(",").toTypedArray(),
                    ""
                )
            }
            binding.websiteText.setOnClickListener {
                IntentUtils.urlIntent(
                    homeFragment.requireContext(),
                    "https://dubaiculture.gov.ae/ar/Pages/default.aspx"
                )

            }
            binding.feedbackText.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.feedback_Fragment)

            }

        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewContactUsModel>() {
            override fun areItemsTheSame(
                oldItem: NewContactUsModel,
                newItem: NewContactUsModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: NewContactUsModel,
                newItem: NewContactUsModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}


