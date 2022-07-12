package com.example.buglibrary.node

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.buglibrary.GlideApp
import com.example.buglibrary.R
import com.google.ar.core.Anchor
import com.example.buglibrary.data.Poi

class PoiNode(
    private val fragment: Fragment,
    private val poi: Poi,
    private val totalDistance: Float,
    private val locAnchor: Anchor?
) /*: AnchorNode(locAnchor)*/


/*{
    override fun onActivate() {
        super.onActivate()
        if (scene == null) {
            return
        }
        val context = fragment.requireContext()
        ViewRenderable.builder()
            .setView(context, R.layout.poi_node)
            .build()
            .thenAccept { render ->

                val poiImg = render.view.findViewById<ImageView>(R.id.img_poi)
                poi.picUrl?.let {
                    if (it.isNotEmpty()) {

                        GlideApp.with(context).load(poi.picUrl[0].imgurl)
                            .placeholder(R.drawable.img_placeholder)
                            .error(R.drawable.img_placeholder)
                            .into(poiImg)
                    }
                }


*/
/* Glide.with(context).load(poi.logoUrl).placeholder(R.mipmap.ic_launcher)
                     .into(poiImg)*//*
*/
/*

                val textPoiName = render.view.findViewById<TextView>(R.id.text_poi_name)
                val textTotDist = render.view.findViewById<TextView>(R.id.text_distance)
                textPoiName.text = poi.poiMultilingual?.en?.name
                textTotDist.text = "$totalDistance m"

                val buttonDetail = render.view.findViewById<Button>(R.id.button_show_detail)
                buttonDetail.setOnClickListener {
                    Toast.makeText(context, "Detail button clicked", Toast.LENGTH_SHORT).show()
//                    val direction = NavigationFragmentDirections.actionPoiDetailFragment(poi)
//                    fragment.findNavController().navigate(direction)
                }
                val buttonNavigate = render.view.findViewById<Button>(R.id.button_navigate)
                buttonNavigate.setOnClickListener {
                    Toast.makeText(context, "Navigate button clicked", Toast.LENGTH_SHORT).show()
//                    val direction = NavigationFragmentDirections.actionPoiDetailFragment(poi)
//                    fragment.findNavController().navigate(direction)
                }
                renderable = render

            }
    }
*//*

}*/
