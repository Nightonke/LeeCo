package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thefinestartist.finestwebview.FinestWebView;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;

/**
 * Created by Weiping on 2016/1/8.
 */

public class ProblemContentFragment extends Fragment implements View.OnClickListener {

    private ProblemActivity activity;
    private Context mContext;

    private RelativeLayout reloadLayout;
    private TextView reload;

    private ScrollView scrollView;
    private RichText content;
    private TagGroup tags;
    private TextView originalProblem;
    private TagGroup similarProblems;

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
        View contentFragment = inflater.inflate(R.layout.fragment_problem_content, container, false);

        reloadLayout = (RelativeLayout)contentFragment.findViewById(R.id.loading_layout);
        reloadLayout.setVisibility(View.VISIBLE);
        reload = (TextView)contentFragment.findViewById(R.id.reload);
        reload.setText(mContext.getResources().getString(R.string.loading));
        reload.setOnClickListener(this);

        scrollView = (ScrollView)contentFragment.findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        content = (RichText)contentFragment.findViewById(R.id.content);
        tags = (TagGroup)contentFragment.findViewById(R.id.tags);
        originalProblem = (TextView)contentFragment.findViewById(R.id.original_problem);
        originalProblem.setOnClickListener(this);
        similarProblems = (TagGroup)contentFragment.findViewById(R.id.similar_problem);

        setListener();

        return contentFragment;
    }

    public void setContent() {
        reloadLayout.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);

        content.setRichText(activity.problem.getContent());
        tags.setTags(activity.problem_index.getTags());
        similarProblems.setTags(activity.problem.getSimilarProblems());
    }

    public void setReload() {
        reloadLayout.setVisibility(View.VISIBLE);
        reload.setText(mContext.getResources().getString(R.string.reload));
        scrollView.setVisibility(View.GONE);
    }

    private void setListener() {
        content.setOnImageClickListener(new RichText.OnImageClickListener() {
            @Override
            public void imageClicked(List<String> imageUrls, int position) {
                Toast.makeText(mContext, imageUrls.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        content.setOnURLClickListener(new RichText.OnURLClickListener() {
            @Override
            public boolean urlClicked(String url) {
                url = "https://leetcode.com" + url;
                Toast.makeText(mContext, url, Toast.LENGTH_SHORT).show();
                new FinestWebView.Builder(activity).show(url);
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
                try {
                    ((ReloadListener)activity)
                            .reload();
                } catch (ClassCastException cce){
                    cce.printStackTrace();
                }
                break;
            case R.id.original_problem:
                new FinestWebView.Builder(activity).show(activity.problem.getProblemLink());
                break;
        }
    }

    public interface ReloadListener {
        void reload();
    }
}
