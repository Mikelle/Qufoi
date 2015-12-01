package com.mikelle.qufoi.app.widget

import com.mikelle.qufoi.app.helper.ViewUtils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v4.view.ViewCompat
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView

/**
 * A transition that resizes text of a TextView.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class TextResizeTransition(context: Context, attrs: AttributeSet) : Transition(context, attrs) {

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view !is TextView) {
            throw UnsupportedOperationException("Doesn't work on " + transitionValues.view.javaClass.name)
        }
        val view = transitionValues.view as TextView
        transitionValues.values.put(PROPERTY_NAME_TEXT_RESIZE, view.textSize)
        transitionValues.values.put(PROPERTY_NAME_PADDING_RESIZE,
                ViewCompat.getPaddingStart(view))
    }

    override fun getTransitionProperties(): Array<String> {
        return TRANSITION_PROPERTIES
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?,
                                endValues: TransitionValues?): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }

        val initialTextSize = startValues.values[PROPERTY_NAME_TEXT_RESIZE] as Float
        val targetTextSize = endValues.values[PROPERTY_NAME_TEXT_RESIZE] as Float
        val targetView = endValues.view as TextView
        targetView.setTextSize(TypedValue.COMPLEX_UNIT_PX, initialTextSize)

        val initialPaddingStart = startValues.values[PROPERTY_NAME_PADDING_RESIZE] as Int
        val targetPaddingStart = endValues.values[PROPERTY_NAME_PADDING_RESIZE] as Int

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(targetView,
                        ViewUtils.PROPERTY_TEXT_SIZE,
                        initialTextSize,
                        targetTextSize),
                ObjectAnimator.ofInt(targetView,
                        ViewUtils.PROPERTY_TEXT_PADDING_START,
                        initialPaddingStart,
                        targetPaddingStart))
        return animatorSet
    }

    companion object {

        private val PROPERTY_NAME_TEXT_RESIZE = "com.mikelle.qufoi.app.widget:TextResizeTransition:textSize"
        private val PROPERTY_NAME_PADDING_RESIZE = "com.mikelle.qufoi.app.widget:TextResizeTransition:paddingStart"

        private val TRANSITION_PROPERTIES = arrayOf(PROPERTY_NAME_TEXT_RESIZE, PROPERTY_NAME_PADDING_RESIZE)
    }
}
