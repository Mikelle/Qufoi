package com.mikelle.qufoi.app.model.quiz

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import com.mikelle.qufoi.app.helper.ParcelableHelper

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * This abstract class provides general structure for quizzes.

 * @see com.mikelle.qufoi.app.model.quiz.QuizType

 * @see com.mikelle.qufoi.app.widget.quiz.AbsQuizView
 */
abstract class Quiz<A> : Parcelable {

    val question: String
    private val mQuizType: String
    var answer: A? = null
        protected set(answer) {
            this.answer = answer
        }
    /**
     * Flag indicating whether this quiz has already been solved.
     * It does not give information whether the solution was correct or not.
     */
    var isSolved: Boolean = false

    protected constructor(question: String, answer: String, solved: Boolean) {
        this.question = question
        this.answer = answer as A?
        mQuizType = type.jsonName
        isSolved = solved
    }

    protected constructor(`in`: Parcel) {
        question = `in`.readString()
        mQuizType = type.jsonName
        isSolved = ParcelableHelper.readBoolean(`in`)
    }

    abstract val type: QuizType

    /**
     * Implementations need to return a human readable version of the given answer.
     */
    abstract val stringAnswer: String

    open fun isAnswerCorrect(answer: A): Boolean {
        return this.answer == answer
    }

    val id: Int
        get() = question.hashCode()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(question)
        ParcelableHelper.writeBoolean(dest, isSolved)
    }

    @SuppressWarnings("RedundantIfStatement")
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Quiz<*>) {
            return false
        }

        if (isSolved != o.isSolved) {
            return false
        }
        if (answer != o.answer) {
            return false
        }
        if (question != o.question) {
            return false
        }
        if (mQuizType != o.mQuizType) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + answer!!.hashCode()
        result = 31 * result + mQuizType.hashCode()
        result = 31 * result + (if (isSolved) 1 else 0)
        return result
    }

    override fun toString(): String {
        return type.toString() + ": \"" + question + "\""
    }

    companion object {

        private val TAG = "Quiz"
        val CREATOR: Parcelable.Creator<Quiz<Any>> = object : Parcelable.Creator<Quiz<Any>> {
            @SuppressWarnings("TryWithIdenticalCatches")
            override fun createFromParcel(`in`: Parcel): Quiz<Any>? {
                val ordinal = `in`.readInt()
                val type = QuizType.values()[ordinal]
                try {
                    val constructor = type.type.getConstructor(Parcel::class.java)
                    return constructor.newInstance(`in`) as Quiz<Any>?
                } catch (e: InstantiationException) {
                    performLegacyCatch(e)
                } catch (e: IllegalAccessException) {
                    performLegacyCatch(e)
                } catch (e: InvocationTargetException) {
                    performLegacyCatch(e)
                } catch (e: NoSuchMethodException) {
                    performLegacyCatch(e)
                }

                throw UnsupportedOperationException("Could not create Quiz")
            }

            override fun newArray(size: Int): Array<Quiz<Any>?> {
                return arrayOfNulls(size)
            }
        }

        private fun performLegacyCatch(e: Exception) {
            Log.e(TAG, "createFromParcel ", e)
        }
    }
}
