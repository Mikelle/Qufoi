package com.mikelle.qufoi.app.model.quiz

import android.os.Parcel
import android.os.Parcelable

import com.mikelle.qufoi.app.helper.AnswerHelper

import java.util.Arrays

class FourQuarterQuiz : OptionsQuiz<String> {

    constructor(question: String, answer: IntArray, options: Array<String>, solved: Boolean) : super(question, answer, options, solved) {
    }

    constructor(`in`: Parcel) : super(`in`) {
        var options = `in`.createStringArray()
        options = options
    }

    override val type: QuizType
        get() = QuizType.FOUR_QUARTER

    override val stringAnswer: String
        get() = AnswerHelper.getAnswer(answer, options!!)!!

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        val options = options
        dest.writeStringArray(options)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is FourQuarterQuiz) {
            return false
        }

        val answer = answer
        val question = question
        if (if (answer != null) !Arrays.equals(answer, o.answer) else o.answer != null) {
            return false
        }
        if (question != o.question) {
            return false
        }

        //noinspection RedundantIfStatement
        if (!Arrays.equals(options, o.options)) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + Arrays.hashCode(options)
        result = 31 * result + Arrays.hashCode(answer)
        return result
    }

    companion object {

        val CREATOR: Parcelable.Creator<FourQuarterQuiz> = object : Parcelable.Creator<FourQuarterQuiz> {
            override fun createFromParcel(`in`: Parcel): FourQuarterQuiz {
                return FourQuarterQuiz(`in`)
            }

            override fun newArray(size: Int): Array<FourQuarterQuiz?> {
                return arrayOfNulls(size)
            }
        }
    }

}
