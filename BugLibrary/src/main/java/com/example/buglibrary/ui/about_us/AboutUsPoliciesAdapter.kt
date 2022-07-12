package com.example.buglibrary.ui.about_us

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.R
import com.example.buglibrary.databinding.NewPoliciesLayoutBinding
import com.example.buglibrary.manager.LocaleManager


class AboutUsPoliciesAdapter(private val strLocale: List<PrivacyModel>) :
    RecyclerView.Adapter<AboutUsPoliciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            NewPoliciesLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(strLocale[position])

    }

    override fun getItemCount(): Int {
        return strLocale.size
    }

    inner class ViewHolder(private val itemBinding: NewPoliciesLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)

        }

        fun bind(data: PrivacyModel) {
            val rotation =
                if (LocaleManager.getLanguage(itemBinding.root.context) == LocaleManager.LANGUAGE_ARABIC) 180f else 0f

            itemBinding.imgExpand.rotation = rotation
            itemBinding.privatePoliciesText.text = data.privacy_text
            itemBinding.privatePoliciesTitle.text = data.privacy_title

        }

        override fun onClick(p0: View?) {
            if(adapterPosition==0) {
                val bundle = Bundle()
                bundle.putInt(Intent.EXTRA_TEXT, 2)
                Navigation.findNavController(p0!!)
                    .navigate(R.id.termsPrivacyFragment, bundle)
            }
            else if(adapterPosition==1)
            {
                val bundle = Bundle()
                bundle.putInt(Intent.EXTRA_TEXT, 3)
                Navigation.findNavController(p0!!)
                    .navigate(R.id.termsPrivacyFragment, bundle)

            }

        }
    }
}