package com.mikelle.qufoi.app.helper

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

import com.mikelle.qufoi.app.model.Avatar
import com.mikelle.qufoi.app.model.Player

/**
 * Easy storage and retrieval of preferences.
 */
object PreferencesHelper {

    private val PLAYER_PREFERENCES = "playerPreferences"
    private val PREFERENCE_FIRST_NAME = PLAYER_PREFERENCES + ".firstName"
    private val PREFERENCE_LAST_INITIAL = PLAYER_PREFERENCES + ".lastInitial"
    private val PREFERENCE_AVATAR = PLAYER_PREFERENCES + ".avatar"

    public fun writeToPreferences(context: Context, player: Player) {
        val editor = getEditor(context)
        editor.putString(PREFERENCE_FIRST_NAME, player.firstName)
        editor.putString(PREFERENCE_LAST_INITIAL, player.lastInitial)
        editor.putString(PREFERENCE_AVATAR, player.avatar.name)
        editor.apply()
    }

    public fun getPlayer(context: Context): Player? {
        val preferences = getSharedPreferences(context)
        val firstName = preferences.getString(PREFERENCE_FIRST_NAME, null)
        val lastInitial = preferences.getString(PREFERENCE_LAST_INITIAL, null)
        val avatarPreference = preferences.getString(PREFERENCE_AVATAR, null)
        val avatar: Avatar?
        if (null != avatarPreference) {
            avatar = Avatar.valueOf(avatarPreference)
        } else {
            avatar = null
        }

        if (null == firstName || null == lastInitial || null == avatar) {
            return null
        }
        return Player(firstName, lastInitial, avatar)
    }

    public fun signOut(context: Context) {
        val editor = getEditor(context)
        editor.remove(PREFERENCE_FIRST_NAME)
        editor.remove(PREFERENCE_LAST_INITIAL)
        editor.remove(PREFERENCE_AVATAR)
        editor.apply()
    }

    public fun isSignedIn(context: Context): Boolean {
        val preferences = getSharedPreferences(context)
        return preferences.contains(PREFERENCE_FIRST_NAME) && preferences.contains(PREFERENCE_LAST_INITIAL) && preferences.contains(PREFERENCE_AVATAR)
    }

    public fun isInputDataValid(firstName: CharSequence, lastInitial: CharSequence): Boolean {
        return !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastInitial)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        val preferences = getSharedPreferences(context)
        return preferences.edit()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PLAYER_PREFERENCES, Context.MODE_PRIVATE)
    }
}
