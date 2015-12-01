package com.mikelle.qufoi.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.fragment.CategorySelectionFragment
import com.mikelle.qufoi.app.helper.ApiLevelHelper
import com.mikelle.qufoi.app.helper.PreferencesHelper
import com.mikelle.qufoi.app.model.Player
import com.mikelle.qufoi.app.persistence.QufoiDatabaseHelper
import com.mikelle.qufoi.app.widget.AvatarView

class CategorySelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_category_selection)
        val player = intent.getParcelableExtra<Player>(EXTRA_PLAYER)
        if (!PreferencesHelper.isSignedIn(this) && player != null) {
            PreferencesHelper.writeToPreferences(this, player)
        }
        setUpToolbar(player)
        if (savedInstanceState == null) {
            attachCategoryGridFragment()
        } else {
            setProgressBarVisibility(View.GONE)
        }
        supportPostponeEnterTransition()
    }

    override fun onResume() {
        super.onResume()
        val scoreView = findViewById(R.id.score) as TextView
        val score = QufoiDatabaseHelper.getScore(this)
        scoreView.text = getString(R.string.x_points, score)
    }

    private fun setUpToolbar(player: Player) {
        val toolbar = findViewById(R.id.toolbar_player) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val avatarView = toolbar.findViewById(R.id.avatar) as AvatarView
        avatarView.setAvatar(player.avatar.drawableId)
        (toolbar.findViewById(R.id.title) as TextView).text = getDisplayName(player)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_category, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val fragment = supportFragmentManager.findFragmentById(R.id.category_container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NewApi")
    private fun signOut() {
        PreferencesHelper.signOut(this)
        QufoiDatabaseHelper.reset(this)
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.category_enter)
        }
        SignInActivity.start(this, false)
        ActivityCompat.finishAfterTransition(this)
    }

    private fun getDisplayName(player: Player): String {
        return getString(R.string.player_display_name, player.firstName,
                player.lastInitial)
    }

    private fun attachCategoryGridFragment() {
        val supportFragmentManager = supportFragmentManager
        var fragment = supportFragmentManager.findFragmentById(R.id.category_container)
        if (fragment !is CategorySelectionFragment) {
            fragment = CategorySelectionFragment.newInstance()
        }
        supportFragmentManager.beginTransaction().replace(R.id.category_container, fragment).commit()
        setProgressBarVisibility(View.GONE)
    }

    private fun setProgressBarVisibility(visibility: Int) {
        findViewById(R.id.progress).visibility = visibility
    }

    companion object {

        private val EXTRA_PLAYER = "player"

        fun start(activity: Activity?, player: Player?, options: ActivityOptionsCompat?) {
            val starter = getStartIntent(activity, player)
            ActivityCompat.startActivity(activity, starter, options?.toBundle())
        }

        fun start(context: Context?, player: Player?) {
            val starter = getStartIntent(context, player)
            context?.startActivity(starter)
        }

        internal fun getStartIntent(context: Context?, player: Player?): Intent? {
            val starter = Intent(context, CategorySelectionActivity::class.java)
            starter.putExtra(EXTRA_PLAYER, player)
            return starter
        }
    }
}

