package com.nightonke.leetcoder;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Weiping on 2016/1/8.
 */
public class ProblemSolutionFragment extends Fragment {

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

        code = (CodeView)solutionFragment.findViewById(R.id.code);
        setCode();
        code.getSettings().setLoadWithOverviewMode(true);
        code.getSettings().setUseWideViewPort(true);

        return solutionFragment;
    }

    private void setCode() {
        Problem problem = activity.problem;
        copyCode(problem.getMySolution());

        File dir = new File(mContext.getFilesDir() + "/" + "code");

        if (dir != null) {
            code.setDirSource(dir);
        }
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

}
