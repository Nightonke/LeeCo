package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Weiping on 2016/1/8.
 */

public class ProblemCommentFragment extends Fragment
        implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        ProblemCommentAdapter.OnCardViewClickListener,
        ProblemCommentAdapter.OnContentLongClickListener,
        ProblemCommentAdapter.OnTargetClickListener,
        ProblemCommentAdapter.OnReplyClickListener,
        ProblemCommentAdapter.OnLikeClickListener {

    private SuperRecyclerView superRecyclerView;
    private ProblemCommentAdapter adapter;
    private List<Comment> comments = new ArrayList<>();

    private Boolean isRefreshing = false;
    private RelativeLayout reloadLayout;
    private ProgressBar progressBar;
    private TextView reload;

    private ProblemActivity activity;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);

        if (context instanceof ProblemActivity){
            activity = (ProblemActivity)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View commentFragment = inflater.inflate(R.layout.fragment_problem_comment, container, false);

        reloadLayout = (RelativeLayout)commentFragment.findViewById(R.id.loading_layout);
        reloadLayout.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar)commentFragment.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        reload = (TextView)commentFragment.findViewById(R.id.reload);
        reload.setText(mContext.getResources().getString(R.string.loading));
        reload.setOnClickListener(this);

        superRecyclerView = (SuperRecyclerView) commentFragment.findViewById(R.id.recyclerview);
        superRecyclerView.getSwipeToRefresh().setColorSchemeResources(R.color.colorPrimary);
        superRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        superRecyclerView.setRefreshListener(this);
        superRecyclerView.getMoreProgressView().setDrawingCacheBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        adapter = new ProblemCommentAdapter(comments, this, this, this, this, this);
        superRecyclerView.setAdapter(adapter);
        superRecyclerView.setVisibility(View.INVISIBLE);

        return commentFragment;
    }

    public void setComment() {

        setLoading();

        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.addWhereEqualTo("id", activity.problem.getId());
        query.setLimit(Integer.MAX_VALUE);
        query.findObjects(LeetCoderApplication.getAppContext(), new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> object) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Get comments: " + object.size());
                reload.setText(mContext.getResources().getString(R.string.reload));  // for refreshing
                reloadLayout.setVisibility(View.GONE);

                comments = object;
                adapter = new ProblemCommentAdapter(comments, ProblemCommentFragment.this, ProblemCommentFragment.this, ProblemCommentFragment.this, ProblemCommentFragment.this, ProblemCommentFragment.this);
                superRecyclerView.setAdapter(adapter);
                superRecyclerView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onError(int code, String msg) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Get comments failed: " + msg);
                setReload();
            }
        });

        if (isRefreshing) {
            isRefreshing = false;
//            superRecyclerView.setRefreshing(false);
        }
    }

    public void setLoading() {
        if (isRefreshing) {
            return;
        }

        reloadLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reload.setText(mContext.getResources().getString(R.string.loading));
    }

    public void setReload() {
        if (isRefreshing) {
            return;
        }

        reloadLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        reload.setText(mContext.getResources().getString(R.string.reload));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload:
                if (reload.getText().toString().equals(mContext.getResources().getString(R.string.loading))) return;
                reload.setText(mContext.getResources().getString(R.string.loading));
                activity.reload();
                break;
        }
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        onClick(reload);
    }

    @Override
    public void onCardViewClick(int position) {

    }

    @Override
    public void onContentLongClick(int position) {

    }

    @Override
    public void onLikeClick(int position) {

    }

    @Override
    public void onReplyClick(int position) {

    }

    @Override
    public void onTargetClick(String objectId) {

    }
}
