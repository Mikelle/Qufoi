package com.mikelle.qufoi.app.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.mikelle.qufoi.app.R

/**
 * A simple adapter to display a options of a quiz.
 */
class OptionsQuizAdapter : BaseAdapter {

    private val mOptions: Array<String>
    private val mLayoutId: Int
    private val mAlphabet: Array<String>?

    constructor(options: Array<String>, @LayoutRes layoutId: Int) {
        mOptions = options
        mLayoutId = layoutId
        mAlphabet = null
    }

    constructor(options: Array<String>, @LayoutRes layoutId: Int,
                context: Context, withPrefix: Boolean) {
        mOptions = options
        mLayoutId = layoutId
        if (withPrefix) {
            mAlphabet = context.resources.getStringArray(R.array.alphabet)
        } else {
            mAlphabet = null
        }
    }

    override fun getCount(): Int {
        return mOptions.size()
    }

    override fun getItem(position: Int): String {
        return mOptions[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            convertView = inflater.inflate(mLayoutId, parent, false)
        }
        val text = getText(position)
        (convertView as TextView?)?.setText(text)
        return convertView
    }

    private fun getText(position: Int): String {
        val text: String
        if (mAlphabet == null) {
            text = getItem(position)
        } else {
            text = getPrefix(position) + getItem(position)
        }
        return text
    }

    private fun getPrefix(position: Int): String {
        val length = mAlphabet!!.size()
        if (position >= length || 0 > position) {
            throw IllegalArgumentException(
                    "Only positions between 0 and $length are supported")
        }
        val prefix: StringBuilder
        if (position < length) {
            prefix = StringBuilder(mAlphabet[position])
        } else {
            val tmpPosition = position % length
            prefix = StringBuilder(tmpPosition)
            prefix.append(getPrefix(position - tmpPosition))
        }
        prefix.append(". ")
        return prefix.toString()
    }
}
