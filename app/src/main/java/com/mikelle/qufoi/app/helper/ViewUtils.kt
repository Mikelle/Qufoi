package com.mikelle.qufoi.app.helper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewCompat
import android.transition.ChangeBounds
import android.util.Property
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

object ViewUtils {

    public val FOREGROUND_COLOR: Property<FrameLayout, Int> = object : IntProperty<FrameLayout>("foregroundColor") {

        override fun setValue(layout: FrameLayout, value: Int) {
            if (layout.foreground is ColorDrawable) {
                (layout.foreground.mutate() as ColorDrawable).color = value
            } else {
                layout.foreground = ColorDrawable(value)
            }
        }

        override fun get(layout: FrameLayout): Int {
            if (layout.foreground is ColorDrawable) {
                return (layout.foreground as ColorDrawable).color
            } else {
                return Color.TRANSPARENT
            }
        }
    }

    public val BACKGROUND_COLOR: Property<View, Int> = object : IntProperty<View>("backgroundColor") {

        override fun setValue(view: View, value: Int) {
            view.setBackgroundColor(value)
        }

        override fun get(view: View): Int {
            val d = view.background
            if (d is ColorDrawable) {
                return d.color
            }
            return Color.TRANSPARENT
        }
    }

    public val PROPERTY_TEXT_SIZE: Property<TextView, Float> = object : FloatProperty<TextView>("textSize") {
        override fun get(view: TextView): Float {
            return view.textSize
        }

        override fun setValue(view: TextView, textSize: Float) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
    }

    val PROPERTY_TEXT_PADDING_START: Property<TextView, Int> = object : IntProperty<TextView>("paddingStart") {
        override fun get(view: TextView): Int {
            return ViewCompat.getPaddingStart(view)
        }

        override fun setValue(view: TextView, paddingStart: Int) {
            ViewCompat.setPaddingRelative(view, paddingStart, view.paddingTop,
                    ViewCompat.getPaddingEnd(view), view.paddingBottom)
        }
    }

    abstract class IntProperty<T>(name: String) : Property<T, Int>(Int::class.java, name) {

        abstract fun setValue(`object`: T, value: Int)

        override fun set(`object`: T, value: Int?) {
            //noinspection UnnecessaryUnboxing
            setValue(`object`, value!!.toInt())
        }
    }

    abstract class FloatProperty<T>(name: String) : Property<T, Float>(Float::class.java, name) {

        abstract fun setValue(`object`: T, value: Float)

        override fun set(`object`: T, value: Float?) {
            //noinspection UnnecessaryUnboxing
            setValue(`object`, value!!.toFloat())
        }
    }

    public fun setPaddingStart(target: TextView, paddingStart: Int) {
        ViewCompat.setPaddingRelative(target, paddingStart, target.paddingTop,
                ViewCompat.getPaddingEnd(target), target.paddingBottom)
    }

}
