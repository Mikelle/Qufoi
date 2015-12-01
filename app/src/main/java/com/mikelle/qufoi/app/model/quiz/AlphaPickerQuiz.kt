package com.mikelle.qufoi.app.model.quiz

import android.annotation.SuppressLint
import android.os.Parcel

@SuppressLint("ParcelCreator")
class AlphaPickerQuiz : Quiz<String> {

    constructor(question: String, answer: String, solved: Boolean) : super(question, answer, solved) {
    }

    @SuppressWarnings("unused")
    constructor(`in`: Parcel) : super(`in`) {
        answer = `in`.readString()
    }

    override val type: QuizType
        get() = QuizType.ALPHA_PICKER

    override val stringAnswer: String
        get() = answer!!

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(answer)
    }
}
