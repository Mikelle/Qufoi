package com.mikelle.qufoi.app.model.quiz

import android.annotation.SuppressLint
import android.os.Parcel

import com.mikelle.qufoi.app.helper.AnswerHelper

import java.util.Arrays

@SuppressLint("ParcelCreator")
class ToggleTranslateQuiz : OptionsQuiz<Array<String>> {

    private var mReadableOptions: Array<String>? = null

    constructor(question: String, answer: IntArray, options: Array<Array<String>>, solved: Boolean) : super(question, answer, options, solved) {
    }

    @SuppressWarnings("unused")
    constructor(`in`: Parcel) : super(`in`) {
        answer = `in`.createIntArray()
        options = `in`.readSerializable() as Array<Array<String>>
    }

    override val type: QuizType
        get() = QuizType.TOGGLE_TRANSLATE

    override val stringAnswer: String
        get() = AnswerHelper.getAnswer(answer, readableOptions)!!

    //lazily initialize
    //iterate over the options and create readable pairs
    val readableOptions: Array<String>
        get() {
            if (null == mReadableOptions) {
                val options = options
            }
            return mReadableOptions as Array<String>
        }

    private fun createReadablePair(option: Array<String>): String {
        // results in "Part one <> Part two"
        return option[0] + " <> " + option[1]
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeIntArray(answer)
        dest.writeSerializable(options)
    }

    @SuppressWarnings("RedundantIfStatement")
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is ToggleTranslateQuiz) {
            return false
        }

        if (!Arrays.equals(answer, o.answer)) {
            return false
        }

        if (!Arrays.deepEquals(options, o.options)) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + Arrays.hashCode(options)
        return result
    }
}
