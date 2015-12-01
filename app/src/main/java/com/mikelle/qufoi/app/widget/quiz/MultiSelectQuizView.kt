package com.mikelle.qufoi.app.widget.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView

import com.mikelle.qufoi.app.adapter.OptionsQuizAdapter
import com.mikelle.qufoi.app.helper.AnswerHelper
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.quiz.MultiSelectQuiz

@SuppressLint("ViewConstructor")
class MultiSelectQuizView(context: Context, category: Category, quiz: MultiSelectQuiz) : AbsQuizView<MultiSelectQuiz>(context, category, quiz) {

    private var mListView: ListView? = null

    override fun createQuizContentView(): View {
        mListView = ListView(context)
        mListView?.adapter = OptionsQuizAdapter(quiz.options!!,
                android.R.layout.simple_list_item_multiple_choice)
        mListView?.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
        mListView?.itemsCanFocus = false
        return mListView!!
    }

    override fun isAnswerCorrect(): Boolean {
        val checkedItemPositions = mListView!!.checkedItemPositions
        val answer = quiz.answer
        return AnswerHelper.isAnswerCorrect(checkedItemPositions, answer)
    }

    override fun getUserInput(): Bundle {
        val bundle = Bundle()
        val bundleableAnswer = bundleableAnswer
        bundle.putBooleanArray(KEY_ANSWER, bundleableAnswer)
        return bundle
    }

    override fun setUserInput(savedInput: Bundle?) {
        if (savedInput == null) {
            return
        }
        val answers = savedInput.getBooleanArray(KEY_ANSWER) ?: return
        for (i in answers.indices) {
            mListView?.setItemChecked(i, answers[i])
        }
    }

    private val bundleableAnswer: BooleanArray?
        get() {
            val checkedItemPositions = mListView!!.checkedItemPositions
            val answerSize = checkedItemPositions.size()
            if (0 == answerSize) {
                return null
            }
            val optionsSize = quiz.options!!.size()
            val bundleableAnswer = BooleanArray(optionsSize)
            var key: Int
            for (i in 0..answerSize - 1) {
                key = checkedItemPositions.keyAt(i)
                bundleableAnswer[key] = checkedItemPositions.valueAt(i)
            }
            return bundleableAnswer
        }

    companion object {

        private val KEY_ANSWER = "ANSWER"
    }
}
