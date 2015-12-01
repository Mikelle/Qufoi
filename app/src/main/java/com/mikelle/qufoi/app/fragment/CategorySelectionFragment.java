package com.mikelle.qufoi.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikelle.qufoi.app.R;
import com.mikelle.qufoi.app.activity.QuizActivity;
import com.mikelle.qufoi.app.adapter.CategoryAdapter;
import com.mikelle.qufoi.app.helper.TransitionHelper;
import com.mikelle.qufoi.app.model.Category;
import com.mikelle.qufoi.app.model.JsonAttributes;
import com.mikelle.qufoi.app.widget.OffsetDecoration;

public class CategorySelectionFragment extends Fragment {

    private CategoryAdapter mAdapter;
    private static final int REQUEST_CATEGORY = 0x2300;

    public static CategorySelectionFragment newInstance() {
        return new CategorySelectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpQuizGrid((RecyclerView) view.findViewById(R.id.categories));
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpQuizGrid(RecyclerView categoriesView) {
        final int spacing = getContext().getResources()
                .getDimensionPixelSize(R.dimen.spacing_nano);
        categoriesView.addItemDecoration(new OffsetDecoration(spacing));
        mAdapter = new CategoryAdapter(getActivity());
        mAdapter.setOnItemClickListener(
                new CategoryAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        Activity activity = getActivity();
                        startQuizActivityWithTransition(activity,
                                v.findViewById(R.id.category_title),
                                mAdapter.getItem(position));
                    }
                });
        categoriesView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        getActivity().supportStartPostponedEnterTransition();
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CATEGORY && resultCode == R.id.solved) {
            mAdapter.notifyItemChanged(data.getStringExtra(JsonAttributes.ID));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startQuizActivityWithTransition(Activity activity, View toolbar,
                                                 Category category) {

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);

        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        Intent startIntent = QuizActivity.getStartIntent(activity, category);
        ActivityCompat.startActivityForResult(activity,
                startIntent,
                REQUEST_CATEGORY,
                transitionBundle);
    }

}
