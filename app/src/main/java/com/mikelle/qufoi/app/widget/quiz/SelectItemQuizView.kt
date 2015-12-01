package com.mikelle.qufoi.app.widget.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.ListView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.adapter.OptionsQuizAdapter
import com.mikelle.qufoi.app.helper.AnswerHelper
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.quiz.SelectItemQuiz

@SuppressLint("ViewConstructor")
class SelectItemQuizView(context: Context, category: Category, quiz: SelectItemQuiz) : AbsQuizView<SelectItemQuiz>(context, category, quiz) {

    private var mAnswers: BooleanArray? = null
    private var mListView: ListView? = null

    init {
        mAnswers = answers
    }

    override fun createQuizContentView(): View {
        val context = context
        mListView = ListView(context)
        mListView?.divider = null
        mListView?.setSelector(R.drawable.selector_button)
        mListView?.adapter = OptionsQuizAdapter(quiz.options!!, R.layout.item_answer_start,
                context, true)
        mListView?.choiceMode = AbsListView.CHOICE_MODE_SINGLE

        return mListView!!
    }

    override fun isAnswerCorrect(): Boolean {
        val checkedItemPositions = mListView!!.checkedItemPositions
        val answer = quiz.answer
        return AnswerHelper.isAnswerCorrect(checkedItemPositions, answer)
    }

    override fun getUserInput(): Bundle {
        val bundle = Bundle()
        bundle.putBooleanArray(KEY_ANSWERS, mAnswers)
        return bundle
    }

    override fun setUserInput(savedInput: Bundle?) {
        if (savedInput == null) {
            return
        }
        mAnswers = savedInput.getBooleanArray(KEY_ANSWERS)
        if (mAnswers == null) {
            return
        }
        val adapter = mListView!!.adapter
        for (i in mAnswers!!.indices) {
            mListView!!.performItemClick(mListView!!.getChildAt(i), i, adapter.getItemId(i))
        }
    }

    private fun toggleAnswerFor(answerId: Int) {
        answers[answerId] = !mAnswers!![answerId]
    }

    private val answers: BooleanArray
        get() {
            if (null == mAnswers) {
                mAnswers = BooleanArray(quiz.options!!.size())
            }
            return mAnswers!!
        }

    companion object {

        private val KEY_ANSWERS = "ANSWERS"
    }
}
