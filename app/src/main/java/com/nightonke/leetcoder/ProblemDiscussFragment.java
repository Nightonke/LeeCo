package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;

/**
 * Created by Weiping on 2016/1/8.
 */
public class ProblemDiscussFragment extends Fragment
        implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        OnMoreListener,
        ProblemDiscussFragmentAdapter.OnItemClickListener {

    public static final int SORT_BY_TIME = 0;
    public static final int SORT_BY_HOT = 1;
    public static final int SORT_BY_VOTE = 2;
    public static final int SORT_BY_ANSWER = 3;
    public static final int SORT_BY_VIEW = 4;

    public static final String SORT_BY_TIME_STRING = "";
    public static final String SORT_BY_HOT_STRING = "?sort=hot";
    public static final String SORT_BY_VOTE_STRING = "?sort=votes";
    public static final String SORT_BY_ANSWER_STRING = "?sort=answers";
    public static final String SORT_BY_VIEW_STRING = "?sort=views";

    public static int sortType = SORT_BY_VOTE;
    public static String sortString = SORT_BY_VOTE_STRING;

    private SuperRecyclerView superRecyclerView;
    private ProblemDiscussFragmentAdapter adapter;
    private ArrayList<Discuss> discusses = new ArrayList<>();
    private Boolean isRefreshing = false;
    private RelativeLayout reloadLayout;
    private ProgressBar progressBar;
    private TextView reload;

    private WebView discuss;

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
        View discussFragment = inflater.inflate(R.layout.fragment_problem_discuss, container, false);

        reloadLayout = (RelativeLayout)discussFragment.findViewById(R.id.loading_layout);
        reloadLayout.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar)discussFragment.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        reload = (TextView)discussFragment.findViewById(R.id.reload);
        reload.setText(mContext.getResources().getString(R.string.loading));
        reload.setOnClickListener(this);

        superRecyclerView = (SuperRecyclerView) discussFragment.findViewById(R.id.recyclerview);
        superRecyclerView.getSwipeToRefresh().setColorSchemeResources(R.color.colorPrimary);
        superRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        superRecyclerView.setRefreshListener(this);
        superRecyclerView.setupMoreListener(this, Integer.MAX_VALUE);
        adapter = new ProblemDiscussFragmentAdapter(discusses, this);
        superRecyclerView.setAdapter(adapter);
        superRecyclerView.setVisibility(View.INVISIBLE);

        discuss = (WebView) discussFragment.findViewById(R.id.discuss);
        discuss.getSettings().setLoadWithOverviewMode(true);
        discuss.getSettings().setUseWideViewPort(true);
        discuss.getSettings().setJavaScriptEnabled(true);
        discuss.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");
        discuss.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                discuss.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });

        return discussFragment;
    }

    public void setDiscuss() {
        Problem problem = activity.problem;
        if (BuildConfig.DEBUG) {
            Log.d("LeetCoder", problem.getDiscussLink());
        }
        if (!sortString.equals(problem.getDiscussLink().substring(problem.getDiscussLink().length() - sortString.length(), problem.getDiscussLink().length()))) {
            problem.setDiscussLink(problem.getDiscussLink() + sortString);
        }
        discuss.loadUrl(problem.getDiscussLink());
        discuss.getSettings().setLoadWithOverviewMode(true);
        discuss.getSettings().setUseWideViewPort(true);
        discuss.getSettings().setJavaScriptEnabled(true);
        discuss.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");
        discuss.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                reload.setText(mContext.getResources().getString(R.string.reload));  // for refreshing
                reloadLayout.setVisibility(View.GONE);
                superRecyclerView.setVisibility(View.VISIBLE);
                discuss.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });

        if (isRefreshing) {
            isRefreshing = false;
            superRecyclerView.setRefreshing(false);
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
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

    }

    @Override
    public void onItemClick(int position) {
        new FinestWebView.Builder(activity).show(discusses.get(position).getUrl());
    }

    class JavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            getDiscusses(html);
            adapter.notifyDataSetChanged();
            if (BuildConfig.DEBUG) {
                Log.d("LeetCoder", html);
            }
        }
    }

    private void getDiscusses(String html) {
        discusses = new ArrayList<>();
        adapter.notifyDataSetChanged();
    }
}
