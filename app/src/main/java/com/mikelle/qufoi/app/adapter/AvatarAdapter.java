package com.mikelle.qufoi.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mikelle.qufoi.app.R;
import com.mikelle.qufoi.app.model.Avatar;
import com.mikelle.qufoi.app.widget.AvatarView;

/**
 * Adapter to display {@link Avatar} icons.
 */
public class AvatarAdapter extends BaseAdapter {

    private static final Avatar[] mAvatars = Avatar.values();

    private final LayoutInflater mLayoutInflater;

    public AvatarAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_avatar, parent, false);
        }
        setAvatar((AvatarView) convertView, mAvatars[position]);
        return convertView;
    }

    private void setAvatar(AvatarView mIcon, Avatar avatar) {
        mIcon.setAvatar(avatar.getDrawableId());
        mIcon.setContentDescription(avatar.getNameForAccessibility());
    }

    @Override
    public int getCount() {
        return mAvatars.length;
    }

    @Override
    public Object getItem(int position) {
        return mAvatars[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
