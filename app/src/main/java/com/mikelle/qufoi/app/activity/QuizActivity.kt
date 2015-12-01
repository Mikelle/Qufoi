package com.mikelle.qufoi.app.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.Window
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.fragment.QuizFragment
import com.mikelle.qufoi.app.helper.ApiLevelHelper
import com.mikelle.qufoi.app.helper.ViewUtils
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.JsonAttributes
import com.mikelle.qufoi.app.persistence.QufoiDatabaseHelper
import com.mikelle.qufoi.app.widget.TextSharedElementCallback
import contrib.CountingIdlingResource

import com.mikelle.qufoi.app.adapter.CategoryAdapter.Companion.DRAWABLE

class QuizActivity : AppCompatActivity() {

    private var mInterpolator: Interpolator? = null
    private var mCategory: Category? = null
    private var mQuizFragment: QuizFragment? = null
    private var mQuizFab: FloatingActionButton? = null
    private var mSavedStateIsPlaying: Boolean = false
    private var mIcon: ImageView? = null
    private var mCircularReveal: Animator? = null
    private var mColorChange: ObjectAnimator? = null
    var countingIdlingResource: CountingIdlingResource? = null
        private set
    private var mToolbarBack: View? = null


    internal val mOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.fab_quiz -> startQuizFromClickOn(v)
            R.id.submitAnswer -> submitAnswer()
            R.id.quiz_done -> ActivityCompat.finishAfterTransition(this@QuizActivity)
            R.id.back -> onBackPressed()
            else -> throw UnsupportedOperationException(
                    "OnClick has not been implemented for " + resources.getResourceName(v.id))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        countingIdlingResource = CountingIdlingResource("Quiz")
        val categoryId = intent.getStringExtra(Category.TAG)
        mInterpolator = FastOutSlowInInterpolator()
        if (null != savedInstanceState) {
            mSavedStateIsPlaying = savedInstanceState.getBoolean(STATE_IS_PLAYING)
        }
        super.onCreate(savedInstanceState)
        populate(categoryId)
        val categoryNameTextSize = resources.getDimensionPixelSize(R.dimen.category_item_text_size)
        val paddingStart = resources.getDimensionPixelSize(R.dimen.spacing_double)
        val startDelay = resources.getInteger(R.integer.toolbar_transition_duration)
        ActivityCompat.setEnterSharedElementCallback(this,
                object : TextSharedElementCallback(categoryNameTextSize.toFloat(), paddingStart) {
                    override fun onSharedElementStart(sharedElementNames: List<String>?,
                                                      sharedElements: List<View>?,
                                                      sharedElementSnapshots: List<View>?) {
                        super.onSharedElementStart(sharedElementNames,
                                sharedElements,
                                sharedElementSnapshots)
                        mToolbarBack?.scaleX = 0f
                        mToolbarBack?.scaleY = 0f
                    }

                    override fun onSharedElementEnd(sharedElementNames: List<String>?,
                                                    sharedElements: List<View>?,
                                                    sharedElementSnapshots: List<View>?) {
                        super.onSharedElementEnd(sharedElementNames,
                                sharedElements,
                                sharedElementSnapshots)
                        ViewCompat.animate(mToolbarBack).setStartDelay(startDelay.toLong()).scaleX(1f).scaleY(1f).alpha(1f)
                    }
                })
    }

    override fun onResume() {
        if (mSavedStateIsPlaying) {
            mQuizFragment = supportFragmentManager.findFragmentByTag(
                    FRAGMENT_TAG) as QuizFragment
            if (!mQuizFragment!!.hasSolvedStateListener()) {
                mQuizFragment?.setSolvedStateListener(solvedStateListener)
            }
            findViewById(R.id.quiz_fragment_container).visibility = View.VISIBLE
            mQuizFab?.hide()
        } else {
            initQuizFragment()
        }
        super.onResume()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_IS_PLAYING, mQuizFab?.visibility == View.GONE)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (mIcon == null || mQuizFab == null) {
            // Skip the animation if icon or fab are not initialized.
            super.onBackPressed()
            return
        }

        ViewCompat.animate(mToolbarBack).scaleX(0f).scaleY(0f).alpha(0f).setDuration(100).start()

        // Scale the icon and fab to 0 size before calling onBackPressed if it exists.
        ViewCompat.animate(mIcon).scaleX(.7f).scaleY(.7f).alpha(0f).setInterpolator(mInterpolator).start()

        ViewCompat.animate(mQuizFab).scaleX(0f).scaleY(0f).setInterpolator(mInterpolator).setStartDelay(100).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            @SuppressLint("NewApi")
            override fun onAnimationEnd(view: View?) {
                if (isFinishing || (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.JELLY_BEAN_MR1) && isDestroyed)) {
                    return
                }
                super@QuizActivity.onBackPressed()
            }
        }).start()
    }

    private fun startQuizFromClickOn(clickedView: View) {
        initQuizFragment()
        supportFragmentManager.beginTransaction().replace(R.id.quiz_fragment_container, mQuizFragment, FRAGMENT_TAG).commit()
        val container = findViewById(R.id.quiz_fragment_container) as FrameLayout
        container.setBackgroundColor(ContextCompat.getColor(this, mCategory?.theme!!.windowBackgroundColor))
        revealFragmentContainer(clickedView, container)
        // the toolbar should not have more elevation than the content while playing
        setToolbarElevation(false)
    }

    private fun revealFragmentContainer(clickedView: View,
                                        fragmentContainer: FrameLayout) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            revealFragmentContainerLollipop(clickedView, fragmentContainer)
        } else {
            fragmentContainer.visibility = View.VISIBLE
            clickedView.visibility = View.GONE
            mIcon?.visibility = View.GONE
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun revealFragmentContainerLollipop(clickedView: View,
                                                fragmentContainer: FrameLayout) {
        prepareCircularReveal(clickedView, fragmentContainer)

        ViewCompat.animate(clickedView).scaleX(0f).scaleY(0f).alpha(0f).setInterpolator(mInterpolator).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            override fun onAnimationEnd(view: View?) {
                fragmentContainer.visibility = View.VISIBLE
                clickedView.visibility = View.GONE
            }
        }).start()

        fragmentContainer.visibility = View.VISIBLE
        val animatorSet = AnimatorSet()
        animatorSet.play(mCircularReveal).with(mColorChange)
        animatorSet.start()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun prepareCircularReveal(startView: View, targetView: FrameLayout) {
        val centerX = (startView.left + startView.right) / 2
        // Subtract the start view's height to adjust for relative coordinates on screen.
        val centerY = (startView.top + startView.bottom) / 2 - startView.height
        val endRadius = Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()
        mCircularReveal = ViewAnimationUtils.createCircularReveal(
                targetView, centerX, centerY, startView.width.toFloat(), endRadius)
        mCircularReveal?.interpolator = FastOutLinearInInterpolator()

        mCircularReveal?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mIcon?.visibility = View.GONE
                mCircularReveal?.removeListener(this)
            }
        })
        // Adding a color animation from the FAB's color to transparent creates a dissolve like
        // effect to the circular reveal.
        val accentColor = ContextCompat.getColor(this, mCategory?.theme!!.accentColor)
        mColorChange = ObjectAnimator.ofInt(targetView,
                ViewUtils.FOREGROUND_COLOR, accentColor, Color.TRANSPARENT)
        mColorChange?.setEvaluator(ArgbEvaluator())
        mColorChange?.interpolator = mInterpolator
    }

    @SuppressLint("NewApi")
    fun setToolbarElevation(shouldElevate: Boolean) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            mToolbarBack?.setElevation(if (shouldElevate)
                resources.getDimension(R.dimen.elevation_header)
            else
                0.toFloat())
        }
    }

    private fun initQuizFragment() {
        if (mQuizFragment != null) {
            return
        }
        mQuizFragment = QuizFragment.newInstance(mCategory?.id, solvedStateListener)
        // the toolbar should not have more elevation than the content while playing
        setToolbarElevation(false)
    }

    private /* We're re-using the already existing fab and give it some
                 * new values. This has to run delayed due to the queued animation
                 * to hide the fab initially.
                 */ val solvedStateListener: QuizFragment.SolvedStateListener
        get() = object : QuizFragment.SolvedStateListener {
            override fun onCategorySolved() {
                setResultSolved()
                setToolbarElevation(true)
                displayDoneFab()
            }

            private fun displayDoneFab() {
                if (null != mCircularReveal && mCircularReveal!!.isRunning) {
                    mCircularReveal?.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            showQuizFabWithDoneIcon()
                            mCircularReveal?.removeListener(this)
                        }
                    })
                } else {
                    showQuizFabWithDoneIcon()
                }
            }

            private fun showQuizFabWithDoneIcon() {
                mQuizFab?.setImageResource(R.drawable.ic_tick)
                mQuizFab?.id = R.id.quiz_done
                mQuizFab?.visibility = View.VISIBLE
                mQuizFab?.scaleX = 0f
                mQuizFab?.scaleY = 0f
                ViewCompat.animate(mQuizFab).scaleX(1f).scaleY(1f).setInterpolator(mInterpolator).setListener(null).start()
            }
        }

    private fun setResultSolved() {
        val categoryIntent = Intent()
        categoryIntent.putExtra(JsonAttributes.ID, mCategory?.id)
        setResult(R.id.solved, categoryIntent)
    }

    /**
     * Proceeds the quiz to it's next state.
     */
    fun proceed() {
        submitAnswer()
    }

    /**
     * Solely exists for testing purposes and making sure Espresso does not get confused.
     */
    fun lockIdlingResource() {
        countingIdlingResource?.increment()
    }

    private fun submitAnswer() {
        countingIdlingResource!!.decrement()
        if (!mQuizFragment!!.showNextPage()) {
            mQuizFragment?.showSummary()
            setResultSolved()
            return
        }
        setToolbarElevation(false)
    }

    @SuppressLint("NewApi")
    private fun populate(categoryId: String?) {
        if (null == categoryId) {
            Log.w(TAG, "Didn't find a category. Finishing")
            finish()
        }
        setTheme(mCategory?.theme!!.styleId)
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            val window = window
            window.statusBarColor = ContextCompat.getColor(this,
                    mCategory?.theme!!.primaryDarkColor)
        }
        initLayout(mCategory?.id)
        initToolbar(mCategory)
    }

    private fun initLayout(categoryId: String?) {
        setContentView(R.layout.activity_quiz)
        //noinspection PrivateResource
        mIcon = findViewById(R.id.icon) as ImageView
        val resId = resources.getIdentifier(IMAGE_CATEGORY + categoryId, DRAWABLE,
                applicationContext.packageName)
        mIcon?.setImageResource(resId)
        mIcon?.setImageResource(resId)
        ViewCompat.animate(mIcon).scaleX(1f).scaleY(1f).alpha(1f).setInterpolator(mInterpolator).setStartDelay(300).start()
        mQuizFab = findViewById(R.id.fab_quiz) as FloatingActionButton
        mQuizFab?.setImageResource(R.drawable.ic_play)
        if (mSavedStateIsPlaying) {
            mQuizFab?.hide()
        } else {
            mQuizFab?.show()
        }
        mQuizFab?.setOnClickListener(mOnClickListener)
    }

    private fun initToolbar(category: Category?) {
        mToolbarBack = findViewById(R.id.back)
        mToolbarBack?.setOnClickListener(mOnClickListener)
        val titleView = findViewById(R.id.category_title) as TextView
        titleView.text = category?.name
        titleView.setTextColor(ContextCompat.getColor(this,
                category?.theme!!.textPrimaryColor))
        if (mSavedStateIsPlaying) {
            // the toolbar should not have more elevation than the content while playing
            setToolbarElevation(false)
        }
    }

    companion object {

        private val TAG = "QuizActivity"
        private val IMAGE_CATEGORY = "image_category_"
        private val STATE_IS_PLAYING = "isPlaying"
        private val FRAGMENT_TAG = "Quiz"

        fun getStartIntent(context: Context, category: Category?): Intent {
            val starter = Intent(context, QuizActivity::class.java)
            starter.putExtra(Category.TAG, category?.id)
            return starter
        }
    }
}
