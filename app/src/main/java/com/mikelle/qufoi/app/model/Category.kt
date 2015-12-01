package com.mikelle.qufoi.app.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import com.mikelle.qufoi.app.helper.ParcelableHelper
import com.mikelle.qufoi.app.model.quiz.Quiz

import java.util.ArrayList
import java.util.Arrays

class Category : Parcelable {
    val name: String
    val id: String
    val theme: Theme
    val scores: IntArray
    var quizzes: List<Quiz<Any>>? = null
        private set
    var isSolved: Boolean = false

    constructor(name: String, id: String, theme: Theme, quizzes: List<Quiz<Any>>, solved: Boolean) {
        this.name = name
        this.id = id
        this.theme = theme
        this.quizzes = quizzes
        scores = IntArray(quizzes.size)
        isSolved = solved
    }

    constructor(name: String, id: String, theme: Theme, quizzes: List<Quiz<Any>>,
                scores: IntArray, solved: Boolean) {
        this.name = name
        this.id = id
        this.theme = theme
        if (quizzes.size == scores.size()) {
            this.quizzes = quizzes
            this.scores = scores
        } else {
            throw IllegalArgumentException("Quizzes and scores must have the same length")
        }
        isSolved = solved
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        id = `in`.readString()
        theme = Theme.values()[`in`.readInt()]
        `in`.readTypedList(quizzes, Quiz.CREATOR)
        scores = `in`.createIntArray()
        isSolved = ParcelableHelper.readBoolean(`in`)
    }

    /**
     * Updates a score for a provided quiz within this category.
     */
    public fun setScore(which: Quiz<Any>, correctlySolved: Boolean) {
        val index = quizzes!!.indexOf(which)
        Log.d(TAG, "Setting score for $which with index $index")
        if (-1 == index) {
            return
        }
        scores[index] = if (correctlySolved) SCORE else NO_SCORE
    }

    public fun isSolvedCorrectly(quiz: Quiz<Any>): Boolean {
        return getScore(quiz) == SCORE
    }

    /**
     * Gets the score for a single quiz.

     * @param which The quiz to look for
     * *
     * @return The score if found, else 0.
     */
    public fun getScore(which: Quiz<Any>): Int {
        try {
            return scores[quizzes!!.indexOf(which)]
        } catch (ioobe: IndexOutOfBoundsException) {
            return 0
        }

    }

    /**
     * @return The sum of all quiz scores within this category.
     */
    public val score: Int
        get() {
            var categoryScore = 0
            for (quizScore in scores) {
                categoryScore += quizScore
            }
            return categoryScore
        }

    /**
     * Checks which quiz is the first unsolved within this category.

     * @return The position of the first unsolved quiz.
     */
    public val firstUnsolvedQuizPosition: Int
        get() {
            if (quizzes == null) {
                return -1
            }
            for (i in quizzes!!.indices) {
                if (!quizzes!![i].isSolved) {
                    return i
                }
            }
            return quizzes!!.size
        }

    override fun toString(): String {
        return "Category{" + "mName='" + name + '\'' + ", mId='" + id + '\'' + ", mTheme=" + theme + ", mQuizzes=" + quizzes + ", mScores=" + Arrays.toString(scores) + ", mSolved=" + isSolved + '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(id)
        dest.writeInt(theme.ordinal)
        dest.writeTypedList(quizzes)
        dest.writeIntArray(scores)
        ParcelableHelper.writeBoolean(dest, isSolved)
    }

    @SuppressWarnings("RedundantIfStatement")
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val category = o as Category?

        if (id != category?.id) {
            return false
        }
        if (name != category?.name) {
            return false
        }
        if (quizzes != category?.quizzes) {
            return false
        }
        if (theme != category?.theme) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + theme.hashCode()
        result = 31 * result + quizzes!!.hashCode()
        return result
    }

    companion object {

        val TAG = "Category"
        val CREATOR: Parcelable.Creator<Category> = object : Parcelable.Creator<Category> {
            override fun createFromParcel(`in`: Parcel): Category {
                return Category(`in`)
            }

            override fun newArray(size: Int): Array<Category?> {
                return arrayOfNulls(size)
            }
        }
        private val SCORE = 8
        private val NO_SCORE = 0
    }
}
