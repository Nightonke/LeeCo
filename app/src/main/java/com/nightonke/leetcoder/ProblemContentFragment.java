package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thefinestartist.finestwebview.FinestWebView;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;

/**
 * Created by Weiping on 2016/1/8.
 */

public class ProblemContentFragment extends Fragment
        implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ProblemActivity activity;
    private Context mContext;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Boolean isRefreshing = false;
    private RelativeLayout reloadLayout;
    private ProgressBar progressBar;
    private TextView reload;

    private RichText content;
    private TagGroup tags;
    private TextView originalProblem;
    private TagGroup similarProblems;

    private CoordinatorLayout snackbarLayout;

    private OnTagClickListener onTagClickListener;
    private OnSimilarProblemClickListener onSimilarProblemClickListener;

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);

        if (context instanceof ProblemActivity){
            activity = (ProblemActivity)context;
        }

        if (context instanceof OnTagClickListener){
            onTagClickListener = (OnTagClickListener)context;
        }

        if (context instanceof OnSimilarProblemClickListener){
            onSimilarProblemClickListener = (OnSimilarProblemClickListener)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentFragment = inflater.inflate(R.layout.fragment_problem_content, container, false);

        reloadLayout = (RelativeLayout)contentFragment.findViewById(R.id.loading_layout);
        reloadLayout.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar)contentFragment.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        reload = (TextView)contentFragment.findViewById(R.id.reload);
        reload.setText(mContext.getResources().getString(R.string.loading));
        reload.setOnClickListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout)contentFragment.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setVisibility(View.GONE);
        content = (RichText)contentFragment.findViewById(R.id.content);
        tags = (TagGroup)contentFragment.findViewById(R.id.tags);
        tags.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                ProblemContentFragment.this.onTagClick(tag);
            }
        });
        originalProblem = (TextView)contentFragment.findViewById(R.id.original_problem);
        originalProblem.setOnClickListener(this);
        similarProblems = (TagGroup)contentFragment.findViewById(R.id.similar_problem);
        similarProblems.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String similarProblem) {
                onSimilarProblemClick(similarProblem);
            }
        });

        snackbarLayout = (CoordinatorLayout)contentFragment.findViewById(R.id.container);

        setListener();

        return contentFragment;
    }

    public void setContent() {
        reload.setText(mContext.getResources().getString(R.string.reload));  // for refreshing
        reloadLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        content.setRichText(LeetCoderUtil.getReadyContent(activity.problem.getContent()));
        tags.setTags(activity.problem_index.getTags());
        if (activity.problem.getSimilarProblems() == null || activity.problem.getSimilarProblems().size() == 0) {
            similarProblems.setVisibility(View.GONE);
        } else {
            similarProblems.setTags(activity.problem.getSimilarProblems());
        }

        if (isRefreshing) {
            isRefreshing = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setLoading() {
        if (isRefreshing) {
            return;
        }

        reloadLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reload.setText(mContext.getResources().getString(R.string.loading));
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    public void setReload() {
        if (isRefreshing) {
            return;
        }

        reloadLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        reload.setText(mContext.getResources().getString(R.string.reload));
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    private void setListener() {
        content.setOnImageClickListener(new RichText.OnImageClickListener() {
            @Override
            public void imageClicked(List<String> imageUrls, int position) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", imageUrls.get(position));
            }
        });

        content.setOnURLClickListener(new RichText.OnURLClickListener() {
            @Override
            public boolean urlClicked(String url) {
                url = "https://leetcode.com" + url;
                new FinestWebView.Builder(activity)
                        .statusBarColorRes(R.color.colorPrimary)
                        .iconDefaultColorRes(R.color.white)
                        .iconDisabledColorRes(R.color.white)
                        .iconPressedColorRes(R.color.white)
                        .swipeRefreshColorRes(R.color.colorPrimary)
                        .titleColorRes(R.color.white)
                        .urlColorRes(R.color.white)
                        .progressBarColorRes(R.color.white)
                        .menuTextColorRes(R.color.colorPrimary)
                        .stringResRefresh(R.string.refresh)
                        .stringResShareVia(R.string.share)
                        .stringResCopyLink(R.string.copy_link)
                        .stringResOpenWith(R.string.open_with)
                        .stringResCopiedToClipboard(R.string.copy_link_toast)
                        .show(url);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload:
                if (reload.getText().toString().equals(mContext.getResources().getString(R.string.loading))) return;
                reload.setText(mContext.getResources().getString(R.string.loading));
                activity.reload();
                break;
            case R.id.original_problem:
                new FinestWebView.Builder(activity)
                        .statusBarColorRes(R.color.colorPrimary)
                        .iconDefaultColorRes(R.color.white)
                        .iconDisabledColorRes(R.color.white)
                        .iconPressedColorRes(R.color.white)
                        .swipeRefreshColorRes(R.color.colorPrimary)
                        .titleColorRes(R.color.white)
                        .urlColorRes(R.color.white)
                        .progressBarColorRes(R.color.white)
                        .menuTextColorRes(R.color.colorPrimary)
                        .stringResRefresh(R.string.refresh)
                        .stringResShareVia(R.string.share)
                        .stringResCopyLink(R.string.copy_link)
                        .stringResOpenWith(R.string.open_with)
                        .stringResCopiedToClipboard(R.string.copy_link_toast)
                        .show(activity.problem.getProblemLink());
                break;
        }
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        onClick(reload);
    }

    private void onTagClick(String tag) {
        onTagClickListener.onTagClick(tag);
    }

    private void onSimilarProblemClick(final String similarProblem) {
        onSimilarProblemClickListener.onSimilarProblemClick(similarProblem);
    }

    public interface OnTagClickListener {
        void onTagClick(String tag);
    }

    public interface OnSimilarProblemClickListener {
        void onSimilarProblemClick(String similarProblem);
    }

    public interface ReloadListener {
        void reload();
    }
}
