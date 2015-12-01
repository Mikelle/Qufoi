package com.mikelle.qufoi.app.model

import android.support.annotation.ColorRes
import android.support.annotation.StyleRes

import com.mikelle.qufoi.app.R

/**
 * A way to make simple changes to the application's appearance at runtime in correlation to its
 * [Category].

 * Usually this should be done via attributes and [android.view.ContextThemeWrapper]s.
 * In one case in Qufoi it is more performant to work like this.
 * This case involves a trade-off between statically loading these themes versus inflation
 * in an adapter backed view without recycling.
 */
enum class Theme private constructor(val primaryColor: Int, val primaryDarkColor: Int,
                                     val windowBackgroundColor: Int, val textPrimaryColor: Int,
                                     val accentColor: Int, val styleId: Int) {
    qufoi(R.color.qufoi_primary, R.color.qufoi_primary_dark, R.color.theme_blue_background, R.color.theme_blue_text, R.color.qufoi_accent, R.style.Qufoi),
    blue(R.color.theme_blue_primary, R.color.theme_blue_primary_dark, R.color.theme_blue_background, R.color.theme_blue_text, R.color.theme_blue_accent, R.style.Qufoi_Blue),
    green(R.color.theme_green_primary, R.color.theme_green_primary_dark, R.color.theme_green_background, R.color.theme_green_text, R.color.theme_green_accent, R.style.Qufoi_Green),
    purple(R.color.theme_purple_primary, R.color.theme_purple_primary_dark, R.color.theme_purple_background, R.color.theme_purple_text, R.color.theme_purple_accent, R.style.Qufoi_Purple),
    red(R.color.theme_red_primary, R.color.theme_red_primary_dark, R.color.theme_red_background, R.color.theme_red_text, R.color.theme_red_accent, R.style.Qufoi_Red),
    yellow(R.color.theme_yellow_primary, R.color.theme_yellow_primary_dark, R.color.theme_yellow_background, R.color.theme_yellow_text, R.color.theme_yellow_accent, R.style.Qufoi_Yellow)
}
