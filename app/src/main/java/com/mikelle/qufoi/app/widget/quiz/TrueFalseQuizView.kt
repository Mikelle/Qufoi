package com.mikelle.qufoi.app.widget.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.quiz.TrueFalseQuiz

@SuppressLint("ViewConstructor")
class TrueFalseQuizView(context: Context, category: Category, quiz: TrueFalseQuiz) : AbsQuizView<TrueFalseQuiz>(context, category, quiz) {

    private var mAnswer: Boolean = false
    private var mAnswerTrue: View? = null
    private var mAnswerFalse: View? = null

    override fun createQuizContentView(): View {
        val container = layoutInflater.inflate(
                R.layout.quiz_radio_group_true_false, this, false) as ViewGroup

        val clickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.answer_true -> mAnswer = true
                R.id.answer_false -> mAnswer = false
            }
            allowAnswer()
        }

        mAnswerTrue = container.findViewById(R.id.answer_true)
        mAnswerTrue?.setOnClickListener(clickListener)
        mAnswerFalse = container.findViewById(R.id.answer_false)
        mAnswerFalse?.setOnClickListener(clickListener)
        return container
    }

    override fun isAnswerCorrect(): Boolean {
        return quiz.isAnswerCorrect(mAnswer)
    }

    override fun getUserInput(): Bundle {
        val bundle = Bundle()
        bundle.putBoolean(KEY_SELECTION, mAnswer)
        return bundle
    }

    override fun setUserInput(savedInput: Bundle?) {
        if (savedInput == null) {
            return
        }
        val tmpAnswer = savedInput.getBoolean(KEY_SELECTION)
        performSelection(if (tmpAnswer) mAnswerTrue!! else mAnswerFalse!!)
    }

    private fun performSelection(selection: View) {
        selection.performClick()
        selection.isSelected = true
    }

    companion object {

        private val KEY_SELECTION = "SELECTION"
        private val LAYOUT_PARAMS = LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1f)

        init {
            LAYOUT_PARAMS.gravity = Gravity.CENTER
        }
    }
}
