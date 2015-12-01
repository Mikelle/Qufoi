package com.mikelle.qufoi.app.widget.outlineprovider

import android.annotation.TargetApi
import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider

/**
 * Creates round outlines for views.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class RoundOutlineProvider(private val mSize: Int) : ViewOutlineProvider() {

    init {
        if (0 > mSize) {
            throw IllegalArgumentException("size needs to be > 0. Actually was " + mSize)
        }
    }

    override fun getOutline(view: View, outline: Outline) {
        outline.setOval(0, 0, mSize, mSize)
    }

}
