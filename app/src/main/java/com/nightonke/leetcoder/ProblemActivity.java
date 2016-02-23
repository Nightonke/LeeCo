package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class ProblemActivity extends AppCompatActivity implements ProblemContentFragment.ReloadListener {

    public ProblemTest problemTest;
    public Problem_Index problem_index;
    public Problem problem;

    private Context mContext;

    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;
    private FragmentPagerItemAdapter adapter;

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        mContext = this;

        problemTest = new ProblemTest();
        problemTest.createTestProblem();

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("", ProblemContentFragment.class)
                .add("", ProblemSolutionFragment.class)
                .add("", ProblemDiscussFragment.class)
                .add("", ProblemCommentFragment.class)
                .create());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);

        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        setupTab();

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        title = (TextView)findViewById(R.id.title);
        title.setText("Sudoku Solver");

        getData();
    }

    private void getData() {
        // test problem 37
        getTest();
    }

    private void getTest() {
        problem_index = new Problem_Index();
        problem_index.setId(37);
        problem_index.setTitle("Sudoku Solver");
        problem_index.setLevel("Hard");
        problem_index.setLike(12);
        problem_index.setSummary("Write a program to solve a Sudoku puzzle by filling the empty cells.");
        List<String> tags = new ArrayList<>();
        tags.add("Backtracking");
        tags.add("Hash Table");
        problem_index.setTags(tags);

        problem = new Problem();
        problem.setId(problem_index.getId());

        Toast.makeText(mContext, "Loading...", Toast.LENGTH_SHORT).show();

        // set loading
        Fragment contentFragment = adapter.getPage(0);
        if (contentFragment != null) {
            if (contentFragment instanceof ProblemContentFragment) {
                ((ProblemContentFragment) contentFragment).setLoading();
            }
        }

        Fragment solutionFragment = adapter.getPage(1);
        if (solutionFragment != null) {
            if (solutionFragment instanceof ProblemSolutionFragment) {
                ((ProblemSolutionFragment) solutionFragment).setLoading();
            }
        }

        BmobQuery<Problem> query = new BmobQuery<>();
        query.addWhereEqualTo("id", problem.getId());
        query.setLimit(1);
        query.findObjects(mContext, new FindListener<Problem>() {
            @Override
            public void onSuccess(List<Problem> list) {
                Toast.makeText(mContext, "Query successfully", Toast.LENGTH_SHORT).show();
                problem.setContent(list.get(0).getContent());
                problem.setSolution(list.get(0).getSolution());
                problem.setDiscussLink(list.get(0).getDiscussLink());
                problem.setProblemLink(list.get(0).getProblemLink());
                problem.setSimilarProblems(list.get(0).getSimilarProblems());
                problem.show();

                Fragment contentFragment = adapter.getPage(0);

                if (contentFragment != null) {
                    if (contentFragment instanceof ProblemContentFragment) {
                        ((ProblemContentFragment) contentFragment).setContent();
                    }
                }

                Fragment solutionFragment = adapter.getPage(1);

                if (solutionFragment != null) {
                    if (solutionFragment instanceof ProblemSolutionFragment) {
                        ((ProblemSolutionFragment) solutionFragment).setCode();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(mContext, "Query failed " + s, Toast.LENGTH_SHORT).show();

                Fragment contentFragment = adapter.getPage(0);
                if (contentFragment != null) {
                    if (contentFragment instanceof ProblemContentFragment) {
                        ((ProblemContentFragment) contentFragment).setReload();
                    }
                }

                Fragment solutionFragment = adapter.getPage(1);
                if (solutionFragment != null) {
                    if (solutionFragment instanceof ProblemSolutionFragment) {
                        ((ProblemSolutionFragment) solutionFragment).setReload();
                    }
                }
            }
        });
    }

    private void setupTab() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);

        viewPagerTab.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) inflater.inflate(R.layout.tab_icon, container, false);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.content_icon));
                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.solution_icon));
                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.discuss_icon));
                        break;
                    case 3:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.comment_icon));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return icon;
            }
        });
    }

    @Override
    public void reload() {
        getTest();
    }
}
