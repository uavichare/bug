package com.example.buglibrary.ui.attraction

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.buglibrary.R
import com.example.buglibrary.data.Poi
import com.example.buglibrary.data.Result
import com.example.buglibrary.databinding.ItemShopNDineBinding
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.ui.favourite.FavouriteFragment
import com.example.buglibrary.utils.CommonUtils

class ShopNDineViewHolder(val fragment: Fragment, private val itemBinding: ItemShopNDineBinding) :
    RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {
    private val context =
        itemBinding.root.context
    private lateinit var poi: Poi
//    private val roundCorner = CommonUtils.pxFromDp(context, 4f)

    init {
        itemBinding.imgFavorite.setOnClickListener(this)
        itemBinding.root.setOnClickListener(this)
    }

    fun bindData(poi: Poi) {
        this.poi = poi
        // view.textShopName.text = poi.poiMultilingual?.en?.name

        when (LocaleManager.getLanguage(context)) {
            LocaleManager.LANGUAGE_ARABIC -> {
                itemBinding.textShopName.text =
                    if (poi.poiMultilingual?.arabic?.name.isNullOrEmpty()) {
                        poi.poiMultilingual?.en?.name
                    } else
                        poi.poiMultilingual?.arabic?.name
            }
            LocaleManager.LANGUAGE_CHINESE -> {
                itemBinding.textShopName.text =
                    if (poi.poiMultilingual?.chinese?.name.isNullOrEmpty()) {
                        poi.poiMultilingual?.en?.name
                    } else
                        poi.poiMultilingual?.chinese?.name
            }
            else -> {
                itemBinding.textShopName.text = poi.poiMultilingual?.en?.name

            }
        }
        if (fragment is FavouriteFragment) {
            itemBinding.imgFavorite.setImageResource(R.drawable.ic_favorite_filled)
            poi.isFavourite = true
//            itemBinding.imgFavorite.visibility = View.GONE
        } else {
            val favImg = if (poi.isFavourite) {
                R.drawable.ic_favorite_filled
            } else
                R.drawable.ic_favorite
            itemBinding.imgFavorite.setImageResource(favImg)
        }
// hide due to not in arabic
//        if (poi.keywords != null)
//            itemBinding.textKeyword.text = poi.keywords.joinToString()

        poi.picUrl?.let {
            if (it.isNotEmpty()) {
/*
                GlideApp.with(context).load(poi.picUrl[0].imgurl)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(CommonUtils.pxFromDp(context, 5f).toInt())
                    )
                    .into(itemBinding.imgShop)
*/

            }
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_favorite -> {
                poi.isFavourite = !poi.isFavourite
                val deviceId = CommonUtils.getDeviceId(context)
                when (fragment) {
                    is ShopNDineFragment -> {
                        fragment.viewModel.favourite(poi, deviceId)
                            .observe(fragment.viewLifecycleOwner, {
                                when (it.status) {
                                    Result.Status.SUCCESS -> {
                                        fragment.viewModel.updatedDb(poi)
                                    }
                                    else -> {
                                    }
                                }
                            })
                    }
                    is FavouriteFragment -> {
                        fragment.viewModel.favourite(poi, deviceId)
                            .observe(fragment.viewLifecycleOwner, {
                                when (it.status) {
                                    Result.Status.SUCCESS -> {
                                        fragment.viewModel.updatedDb(poi)
                                        fragment.reload()
                                    }
                                    else -> {
                                    }
                                }
                            })
                    }
                }

            }
            else -> {
/*
                val direction = if (fragment is FavouriteFragment) {
                    FavouriteFragmentDirections.actionToPoiDetailFragment(poi, true)
                } else {
                    AttractionFragmentDirections.actionNavAttractionToPoiDetailFragment(poi)
                }
                val extras =
                    FragmentNavigatorExtras(itemBinding.textShopName to context.getString(R.string.transition_title))
                Navigation.findNavController(itemBinding.root).navigate(direction, extras)
*/
//                if (fragment is FlightDetailsFragment) {
//                    fragment.isLeavingPage = true
//                }
//                val bundle = Bundle()
//                bundle.putParcelable(Intent.EXTRA_TEXT, poi)
//                Navigation.findNavController(view).navigate(R.id.fragment_POIdDetails, bundle)
            }
        }
    }
}