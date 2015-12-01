package com.mikelle.qufoi.app.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.ImageView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.helper.ApiLevelHelper
import com.mikelle.qufoi.app.widget.outlineprovider.RoundOutlineProvider

/**
 * A simple view that wraps an avatar.
 */
class AvatarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attrs, defStyle), Checkable {

    private var mChecked: Boolean = false

    override fun setChecked(b: Boolean) {
        mChecked = b
        invalidate()
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    @SuppressLint("NewApi")
    fun setAvatar(@DrawableRes resId: Int) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            clipToOutline = true
            setImageResource(resId)
        } else {
            setAvatarPreLollipop(resId)
        }
    }

    private fun setAvatarPreLollipop(@DrawableRes resId: Int) {
        val drawable = ResourcesCompat.getDrawable(resources, resId,
                context.theme)
        val bitmapDrawable = drawable as BitmapDrawable
        @SuppressWarnings("ConstantConditions")
        val roundedDrawable = RoundedBitmapDrawableFactory.create(resources,
                bitmapDrawable.bitmap)
        roundedDrawable.isCircular = true
        setImageDrawable(roundedDrawable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mChecked) {
            val border = ContextCompat.getDrawable(context, R.drawable.selector_avatar)
            border.setBounds(0, 0, width, height)
            border.draw(canvas)
        }
    }

    @SuppressLint("NewApi")
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            return
        }
        if (w > 0 && h > 0) {
            outlineProvider = RoundOutlineProvider(Math.min(w, h))
        }
    }
}
