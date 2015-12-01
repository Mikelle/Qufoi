package com.mikelle.qufoi.app.fragment

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.GridView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.activity.CategorySelectionActivity
import com.mikelle.qufoi.app.adapter.AvatarAdapter
import com.mikelle.qufoi.app.helper.PreferencesHelper
import com.mikelle.qufoi.app.helper.TransitionHelper
import com.mikelle.qufoi.app.model.Avatar
import com.mikelle.qufoi.app.model.Player

/**
 * Enable selection of an [Avatar] and user name.
 */
class SignInFragment : Fragment() {
    private var mPlayer: Player? = null
    private var mFirstName: EditText? = null
    private var mLastInitial: EditText? = null
    private var mSelectedAvatar: Avatar? = null
    private var mSelectedAvatarView: View? = null
    private var mAvatarGrid: GridView? = null
    private var mDoneFab: FloatingActionButton? = null
    private var edit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val savedAvatarIndex = savedInstanceState.getInt(KEY_SELECTED_AVATAR_INDEX)
            mSelectedAvatar = Avatar.values()[savedAvatarIndex]
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val contentView = inflater?.inflate(R.layout.fragment_sign_in, container, false)
        contentView?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            setUpGridView(view)
        }
        return contentView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(KEY_SELECTED_AVATAR_INDEX, mSelectedAvatar?.ordinal!!)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        assurePlayerInit()
        checkIsInEditMode()

        if (null == mPlayer || edit) {
            view?.findViewById(R.id.empty)?.visibility = View.GONE
            view?.findViewById(R.id.content)?.visibility = View.VISIBLE
            initContentViews(view as View)
            initContents()
        } else {
            val activity = activity
            CategorySelectionActivity.start(activity, mPlayer as Player)
            activity.finish()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun checkIsInEditMode() {
        val arguments = arguments
        if (null == arguments) {
            edit = false
        } else {
            edit = arguments.getBoolean(ARG_EDIT, false)
        }
    }

    private fun initContentViews(view: View) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                /* no-op */
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 0) {
                    mDoneFab?.hide()
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (isAvatarSelected && isInputDataValid) {
                    mDoneFab?.show()
                }
            }
        }

        mFirstName = view.findViewById(R.id.first_name) as EditText
        mFirstName?.addTextChangedListener(textWatcher)
        mLastInitial = view.findViewById(R.id.last_initial) as EditText
        mLastInitial?.addTextChangedListener(textWatcher)
        mDoneFab = view.findViewById(R.id.done) as FloatingActionButton
        mDoneFab?.setOnClickListener { v ->
            when (v.id) {
                R.id.done -> {
                    savePlayer(activity)
                    removeDoneFab(Runnable {
                        if (null == mSelectedAvatarView) {
                            performSignInWithTransition(mAvatarGrid?.getChildAt(
                                    mSelectedAvatar?.ordinal!!)!!)
                        } else {
                            performSignInWithTransition(mSelectedAvatarView as View)
                        }
                    })
                }
                else -> throw UnsupportedOperationException(
                        "The onClick method has not been implemented for " + resources.getResourceEntryName(v.id))
            }
        }
    }

    private fun removeDoneFab(endAction: Runnable?) {
        ViewCompat.animate(mDoneFab).scaleX(0f).scaleY(0f).setInterpolator(FastOutSlowInInterpolator()).withEndAction(endAction).start()
    }

    private fun setUpGridView(container: View) {
        mAvatarGrid = container.findViewById(R.id.avatars) as GridView
        mAvatarGrid?.adapter = AvatarAdapter(activity)
        mAvatarGrid?.numColumns = calculateSpanCount()
        if (mSelectedAvatar != null) {
            mAvatarGrid?.setItemChecked(mSelectedAvatar!!.ordinal, true)
        }
    }

    private fun performSignInWithTransition(v: View) {
        val activity = activity

        val pairs = TransitionHelper.createSafeTransitionParticipants(activity, true,
                Pair(v, activity.getString(R.string.transition_avatar)))
    }

    private fun initContents() {
        assurePlayerInit()
        if (null != mPlayer) {
            mFirstName?.setText(mPlayer?.firstName)
            mLastInitial?.setText(mPlayer?.lastInitial)
            mSelectedAvatar = mPlayer?.avatar
        }
    }

    private fun assurePlayerInit() {
        if (null == mPlayer) {
            mPlayer = PreferencesHelper.getPlayer(activity)
        }
    }

    private fun savePlayer(activity: Activity) {
        mPlayer = Player(mFirstName?.text.toString(), mLastInitial?.text.toString(),
                mSelectedAvatar!!)
        PreferencesHelper.writeToPreferences(activity, mPlayer as Player)
    }

    private val isAvatarSelected: Boolean
        get() = mSelectedAvatarView != null || mSelectedAvatar != null

    private val isInputDataValid: Boolean
        get() = PreferencesHelper.isInputDataValid(mFirstName?.text!!, mLastInitial?.text!!)

    /**
     * Calculates spans for avatars dynamically.

     * @return The recommended amount of columns.
     */
    private fun calculateSpanCount(): Int {
        val avatarSize = resources.getDimensionPixelSize(R.dimen.size_fab)
        val avatarPadding = resources.getDimensionPixelSize(R.dimen.spacing_double)
        return mAvatarGrid!!.width / (avatarSize + avatarPadding)
    }

    companion object {

        private val ARG_EDIT = "EDIT"
        private val KEY_SELECTED_AVATAR_INDEX = "selectedAvatarIndex"

        fun newInstance(edit: Boolean): SignInFragment {
            val args = Bundle()
            args.putBoolean(ARG_EDIT, edit)
            val fragment = SignInFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
