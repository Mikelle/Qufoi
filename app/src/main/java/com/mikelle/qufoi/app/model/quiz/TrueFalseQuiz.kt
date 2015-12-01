package com.mikelle.qufoi.app.model.quiz

import android.annotation.SuppressLint
import android.os.Parcel

import com.mikelle.qufoi.app.helper.ParcelableHelper

@SuppressLint("ParcelCreator")
class TrueFalseQuiz : Quiz<Boolean> {

    constructor(question: String, answer: Int?, solved: Boolean) : super(question, answer!!.toString(), solved) {
    }

    @SuppressWarnings("unused")
    constructor(`in`: Parcel) : super(`in`) {
        answer = ParcelableHelper.readBoolean(`in`)
    }

    override val stringAnswer: String
        get() = answer!!.toString()

    override val type: QuizType
        get() = QuizType.TRUE_FALSE

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        ParcelableHelper.writeBoolean(dest, answer!!)
    }
}
