package com.example.buglibrary.ui.terms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.data.Section
import com.example.buglibrary.databinding.ItemPrivacyTermsBinding
import com.example.buglibrary.manager.LocaleManager

class TermsPrivacyAdapter :
    ListAdapter<Section, TermsPrivacyAdapter.PolicyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolicyViewHolder {

        val binding = ItemPrivacyTermsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PolicyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: PolicyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class PolicyViewHolder(private val binding: ItemPrivacyTermsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context
        fun bind(home: Section) {
            val title: String
            val desc: String
            if (LocaleManager.getLanguage(context) == LocaleManager.LANGUAGE_ARABIC) {
                title = home.SectionName_AR
                desc = home.SectionDetails_AR
            } else {
                title = home.SectionName
                desc = home.SectionDetails
            }

            binding.textTitle.text = title
            binding.textDesc.text = desc
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Section>() {
            override fun areItemsTheSame(oldItem: Section, newItem: Section): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Section, newItem: Section): Boolean {
                return newItem.SectionName == oldItem.SectionName
            }
        }
    }
}