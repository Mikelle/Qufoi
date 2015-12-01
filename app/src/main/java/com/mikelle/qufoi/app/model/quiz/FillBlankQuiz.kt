package com.mikelle.qufoi.app.model.quiz

import android.annotation.SuppressLint
import android.os.Parcel

@SuppressLint("ParcelCreator")
class FillBlankQuiz : Quiz<String> {

    val start: String
    val end: String

    constructor(question: String, answer: String, start: String, end: String, solved: Boolean) : super(question, answer, solved) {
        this.start = start
        this.end = end
    }

    @SuppressWarnings("unused")
    constructor(`in`: Parcel) : super(`in`) {
        answer = `in`.readString()
        start = `in`.readString()
        end = `in`.readString()
    }

    override val stringAnswer: String
        get() = answer!!

    override val type: QuizType
        get() = QuizType.FILL_BLANK

    override fun isAnswerCorrect(answer: String): Boolean {
        return answer.equals(answer, ignoreCase = true)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(answer)
        dest.writeString(start)
        dest.writeString(end)
    }
}
