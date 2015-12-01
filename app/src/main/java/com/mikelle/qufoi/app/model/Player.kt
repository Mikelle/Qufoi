package com.mikelle.qufoi.app.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Stores values to identify the subject that is currently attempting to solve quizzes.
 */
class Player : Parcelable {
    val firstName: String
    val lastInitial: String
    val avatar: Avatar

    constructor(firstName: String, lastInitial: String, avatar: Avatar) {
        this.firstName = firstName
        this.lastInitial = lastInitial
        this.avatar = avatar
    }

    protected constructor(`in`: Parcel) {
        firstName = `in`.readString()
        lastInitial = `in`.readString()
        avatar = Avatar.values()[`in`.readInt()]
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(firstName)
        dest.writeString(lastInitial)
        dest.writeInt(avatar.ordinal)
    }

    @SuppressWarnings("RedundantIfStatement")
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val player = o as Player?

        if (avatar !== player?.avatar) {
            return false
        }
        if (firstName != player?.firstName) {
            return false
        }
        if (lastInitial != player?.lastInitial) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + lastInitial.hashCode()
        result = 31 * result + avatar.hashCode()
        return result
    }

    companion object {

        val CREATOR: Parcelable.Creator<Player> = object : Parcelable.Creator<Player> {
            override fun createFromParcel(`in`: Parcel): Player {
                return Player(`in`)
            }

            override fun newArray(size: Int): Array<Player?> {
                return arrayOfNulls(size)
            }
        }
    }
}
