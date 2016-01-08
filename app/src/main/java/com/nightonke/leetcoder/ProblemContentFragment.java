package com.nightonke.leetcoder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Weiping on 2016/1/8.
 */

public class ProblemContentFragment extends Fragment {

    private ProblemActivity activity;

    private LinearLayout content;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ProblemActivity){
            activity = (ProblemActivity)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentFragment = inflater.inflate(R.layout.fragment_problem_content, container, false);

        content = (LinearLayout)contentFragment.findViewById(R.id.content);
        setContent();

        return contentFragment;
    }

    // <p>\n<img src="http://xxx.png" /><br />\n
    private void setContent() {
        Problem problem = activity.problem;
        ArrayList<String> contents = new ArrayList<>();
        String contentString = problem.getContent();
        int st = 0;
        int ed = 0;
        while (true) {
            ed = contentString.indexOf("<p>\\n<img src=\"", st);
            if (ed == -1) break;

            // [st, ed) is text
            String textString = contentString.substring(st, ed);

            int edOfSrc = contentString.indexOf("\" /><br />\\n");
            // [ed + 14, edOfSrc) is image
            String imageString = contentString.substring(ed + 14, edOfSrc);

            // remove the imageString from the contentString
            contentString = contentString.substring(st, ed) + contentString.substring(edOfSrc + 11);

            // add the text and image
            TextView textView = new TextView(activity);
            textView.setText(Html.fromHtml(textString));
            ImageView imageView = new ImageView(activity);
            Picasso.with(activity).load(imageString).into(imageView);
            content.addView(textView);
            content.addView(imageView);

            // set the position for next
            st = edOfSrc;
        }
        TextView textView = new TextView(activity);
        textView.setText(Html.fromHtml(contentString.substring(st)));
        content.addView(textView);
    }

}
