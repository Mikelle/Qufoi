package com.mikelle.qufoi.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.model.Avatar
import com.mikelle.qufoi.app.widget.AvatarView
/**
 * Adapter to display [Avatar] icons.
 */
class AvatarAdapter(context: Context) : BaseAdapter() {

    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_avatar, parent, false)
        }
        setAvatar(convertView as AvatarView, mAvatars[position])
        return convertView
    }

    private fun setAvatar(mIcon: AvatarView, avatar: Avatar) {
        mIcon.setAvatar(avatar.drawableId)
        mIcon.contentDescription = avatar.nameForAccessibility
    }

    override fun getCount(): Int {
        return mAvatars.size()
    }

    override fun getItem(position: Int): Any {
        return mAvatars[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {

        private val mAvatars = Avatar.values()
    }
}
