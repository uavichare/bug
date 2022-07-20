package com.example.buglibrary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.SDKActivity
import com.example.buglibrary.R
import com.example.buglibrary.data.Poi
import com.example.buglibrary.databinding.ItemSearchBinding
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.utils.SearchDiffUtilCallback

class SearchAdapter(val mainActivity: SDKActivity) :
    ListAdapter<Poi, SearchAdapter.SearchViewHolder>(SearchDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemBinding =
            ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchViewHolder(private val itemBinding: ItemSearchBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        private lateinit var poi: Poi

        init {
            itemBinding.root.setOnClickListener(this)
        }

        fun bind(poi: Poi) {
            this.poi = poi
            itemBinding.imgSearchPoi.setImageResource(getImage(poi))
            itemBinding.textSearchPoi.text =
                when (LocaleManager.getLanguage(itemBinding.root.context)) {
                    LocaleManager.LANGUAGE_ARABIC -> {
                        poi.poiMultilingual?.arabic?.name
                    }

                    else -> {
                        poi.poiMultilingual?.en?.name

                    }
                }
        }

        override fun onClick(p0: View?) {
            mainActivity.clearSearch()
            val bundle = Bundle()
            bundle.putParcelable("poi", poi)
            Navigation.findNavController(mainActivity.navHostFragment)
                .navigate(R.id.poiDetailFragment, bundle)
        }

        private fun getImage(poi: Poi): Int {
            return when (poi.subcategory.toLowerCase(java.util.Locale.ROOT)) {
                "restaurants" -> {
                    R.drawable.food_beverages_vendors
                }
                "hotels" -> {
                    R.drawable.hotel
                }
                "houses of safeguarding the emirati heritage ( museum)" -> {
                    R.drawable.frame
                }
                "institutions(office)" -> {
                    R.drawable.institution
                }
                "artistic houses for professional talents" -> {
                    R.drawable.artistic_houses
                }
                "retail" -> {
                    R.drawable.retail
                }
                "parking" -> {
                    R.drawable.parking
                }
                "mosque" -> {
                    R.drawable.mosque
                }
                "toilet" -> {
                    R.drawable.amenities_toilet
                }
                else -> {
                    R.drawable.img_placeholder
                }

            }
        }
    }
}