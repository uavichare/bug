package com.example.buglibrary.node

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ar.core.Anchor
/*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
*/


class SignalNode(
    private val context: Context,
    private val arrow: Int,
    private val locAnchor: Anchor?
) /*: AnchorNode(locAnchor)*/ {

/*
    override fun onActivate() {
        super.onActivate()
        if (scene == null) {
            return
        }

        if (arrow == 0) {
            return
        }
        */
/* MaterialFactory.makeOpaqueWithColor(
             context,
             Color(android.graphics.Color.YELLOW)
         ).thenAccept {
             ShapeFactory.makeSphere(3f, Vector3.zero(), it)
         }*//*


        var glbArrowAssets = "file:///android_asset/left_bounding_box.glb"
        Log.d("TAG", "onActivate: $arrow")
        if (arrow == 2) {
            glbArrowAssets = "file:///android_asset/right_bounding_box.glb"
        }
        ModelRenderable.builder()
            .setSource(
                context,
                Uri.parse(
                    glbArrowAssets
                )
            )
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->

                renderable = modelRenderable



            }
            .exceptionally { throwable: Throwable? ->
                Log.d("TAG", "Unable to load Renderable.${throwable!!.message}")
                null
            }

        */
/*  ViewRenderable.builder()
              .setView(context, R.layout.node_signal)

              .build()
              .thenAccept { render ->
                  val img = render.view.findViewById<ImageView>(R.id.img_signal)
                  when (arrow) {
                      1 -> {
                          img.setImageResource(R.drawable.poi_route_left)
                      }
                      2 -> {
                          img.setImageResource(R.drawable.poi_route_right)
                      }
                  }

                  renderable = render

              }*//*

    }
*/
}