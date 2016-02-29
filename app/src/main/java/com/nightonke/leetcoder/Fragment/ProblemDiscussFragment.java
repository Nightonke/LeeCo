package com.nightonke.leetcoder.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.nightonke.leetcoder.Activity.ProblemActivity;
import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.Model.Discuss;
import com.nightonke.leetcoder.Model.Problem;
import com.nightonke.leetcoder.Adapter.ProblemDiscussAdapter;
import com.nightonke.leetcoder.R;
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
        ProblemDiscussAdapter.OnItemClickListener {

    public final int NOTIFICAION_ADAPTER = 1;

    public static final int SORT_BY_TIME = 0;
    public static final int SORT_BY_HOT = 1;
    public static final int SORT_BY_VOTE = 2;
    public static final int SORT_BY_ANSWER = 3;
    public static final int SORT_BY_VIEW = 4;

    public static final String[] SORT_STRINGS = new String[]{"?sort=recent", "?sort=hot", "?sort=votes", "?sort=answers", "?sort=views"};

    public static int sortType = SORT_BY_VOTE;

    private boolean end = false;

    private SuperRecyclerView superRecyclerView;
    private ProblemDiscussAdapter adapter;
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
        superRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        superRecyclerView.setRefreshListener(this);
//        superRecyclerView.setupMoreListener(this, Integer.MAX_VALUE);
        superRecyclerView.getMoreProgressView().setDrawingCacheBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        adapter = new ProblemDiscussAdapter(discusses, this);
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

    public void sort(int newSortType) {
        discusses = new ArrayList<>();
        sortType = newSortType;
        setDiscuss();
    }

    public void setDiscuss() {
        Problem problem = activity.problem;
        if (!SORT_STRINGS[sortType].equals(problem.getDiscussLink().substring(problem.getDiscussLink().length() - SORT_STRINGS[sortType].length(), problem.getDiscussLink().length()))) {
            discuss.loadUrl(problem.getDiscussLink() + SORT_STRINGS[sortType] + "&start=" + discusses.size());
        } else {
            discuss.loadUrl(problem.getDiscussLink() + "&start=" + discusses.size());
        }

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
        if (BuildConfig.DEBUG) {
            Log.d("LeetCoder", "onMoreAsked start from: " + discusses.size() + ", end: " + end);
        }
        if (end) {
            superRecyclerView.hideMoreProgress();
        } else {
            Problem problem = activity.problem;
            if (!SORT_STRINGS[sortType].equals(problem.getDiscussLink().substring(problem.getDiscussLink().length() - SORT_STRINGS[sortType].length(), problem.getDiscussLink().length()))) {
                Log.d("LeetCoder", problem.getDiscussLink() + SORT_STRINGS[sortType] + "&start=" + discusses.size());
                discuss.loadUrl(problem.getDiscussLink() + SORT_STRINGS[sortType] + "&start=" + discusses.size());
            } else {
                Log.d("LeetCoder", problem.getDiscussLink() + "&start=" + discusses.size());
                discuss.loadUrl(problem.getDiscussLink() + "&start=" + discusses.size());
            }
        }
    }

    @Override
    public void onItemClick(int position) {
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
                .show(discusses.get(position).getUrl());
    }

    class JavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            getDiscusses(html);
            Message msg = new Message();
            msg.what = NOTIFICAION_ADAPTER;
            handler.sendMessage(msg);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NOTIFICAION_ADAPTER) {
                adapter = new ProblemDiscussAdapter(discusses, ProblemDiscussFragment.this);
                superRecyclerView.setAdapter(adapter);
                if (superRecyclerView != null) superRecyclerView.hideMoreProgress();
                end = false;
            }
        }
    };

    private final String VOTE_START_STRING = "<span class=\"qa-netvote-count-data\">";
    private final String VOTE_END_STRING = "</span>";
    private final String ANSWER_START_STRING = "<span class=\"qa-a-count-data\">";
    private final String ANSWER_END_STRING = "</span>";
    private final String VIEW_START_STRING = "<span class=\"qa-view-count-data\">";
    private final String VIEW_END_STRING = "</span>";
    private final String TITLE_START_STRING = "<div class=\"qa-q-item-title\">";
    private final String TITLE_END_STRING = "</span></a>";
    private final String DATE_START_STRING = "<span class=\"qa-q-item-when-data\">";
    private final String DATE_END_STRING = "</span>";
    private final String ASKER_START_STRING = "class=\"qa-user-link\">";
    private final String ASKER_END_STRING = "</a></span>";
    private final String URL_START_STRING = "<a href=\"../../";
    private final String URL_END_STRING = "\">";
    private void getDiscusses(String html) {
        if ("<head><head></head><body></body></head>".equals(html)) {
            end = true;
            return;
        }

        int position = 0;
        int count = 0;
        while (position != -1 && count < 20) {
            Discuss discuss = new Discuss();
            position = html.indexOf(VOTE_START_STRING, position + 1);

            if(position != -1){
                count++;

                int endPosition = html.indexOf(VOTE_END_STRING, position);
                discuss.setVote(html.substring(position + VOTE_START_STRING.length(), endPosition));

                position = html.indexOf(ANSWER_START_STRING, position + 1);
                endPosition = html.indexOf(ANSWER_END_STRING, position);
                discuss.setAnswer(html.substring(position + ANSWER_START_STRING.length(), endPosition));

                position = html.indexOf(VIEW_START_STRING, position + 1);
                endPosition = html.indexOf(VIEW_END_STRING, position);
                discuss.setView(html.substring(position + VIEW_START_STRING.length(), endPosition));

                position = html.indexOf(TITLE_START_STRING, position + 1);

                int urlStartPosition = html.indexOf(URL_START_STRING, position + 1) + URL_START_STRING.length();
                int urlEndPostion = html.indexOf(URL_END_STRING, urlStartPosition);
                discuss.setUrl("https://leetcode.com/discuss/" + html.substring(urlStartPosition, urlEndPostion));

                endPosition = html.indexOf(TITLE_END_STRING, position);
                int offset = endPosition - 1;
                while (!"\">".equals(html.substring(offset, offset + 2))) offset--;
                discuss.setTitle(html.substring(offset + 2, endPosition));

//                position = html.indexOf(URL_START_STRING, position + 1);
//                endPosition = html.indexOf(URL_END_STRING, position);
//                discuss.setUrl("https://leetcode.com/discuss/" + html.substring(position + URL_START_STRING.length(), endPosition));

                position = html.indexOf(DATE_START_STRING, position + 1);
                endPosition = html.indexOf(DATE_END_STRING, position);
                discuss.setDate(html.substring(position + DATE_START_STRING.length(), endPosition));

                position = html.indexOf(ASKER_START_STRING, position + 1);
                endPosition = html.indexOf(ASKER_END_STRING, position);
                discuss.setAsker(html.substring(position + ASKER_START_STRING.length(), endPosition));

                discusses.add(discuss);
            }
        }

        if (count < 20) end = true;
    }
}
