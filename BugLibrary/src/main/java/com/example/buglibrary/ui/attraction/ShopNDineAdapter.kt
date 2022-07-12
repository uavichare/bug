package com.example.buglibrary.ui.attraction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.data.Poi
import com.example.buglibrary.databinding.ItemShopNDineBinding

class ShopNDineAdapter(val fragment: Fragment, private val poiList: List<Poi>) :
    RecyclerView.Adapter<ShopNDineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopNDineViewHolder {
        val itemBinding =
            ItemShopNDineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopNDineViewHolder(fragment, itemBinding)
    }

    override fun onBindViewHolder(holder: ShopNDineViewHolder, position: Int) {
        holder.bindData(poiList[position])
    }

    override fun getItemCount() = poiList.size
}