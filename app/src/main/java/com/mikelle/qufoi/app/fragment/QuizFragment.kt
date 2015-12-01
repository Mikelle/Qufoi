package com.mikelle.qufoi.app.fragment

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterViewAnimator
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.adapter.QuizAdapter
import com.mikelle.qufoi.app.adapter.ScoreAdapter
import com.mikelle.qufoi.app.helper.ApiLevelHelper
import com.mikelle.qufoi.app.helper.PreferencesHelper
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.Player
import com.mikelle.qufoi.app.model.Theme
import com.mikelle.qufoi.app.model.quiz.Quiz
import com.mikelle.qufoi.app.persistence.QufoiDatabaseHelper
import com.mikelle.qufoi.app.widget.AvatarView
import com.mikelle.qufoi.app.widget.quiz.AbsQuizView

/**
 * Encapsulates Quiz solving and displays it to the user.
 */
class QuizFragment : android.support.v4.app.Fragment() {
    private var mProgressText: TextView? = null
    private var mQuizSize: Int = 0
    private var mProgressBar: ProgressBar? = null
    private var mCategory: Category? = null
    private var mQuizView: AdapterViewAnimator? = null
    private var mScoreAdapter: ScoreAdapter? = null
    private var mQuizAdapter: QuizAdapter? = null
    private var mSolvedStateListener: SolvedStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val categoryId = arguments.getString(Category.TAG)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val theme = mCategory!!.theme
        val context = ContextThemeWrapper(activity,
                theme.styleId)
        val themedInflater = LayoutInflater.from(context)
        return themedInflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mQuizView = view!!.findViewById(R.id.quiz_view) as AdapterViewAnimator
        decideOnViewToDisplay()
        setQuizViewAnimations()
        val avatar = view.findViewById(R.id.avatar) as AvatarView
        setAvatarDrawable(avatar)
        initProgressToolbar(view)
        super.onViewCreated(view, savedInstanceState)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setQuizViewAnimations() {
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            return
        }
        mQuizView?.setInAnimation(activity, R.animator.slide_in_bottom)
        mQuizView?.setOutAnimation(activity, R.animator.slide_out_top)
    }

    private fun initProgressToolbar(view: View) {
        val firstUnsolvedQuizPosition = mCategory?.firstUnsolvedQuizPosition
        val quizzes = mCategory?.quizzes
        mQuizSize = quizzes!!.size
        mProgressText = view.findViewById(R.id.progress_text) as TextView
        mProgressBar = (view.findViewById(R.id.progress) as ProgressBar)
        mProgressBar?.max = mQuizSize

        setProgress(firstUnsolvedQuizPosition!!)
    }

    private fun setProgress(currentQuizPosition: Int) {
        if (!isAdded) {
            return
        }
        mProgressText?.text = getString(R.string.quiz_of_quizzes, currentQuizPosition, mQuizSize)
        mProgressBar?.progress = currentQuizPosition
    }

    @SuppressWarnings("ConstantConditions")
    private fun setAvatarDrawable(avatarView: AvatarView) {
        val player = PreferencesHelper.getPlayer(activity)
        avatarView.setAvatar(player?.avatar!!.drawableId)
        ViewCompat.animate(avatarView).setInterpolator(FastOutLinearInInterpolator()).setStartDelay(500).scaleX(1f).scaleY(1f).start()
    }

    private fun decideOnViewToDisplay() {
        val isSolved = mCategory?.isSolved
        if (isSolved!!) {
            showSummary()
            if (null != mSolvedStateListener) {
                mSolvedStateListener?.onCategorySolved()
            }
        } else {
            mQuizView?.adapter = quizAdapter
            mQuizView?.setSelection(mCategory!!.firstUnsolvedQuizPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        val focusedChild = mQuizView!!.focusedChild
        if (focusedChild is ViewGroup) {
            val currentView = focusedChild.getChildAt(0)
            if (currentView is AbsQuizView<*>) {
                outState!!.putBundle(KEY_USER_INPUT, currentView.userInput)
            }
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        restoreQuizState(savedInstanceState)
        super.onViewStateRestored(savedInstanceState)
    }

    private fun restoreQuizState(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            return
        }
        mQuizView!!.addOnLayoutChangeListener { view: View?, i: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int,
                                                i6: Int, i7: Int ->
            val currentChild = mQuizView!!.getChildAt(0)
            if (currentChild is ViewGroup) {
                val potentialQuizView = currentChild.getChildAt(0)
                if (potentialQuizView is AbsQuizView<*>) {
                    potentialQuizView.userInput = savedInstanceState.getBundle(KEY_USER_INPUT)
                }
            }
        }

    }

    private val quizAdapter: QuizAdapter
        get() {
            if (null == mQuizAdapter) {
                mQuizAdapter = QuizAdapter(activity, mCategory)
            }
            return mQuizAdapter as QuizAdapter
        }


    public fun showNextPage(): Boolean {
        if (null == mQuizView) {
            return false
        }
        val nextItem = mQuizView!!.displayedChild + 1
        setProgress(nextItem)
        val count = mQuizView?.adapter!!.count
        if (nextItem < count) {
            mQuizView?.showNext()
            QufoiDatabaseHelper.updateCategory(activity, mCategory)
            return true
        }
        markCategorySolved()
        return false
    }

    private fun markCategorySolved() {
        mCategory?.isSolved = true
        QufoiDatabaseHelper.updateCategory(activity, mCategory)
    }

    public fun showSummary() {
        @SuppressWarnings("ConstantConditions")
        val scorecardView = view?.findViewById(R.id.scorecard) as ListView
        mScoreAdapter = scoreAdapter
        scorecardView.adapter = mScoreAdapter
        scorecardView.visibility = View.VISIBLE
        mQuizView?.visibility = View.GONE
    }

    public fun hasSolvedStateListener(): Boolean {
        return mSolvedStateListener != null
    }

    public fun setSolvedStateListener(solvedStateListener: SolvedStateListener) {
        mSolvedStateListener = solvedStateListener
        if (mCategory!!.isSolved && null != mSolvedStateListener) {
            mSolvedStateListener?.onCategorySolved()
        }
    }

    private val scoreAdapter: ScoreAdapter?
        get() {
            if (null == mScoreAdapter) {
                mScoreAdapter = ScoreAdapter(mCategory)
            }
            return mScoreAdapter as ScoreAdapter
        }

    /**
     * Interface definition for a callback to be invoked when the quiz is started.
     */
    interface SolvedStateListener {

        /**
         * This method will be invoked when the category has been solved.
         */
        public fun onCategorySolved()
    }

    companion object {

        private val KEY_USER_INPUT = "USER_INPUT"

        public fun newInstance(categoryId: String?,
                        solvedStateListener: SolvedStateListener?): QuizFragment {
            if (categoryId == null) {
                throw IllegalArgumentException("The category can not be null")
            }
            val args = Bundle()
            args.putString(Category.TAG, categoryId)
            val fragment = QuizFragment()
            if (solvedStateListener != null) {
                fragment.mSolvedStateListener = solvedStateListener
            }
            fragment.arguments = args
            return fragment
        }
    }
}