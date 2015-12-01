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
import com.mikelle.qufoi.app.model.quiz.PickerQuiz

@SuppressLint("ViewConstructor")
class PickerQuizView(context: Context, category: Category, quiz: PickerQuiz) : AbsQuizView<PickerQuiz>(context, category, quiz) {

    private var mCurrentSelection: TextView? = null
    private var mSeekBar: SeekBar? = null
    private var mStep: Int = 0
    private var mMin: Int = 0
    private var mProgress: Int = 0

    override fun createQuizContentView(): View {
        initStep()
        mMin = quiz.min
        val layout = layoutInflater.inflate(
                R.layout.quiz_layout_picker, this, false) as ScrollView
        mCurrentSelection = layout.findViewById(R.id.seekbar_progress) as TextView
        mCurrentSelection?.text = mMin.toString()
        mSeekBar = layout.findViewById(R.id.seekbar) as SeekBar
        mSeekBar?.max = seekBarMax
        mSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                setCurrentSelectionText(mMin + progress)
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

    private fun setCurrentSelectionText(progress: Int) {
        mProgress = progress / mStep * mStep
        mCurrentSelection?.text = mProgress.toString()
    }

    override fun isAnswerCorrect(): Boolean {
        return quiz.isAnswerCorrect(mProgress)
    }

    private fun initStep() {
        val tmpStep = quiz.step
        //make sure steps are never 0
        if (0 == tmpStep) {
            mStep = 1
        } else {
            mStep = tmpStep
        }
    }

    override fun getUserInput(): Bundle {
        val bundle = Bundle()
        bundle.putInt(KEY_ANSWER, mProgress)
        return bundle
    }

    override fun setUserInput(savedInput: Bundle?) {
        if (null == savedInput) {
            return
        }
        mSeekBar?.progress = savedInput.getInt(KEY_ANSWER) - mMin
    }

    /**
     * Calculates the actual max value of the SeekBar
     */
    private val seekBarMax: Int
        get() {
            val absMin = Math.abs(quiz.min)
            val absMax = Math.abs(quiz.max)
            val realMin = Math.min(absMin, absMax)
            val realMax = Math.max(absMin, absMax)
            return realMax - realMin
        }

    companion object {

        private val KEY_ANSWER = "ANSWER"
    }
}
