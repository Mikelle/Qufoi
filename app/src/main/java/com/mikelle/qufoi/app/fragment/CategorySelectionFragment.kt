package com.mikelle.qufoi.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.activity.QuizActivity
import com.mikelle.qufoi.app.adapter.CategoryAdapter
import com.mikelle.qufoi.app.helper.TransitionHelper
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.JsonAttributes
import com.mikelle.qufoi.app.widget.OffsetDecoration

class CategorySelectionFragment : Fragment() {

    private var mAdapter: CategoryAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setUpQuizGrid(view?.findViewById(R.id.categories) as RecyclerView)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpQuizGrid(categoriesView: RecyclerView) {
        val spacing = context.resources.getDimensionPixelSize(R.dimen.spacing_nano)
        categoriesView.addItemDecoration(OffsetDecoration(spacing))
        mAdapter = CategoryAdapter(activity)
        mAdapter?.setOnItemClickListener(
                object : CategoryAdapter.OnItemClickListener {
                    override fun onClick(v: View, position: Int) {
                        val activity = activity
                        startQuizActivityWithTransition(activity,
                                v.findViewById(R.id.category_title),
                                mAdapter?.getItem(position))
                    }
                })
        categoriesView.adapter = mAdapter
    }

    override fun onResume() {
        activity.supportStartPostponedEnterTransition()
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CATEGORY && resultCode == R.id.solved) {
            mAdapter?.notifyItemChanged(data?.getStringExtra(JsonAttributes.ID))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startQuizActivityWithTransition(activity: Activity, toolbar: View,
                                                category: Category?) {

        val pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                Pair(toolbar, activity.getString(R.string.transition_toolbar)))
        @SuppressWarnings("unchecked")
        val startIntent = QuizActivity.getStartIntent(activity, category)
    }

    companion object {
        private val REQUEST_CATEGORY = 8960

        fun newInstance(): CategorySelectionFragment {
            return CategorySelectionFragment()
        }
    }

}
