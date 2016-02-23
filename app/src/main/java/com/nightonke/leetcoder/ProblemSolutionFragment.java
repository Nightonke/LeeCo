package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Weiping on 2016/1/8.
 */

public class ProblemSolutionFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout reloadLayout;
    private ProgressBar progressBar;
    private TextView reload;

    private CodeView code;

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
        View solutionFragment = inflater.inflate(R.layout.fragment_problem_solution, container, false);

        reloadLayout = (RelativeLayout)solutionFragment.findViewById(R.id.loading_layout);
        reloadLayout.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar)solutionFragment.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        reload = (TextView)solutionFragment.findViewById(R.id.reload);
        reload.setText(mContext.getResources().getString(R.string.loading));
        reload.setOnClickListener(this);

        code = (CodeView)solutionFragment.findViewById(R.id.code);
        code.getSettings().setLoadWithOverviewMode(true);
        code.getSettings().setUseWideViewPort(true);
        code.setVisibility(View.GONE);

        return solutionFragment;
    }

    public void setCode() {
        Problem problem = activity.problem;
        copyCode(problem.getSolution());

        File dir = new File(mContext.getFilesDir() + "/" + "code");

        if (dir != null) {
            code.setDirSource(dir);
        }
        code.getSettings().setLoadWithOverviewMode(true);
        code.getSettings().setUseWideViewPort(true);
        code.setVisibility(View.VISIBLE);
    }

    private void copyCode(String codeString) {
        try {
            FileOutputStream fos = mContext.openFileOutput("code", Context.MODE_PRIVATE);
            FileOutputStream fileOutputStream
                    = mContext.openFileOutput("code", Context.MODE_PRIVATE);
            fileOutputStream.write(codeString.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLoading() {
        reloadLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reload.setText(mContext.getResources().getString(R.string.loading));
    }

    public void setReload() {
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
                try {
                    ((ReloadListener)activity)
                            .reload();
                } catch (ClassCastException cce){
                    cce.printStackTrace();
                }
                break;
        }
    }

    public interface ReloadListener {
        void reload();
    }
}
