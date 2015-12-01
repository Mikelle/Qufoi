package com.mikelle.qufoi.app.helper

import android.os.Build

/**
 * Encapsulates checking api levels.
 */
object ApiLevelHelper {

    public fun isAtLeast(apiLevel: Int): Boolean {
        return Build.VERSION.SDK_INT >= apiLevel
    }

    public fun isLowerThan(apiLevel: Int): Boolean {
        return Build.VERSION.SDK_INT < apiLevel
    }
}
