package com.example.buglibrary.ui.about_us

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.R
import com.example.buglibrary.databinding.NewAboutUsLayoutBinding
import com.example.buglibrary.databinding.NewLibrarySelectLayoutBinding
import com.example.buglibrary.databinding.NewPoliciesMainLayoutBinding

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.example.buglibrary.manager.LocaleManager
import java.text.SimpleDateFormat
import java.util.*

class AboutUsAdapter(val homeFragment: AboutUsFragment) :
    ListAdapter<Home, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (Component.values()[viewType]) {
            Component.ABOUT_THE_APP -> {
                val binding = NewAboutUsLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AboutTheAppViewHolder(binding)
            }
            Component.POLICIES -> {
                val itemInfoBinding =
                    NewPoliciesMainLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                PolicieViewHolder(itemInfoBinding)
            }
            Component.LIBRARY_WE_USE -> {
                val itemSaveFlightBinding = NewLibrarySelectLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LibraryWeUseViewHolder(itemSaveFlightBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is AboutTheAppViewHolder -> {
                holder.bind()
            }
            is PolicieViewHolder -> {
                holder.bind(getItem(position).data as List<PrivacyModel>)
            }
            is LibraryWeUseViewHolder -> {
                holder.bind()
            }
            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return getItem(position).component.ordinal
    }

    inner class AboutTheAppViewHolder(private val binding: NewAboutUsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
/*
            binding.layoutVersion.version.text =
                binding.root.context.getString(R.string.version, BuildConfig.VERSION_NAME)
*/
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

/*
            binding.layoutVersion.date.text =
                dateFormat.format(Date(BuildConfig.BUILD_TIME.toLong()))
*/
            binding.textTitle.text = homeFragment.getString(R.string.about_us_text)
            binding.aboutTitle.text = homeFragment.getString(R.string.about_app)
            binding.aboutDesc.text = homeFragment.getString(R.string.about_us_description_text)
        }
    }

    inner class LibraryWeUseViewHolder(private val binding: NewLibrarySelectLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context

        fun bind() {
            val rotation =
                if (LocaleManager.getLanguage(binding.root.context) == LocaleManager.LANGUAGE_ARABIC) 180f else 0f

            binding.aboutUsInsideLayout.imgExpand.rotation = rotation
            binding.aboutUsInsideLayout.privatePoliciesTitle.text =
                context.getString(R.string.used_library)
            binding.aboutUsInsideLayout.privatePoliciesText.text =
                context.getString(R.string.click_view)
            binding.aboutUsInsideLayout.aboutUsInsideLayout.setOnClickListener {
                OssLicensesMenuActivity.setActivityTitle(context.getString(R.string.libraray_we_use))
                homeFragment.requireContext().startActivity(
                    Intent(
                        homeFragment.requireContext(),
                        OssLicensesMenuActivity::class.java
                    )
                )

            }

        }


    }


    inner class PolicieViewHolder(private val binding: NewPoliciesMainLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context
        fun bind(home: List<PrivacyModel>) {
            binding.newPolicyRecyclerview.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            val adapter = AboutUsPoliciesAdapter(home)
            binding.newPolicyRecyclerview.adapter = adapter
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Home>() {
            override fun areItemsTheSame(oldItem: Home, newItem: Home): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Home, newItem: Home): Boolean {
                return oldItem == newItem
            }
        }
    }
}
