package com.nightonke.leetcoder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Weiping on 2016/1/8.
 */
public class ProblemCommentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View me = inflater.inflate(R.layout.fragment_problem_comment, container, false);

        return me;
    }

}
