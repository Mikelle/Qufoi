package com.mikelle.qufoi.app.widget.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.quiz.AlphaPickerQuiz

import java.util.Arrays

@SuppressLint("ViewConstructor")
class AlphaPickerQuizView(context: Context, category: Category, quiz: AlphaPickerQuiz) : AbsQuizView<AlphaPickerQuiz>(context, category, quiz) {

    private var mCurrentSelection: TextView? = null
    private var mSeekBar: SeekBar? = null
    private var mAlphabet: List<String>? = null

    override fun createQuizContentView(): View {
        val layout = layoutInflater.inflate(
                R.layout.quiz_layout_picker, this, false) as ScrollView
        mCurrentSelection = layout.findViewById(R.id.seekbar_progress) as TextView
        mCurrentSelection!!.text = alphabet[0]
        mSeekBar = layout.findViewById(R.id.seekbar) as SeekBar
        mSeekBar?.max = alphabet.size - 1
        mSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mCurrentSelection!!.text = alphabet[progress]
                allowAnswer()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                /* no-op */
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                /* no-op */
            }
        })
        return layout
    }

    override fun isAnswerCorrect(): Boolean {
        return quiz.isAnswerCorrect(mCurrentSelection?.text.toString())
    }

    override fun getUserInput(): Bundle {
        val bundle = Bundle()
        bundle.putString(KEY_SELECTION, mCurrentSelection!!.text.toString())
        return bundle
    }

    override fun setUserInput(savedInput: Bundle?) {
        if (savedInput == null) {
            return
        }
        val userInput = savedInput.getString(KEY_SELECTION, alphabet[0])
        mSeekBar?.progress = alphabet.indexOf(userInput)
    }


    private val alphabet: List<String>
        get() {
            if (null == mAlphabet) {
                mAlphabet = Arrays.asList(*resources.getStringArray(R.array.alphabet))
            }
            return mAlphabet!!
        }

    companion object {

        private val KEY_SELECTION = "SELECTION"
    }
}
