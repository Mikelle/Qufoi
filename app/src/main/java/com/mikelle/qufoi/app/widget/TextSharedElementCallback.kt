package com.mikelle.qufoi.app.widget

import com.mikelle.qufoi.app.helper.ViewUtils

import android.annotation.TargetApi
import android.os.Build
import android.support.v4.app.SharedElementCallback
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView

/**
 * This callback allows a shared TextView to resize text and start padding during transition.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
open class TextSharedElementCallback(private val mInitialTextSize: Float, private val mInitialPaddingStart: Int) : SharedElementCallback() {
    private var mTargetViewTextSize: Float = 0.toFloat()
    private var mTargetViewPaddingStart: Int = 0

    override fun onSharedElementStart(sharedElementNames: List<String>?, sharedElements: List<View>?,
                                      sharedElementSnapshots: List<View>?) {
        val targetView = getTextView(sharedElements!!)
        if (targetView == null) {
            Log.w(TAG, "onSharedElementStart: No shared TextView, skipping.")
            return
        }
        mTargetViewTextSize = targetView.textSize
        mTargetViewPaddingStart = targetView.paddingStart
        // Setup the TextView's start values.
        targetView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mInitialTextSize)
        ViewUtils.setPaddingStart(targetView, mInitialPaddingStart)
    }

    override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?,
                                    sharedElementSnapshots: List<View>?) {
        val initialView = getTextView(sharedElements!!)

        if (initialView == null) {
            Log.w(TAG, "onSharedElementEnd: No shared TextView, skipping")
            return
        }

        // Setup the TextView's end values.
        initialView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTargetViewTextSize)
        ViewUtils.setPaddingStart(initialView, mTargetViewPaddingStart)

        // Re-measure the TextView (since the text size has changed).
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        initialView.measure(widthSpec, heightSpec)
        initialView.requestLayout()
    }

    private fun getTextView(sharedElements: List<View>): TextView? {
        var targetView: TextView? = null
        for (i in sharedElements.indices) {
            if (sharedElements[i] is TextView) {
                targetView = sharedElements[i] as TextView
            }
        }
        return targetView
    }

    companion object {
        private val TAG = "TextResize"
    }

}
