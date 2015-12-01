package com.mikelle.qufoi.app.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity


import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.fragment.SignInFragment
import com.mikelle.qufoi.app.helper.PreferencesHelper

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val edit = isInEditMode
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.sign_in_container, SignInFragment.newInstance(edit)).commit()
        }
    }

    override fun onStop() {
        super.onStop()
        if (PreferencesHelper.isSignedIn(this)) {
            finish()
        }
    }

    private val isInEditMode: Boolean
        get() {
            val intent = intent
            var edit = false
            if (null != intent) {
                edit = intent.getBooleanExtra(EXTRA_EDIT, false)
            }
            return edit
        }

    companion object {

        private val EXTRA_EDIT = "EDIT"

        fun start(activity: Activity, edit: Boolean?) {
            val starter = Intent(activity, SignInActivity::class.java)
            starter.putExtra(EXTRA_EDIT, edit)
            //noinspection unchecked
            ActivityCompat.startActivity(activity,
                    starter,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle())
        }
    }
}
