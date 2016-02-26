package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Weiping on 2016/1/8.
 */

public class ProblemCommentFragment extends Fragment
        implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

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

        return commentFragment;
    }

    public void setComment() {

        reload.setText(mContext.getResources().getString(R.string.reload));  // for refreshing
        reloadLayout.setVisibility(View.GONE);

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
}
