package com.mikelle.qufoi.app.helper

import android.util.Log

import org.json.JSONArray
import org.json.JSONException

/**
 * Helper class to make unsafe types safe to use in the java world.
 */
object JsonHelper {

    private val TAG = "JsonHelper"

    public fun jsonArrayToStringArray(json: String): Array<String?> {
        try {
            val jsonArray = JSONArray(json)
            val stringArray = arrayOfNulls<String>(jsonArray.length())
            for (i in 0..jsonArray.length() - 1) {
                stringArray[i] = jsonArray.getString(i)
            }
            return stringArray
        } catch (e: JSONException) {
            Log.e(TAG, "Error during Json processing: ", e)
        }

        return arrayOfNulls(0)
    }

    public fun jsonArrayToIntArray(json: String): IntArray {
        try {
            val jsonArray = JSONArray(json)
            val intArray = IntArray(jsonArray.length())
            for (i in 0..jsonArray.length() - 1) {
                intArray[i] = jsonArray.getInt(i)
            }
            return intArray
        } catch (e: JSONException) {
            Log.e(TAG, "Error during Json processing: ", e)
        }

        return IntArray(0)
    }
}
