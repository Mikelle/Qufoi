package com.mikelle.qufoi.app.model.quiz

import android.os.Parcel

import java.util.Arrays

/**
 * Base class holding details for quizzes with several potential answers.
 */
abstract class OptionsQuiz<T> : Quiz<IntArray> {

    var options: Array<T>? = null
        protected set(options) {
            this.options = options
        }

    constructor(question: String, answer: IntArray, options: Array<T>, solved: Boolean) : super(question, answer.toString(), solved) {
        this.options = options
    }

    constructor(`in`: Parcel) : super(`in`) {
        var answer = `in`.createIntArray()
        answer = answer
    }

    override fun isAnswerCorrect(answer: IntArray): Boolean {
        return Arrays.equals(answer, answer)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeIntArray(answer)
    }

    @SuppressWarnings("RedundantIfStatement")
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is OptionsQuiz<*>) {
            return false
        }

        if (!Arrays.equals(answer, (o.answer as IntArray))) {
            return false
        }
        if (!Arrays.equals(options, o.options)) {
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
