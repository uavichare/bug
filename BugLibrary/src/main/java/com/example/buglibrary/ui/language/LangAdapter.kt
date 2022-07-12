package com.example.buglibrary.ui.language

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.MainActivity
import com.example.buglibrary.databinding.FragmentLangItemBinding
import com.example.buglibrary.manager.LocaleManager

/**
 * Created by Furqan on 24-07-2019.
 */
class LangAdapter(private val strLocale: List<String>) :
    RecyclerView.Adapter<LangAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            FragmentLangItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(strLocale[position])

    }

    override fun getItemCount(): Int {
        return strLocale.size
    }

    inner class ViewHolder(private val itemBinding: FragmentLangItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        private val context = itemBinding.root.context

        init {
            itemView.setOnClickListener(this)
            itemBinding.radioButton.setOnClickListener(this)

        }

        fun bind(data: String) {
            itemBinding.textLang.text = data
            itemBinding.radioButton.isChecked = when (adapterPosition) {
                1 -> {
                    LocaleManager.getLanguage(context) == LocaleManager.LANGUAGE_ARABIC
                }
                else -> {
                    LocaleManager.getLanguage(context) == LocaleManager.LANGUAGE_ENGLISH
                }
            }

        }

        override fun onClick(p0: View?) {
            val language = when (adapterPosition) {
                0 -> {
                    LocaleManager.LANGUAGE_ENGLISH
                }
                1 -> {
                    LocaleManager.LANGUAGE_ARABIC
                }
                else -> {
                    LocaleManager.LANGUAGE_ENGLISH
                }
            }
            LocaleManager.setNewLocale(itemView.context, language)
            val i = Intent(itemView.context, MainActivity::class.java)
            itemView.context.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}