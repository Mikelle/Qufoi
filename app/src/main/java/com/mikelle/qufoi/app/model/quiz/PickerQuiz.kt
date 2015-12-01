package com.mikelle.qufoi.app.model.quiz

import android.annotation.SuppressLint
import android.os.Parcel

@SuppressLint("ParcelCreator")
class PickerQuiz : Quiz<Int> {

    val min: Int
    val max: Int
    val step: Int

    constructor(question: String, answer: Int?, min: Int, max: Int, step: Int, solved: Boolean) : super(question, answer.toString(), solved) {
        this.min = min
        this.max = max
        this.step = step
    }

    constructor(`in`: Parcel) : super(`in`) {
        answer = `in`.readInt()
        min = `in`.readInt()
        max = `in`.readInt()
        step = `in`.readInt()
    }

    override val type: QuizType
        get() = QuizType.PICKER

    override val stringAnswer: String
        get() = answer!!.toString()

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(answer!!)
        dest.writeInt(min)
        dest.writeInt(max)
        dest.writeInt(step)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is PickerQuiz) {
            return false
        }
        //noinspection EqualsBetweenInconvertibleTypes
        if (!super.equals(o)) {
            return false
        }

        if (min != o.min) {
            return false
        }
        //noinspection SimplifiableIfStatement
        if (max != o.max) {
            return false
        }
        return step == o.step

    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + min
        result = 31 * result + max
        result = 31 * result + step
        return result
    }
}
