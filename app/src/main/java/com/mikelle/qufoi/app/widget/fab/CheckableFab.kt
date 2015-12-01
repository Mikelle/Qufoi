package com.mikelle.qufoi.app.widget.fab

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable

import com.mikelle.qufoi.app.R

/**
 * A [FloatingActionButton] that implements [Checkable] to allow display of different
 * icons in it's states.
 */
class CheckableFab @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FloatingActionButton(context, attrs, defStyle), Checkable {

    private var mIsChecked = true

    init {
        setImageResource(R.drawable.answer_quiz_fab)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        var extraSpace = extraSpace
        val drawableState = super.onCreateDrawableState(++extraSpace)
        if (mIsChecked) {
            View.mergeDrawableStates(drawableState, CHECKED)
        }
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (mIsChecked == checked) {
            return
        }
        mIsChecked = checked
        refreshDrawableState()
    }

    override fun isChecked(): Boolean {
        return mIsChecked
    }

    override fun toggle() {
        isChecked = !mIsChecked
    }

    companion object {

        private val CHECKED = intArrayOf(android.R.attr.state_checked)
    }
}
