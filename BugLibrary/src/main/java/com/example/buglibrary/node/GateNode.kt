package com.example.buglibrary.node

import android.content.Context
import com.google.ar.core.Anchor


class GateNode(
    private val context: Context,

    private val locAnchor: Anchor?
) /*: AnchorNode(locAnchor)*/
{

/*
    override fun onActivate() {
        super.onActivate()
        if (scene == null) {
            return
        }

*/
/*
        val fltfAssets = "file:///android_asset/gate_wo_base.glb"
        ModelRenderable.builder() // To load as an asset from the 'assets' folder ('src/main/assets/andy.sfb'):
            .setSource(
                context, RenderableSource.builder().setSource(
                    context,
                    Uri.parse(fltfAssets),
                    RenderableSource.SourceType.GLB
                )
                    .setScale(0.4f)  // Scale the original model to 50%.
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
//                Uri.parse(fltfAssets)

//                Uri.parse("file:///android_asset/gate.glb")
            ) // Instead, load as a resource from the 'res/raw' folder ('src/main/res/raw/andy.sfb'):

            .build()
            .thenAccept { render: ModelRenderable ->
                Log.d("TAG", "Unable to loadonActivate: ")
                renderable = render
            }
            .exceptionally { throwable: Throwable? ->
                Log.d("TAG", "Unable to load Renderable.${throwable!!.message}")
                null
            }*//*



    }
*/
}