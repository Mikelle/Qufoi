package com.mikelle.qufoi.app.helper

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.support.v4.util.Pair
import android.view.View

import java.util.ArrayList
import java.util.Arrays

/**
 * Helper class for creating content transitions used with [android.app.ActivityOptions].
 */
object TransitionHelper {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun createSafeTransitionParticipants(activity: Activity,
                                         includeStatusBar: Boolean,
                                         vararg otherParticipants: Pair<Any, Any>): Array<out Pair<Any, Any>>? {
        val decor = activity.window.decorView
        var statusBar: View? = null
        if (includeStatusBar) {
            statusBar = decor.findViewById(android.R.id.statusBarBackground)
        }
        val navBar = decor.findViewById(android.R.id.navigationBarBackground)

        // Create pair of transition participants.
        val participants = ArrayList<Pair<Any, Any>>(3)
        addNonNullViewToTransitionParticipants(statusBar, participants)
        addNonNullViewToTransitionParticipants(navBar, participants)
        // only add transition participants if there's at least one none-null element
        if (otherParticipants != null && !(otherParticipants.size() == 1 && otherParticipants[0] == null)) {
            participants.addAll(Arrays.asList(*otherParticipants))
        }
        //noinspection unchecked
        return participants.toArray<Pair<Any, Any>>(arrayOfNulls<Pair<Any, Any>>(participants.size))
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addNonNullViewToTransitionParticipants(view: View?, participants: MutableList<Pair<Any, Any>>) {
        if (view == null) {
            return
        }
        participants.add(Pair(view, view.transitionName))
    }

}
