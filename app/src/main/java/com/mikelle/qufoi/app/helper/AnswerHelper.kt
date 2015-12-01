package com.mikelle.qufoi.app.helper

import android.util.Log
import android.util.SparseBooleanArray

/**
 * Collection of methods to convert answers to human readable forms.
 */
object AnswerHelper {

    internal val SEPARATOR = System.getProperty("line.separator")
    private val TAG = "AnswerHelper"

    fun getAnswer(answers: Array<String?>): String? {
        val readableAnswer = StringBuilder()
        //Iterate over all answers
        for (i in answers.indices) {
            val answer = answers[i]
            readableAnswer.append(answer)
            //Don't add a separator for the last answer
            if (i < answers.size() - 1) {
                readableAnswer.append(SEPARATOR)
            }
        }
        return readableAnswer.toString()
    }

    public fun getAnswer(answers: IntArray?, options: Array<String>): String? {
        val readableAnswers = arrayOfNulls<String>(answers!!.size())
        for (i in answers.indices) {
            val humanReadableAnswer = options[answers[i]]
            readableAnswers[i] = humanReadableAnswer
        }
        return getAnswer(readableAnswers)
    }


    public fun isAnswerCorrect(checkedItems: SparseBooleanArray?, answerIds: IntArray?): Boolean {
        if (null == checkedItems || null == answerIds) {
            Log.i(TAG, "isAnswerCorrect got a null parameter input.")
            return false
        }
        for (answer in answerIds) {
            if (0 > checkedItems.indexOfKey(answer)) {
                return false
            }
        }
        return checkedItems.size() == answerIds.size()
    }

}
