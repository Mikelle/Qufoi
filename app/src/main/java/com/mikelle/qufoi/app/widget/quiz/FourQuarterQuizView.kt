package com.mikelle.qufoi.app.widget.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.adapter.OptionsQuizAdapter
import com.mikelle.qufoi.app.helper.ApiLevelHelper
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.quiz.FourQuarterQuiz

@SuppressLint("ViewConstructor")
class FourQuarterQuizView(context: Context, category: Category, quiz: FourQuarterQuiz) : AbsQuizView<FourQuarterQuiz>(context, category, quiz) {
    private var mAnswered = -1
    private var mAnswerView: GridView? = null

    override fun createQuizContentView(): View {
        mAnswerView = GridView(context)
        mAnswerView!!.setSelector(R.drawable.selector_button)
        mAnswerView!!.numColumns = 2
        mAnswerView!!.adapter = OptionsQuizAdapter(quiz.options!!,
                R.layout.item_answer)
        return mAnswerView!!
    }

    override fun getUserInput(): Bundle {
        val bundle = Bundle()
        bundle.putInt(KEY_ANSWER, mAnswered)
        return bundle
    }

    @SuppressLint("NewApi")
    override fun setUserInput(savedInput: Bundle?) {
        if (savedInput == null) {
            return
        }
        mAnswered = savedInput.getInt(KEY_ANSWER)
        if (mAnswered != -1) {
            if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.KITKAT) && isLaidOut) {
                setUpUserInput()
            }

        }
    }

    private fun setUpUserInput() {
        mAnswerView!!.performItemClick(mAnswerView!!.getChildAt(mAnswered), mAnswered,
                mAnswerView!!.adapter.getItemId(mAnswered))
        mAnswerView!!.getChildAt(mAnswered).isSelected = true
        mAnswerView!!.setSelection(mAnswered)
    }

    override fun isAnswerCorrect(): Boolean {
        return quiz.isAnswerCorrect(intArrayOf(mAnswered))
    }

    companion object {

        private val KEY_ANSWER = "ANSWER"
    }
}
