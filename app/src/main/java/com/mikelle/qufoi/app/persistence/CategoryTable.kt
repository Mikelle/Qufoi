package com.mikelle.qufoi.app.persistence

import android.provider.BaseColumns

/**
 * Structure of the category table.
 */
interface CategoryTable {
    companion object {

        val NAME = "category"

        val COLUMN_ID = BaseColumns._ID
        val COLUMN_NAME = "name"
        val COLUMN_THEME = "theme"
        val COLUMN_SCORES = "scores"
        val COLUMN_SOLVED = "solved"

        val PROJECTION = arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_THEME, COLUMN_SOLVED, COLUMN_SCORES)

        val CREATE = "CREATE TABLE $NAME ($COLUMN_ID TEXT PRIMARY KEY, $COLUMN_NAME TEXT NOT NULL, $COLUMN_THEME TEXT NOT NULL, $COLUMN_SOLVED TEXT NOT NULL, $COLUMN_SCORES TEXT);"
    }
}
