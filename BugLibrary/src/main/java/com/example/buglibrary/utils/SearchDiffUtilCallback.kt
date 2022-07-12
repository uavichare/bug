package com.example.buglibrary.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.buglibrary.data.Poi

/**
 * Created by Furqan on 22-03-2018.
 */
class SearchDiffUtilCallback : DiffUtil.ItemCallback<Poi>() {
    override fun areItemsTheSame(oldItem: Poi, newItem: Poi): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Poi, newItem: Poi): Boolean {
        return oldItem.poiMultilingual?.en == newItem.poiMultilingual?.en
    }

/* override fun areItemsTheSame(oldItem: NlpChatModel, newItem: NlpChatModel): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: NlpChatModel, newItem: NlpChatModel): Boolean {
        return oldItem.msg == newItem.msg
    }*/
}