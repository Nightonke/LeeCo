package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thefinestartist.finestwebview.FinestWebView;

import java.util.List;

/**
 * Created by Weiping on 2016/1/8.
 */

public class ProblemContentFragment extends Fragment {

    private ProblemActivity activity;
    private Context mContext;

    private RichText content;

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

        content = (RichText)contentFragment.findViewById(R.id.content);

        setListener();
        setContent();

        return contentFragment;
    }

    private void setContent() {
        Problem problem = activity.problem;
        content.setRichText(problem.getContent());
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
}
