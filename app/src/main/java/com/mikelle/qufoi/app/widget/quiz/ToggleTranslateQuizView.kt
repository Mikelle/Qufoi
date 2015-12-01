package com.mikelle.qufoi.app.widget.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.ListAdapter
import android.widget.ListView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.adapter.OptionsQuizAdapter
import com.mikelle.qufoi.app.helper.AnswerHelper
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.quiz.ToggleTranslateQuiz

@SuppressLint("ViewConstructor")
class ToggleTranslateQuizView(context: Context, category: Category, quiz: ToggleTranslateQuiz) : AbsQuizView<ToggleTranslateQuiz>(context, category, quiz) {

    private var mAnswers: BooleanArray? = null
    private var mListView: ListView? = null

    init {
        initAnswerSpace()
    }

    private fun initAnswerSpace() {
        mAnswers = BooleanArray(quiz.options!!.size())
    }

    override fun createQuizContentView(): View {
        mListView = ListView(context)
        mListView?.divider = null
        mListView?.setSelector(R.drawable.selector_button)
        mListView?.adapter = OptionsQuizAdapter(quiz.readableOptions,
                R.layout.item_answer)
        mListView?.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE

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
            initAnswerSpace()
            return
        }
        val adapter = mListView!!.adapter
        for (i in mAnswers!!.indices) {
            mListView!!.performItemClick(mListView!!.getChildAt(i), i, adapter.getItemId(i))
        }
    }

    companion object {

        private val KEY_ANSWERS = "ANSWERS"
    }
}
