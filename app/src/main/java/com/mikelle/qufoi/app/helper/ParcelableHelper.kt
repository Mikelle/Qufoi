package com.mikelle.qufoi.app.helper

import android.os.Parcel

/**
 * Collection of shared methods ease parcellation of special types.
 */
object ParcelableHelper {

    public fun writeBoolean(dest: Parcel, toWrite: Boolean) {
        dest.writeInt(if (toWrite) 0 else 1)
    }

    public fun readBoolean(`in`: Parcel): Boolean {
        return 0 == `in`.readInt()
    }
}
