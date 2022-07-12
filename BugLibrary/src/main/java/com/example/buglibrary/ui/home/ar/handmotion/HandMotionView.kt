package com.example.buglibrary.ui.home.ar.handmotion

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.example.buglibrary.ui.home.ar.handmotion.HandMotionAnimation

class HandMotionView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        clearAnimation()
        startAnimation(HandMotionAnimation(this))
    }
}
