package com.mikelle.qufoi.app.widget

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class OffsetDecoration(private val mOffset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State?) {
        outRect.left = mOffset
        outRect.right = mOffset
        outRect.bottom = mOffset
        outRect.top = mOffset
    }
}
