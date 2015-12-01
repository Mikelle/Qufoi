package com.mikelle.qufoi.app.model.quiz

import android.annotation.SuppressLint
import android.os.Parcel

import com.mikelle.qufoi.app.helper.AnswerHelper

@SuppressLint("ParcelCreator")
class MultiSelectQuiz : OptionsQuiz<String> {

    constructor(question: String, answer: IntArray, options: Array<String>, solved: Boolean) : super(question, answer, options, solved) {
    }

    @SuppressWarnings("unused")
    constructor(`in`: Parcel) : super(`in`) {
        var options = `in`.createStringArray()
        options = options
    }

    override val type: QuizType
        get() = QuizType.MULTI_SELECT

    override val stringAnswer: String
        get() = AnswerHelper.getAnswer(answer, options!!)!!

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeStringArray(options)
    }
}
