package com.nightonke.leetcoder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


public class ProblemActivity extends AppCompatActivity {

    public Problem problem;

    private Context mContext;

    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        mContext = this;

        problem = new Problem();
        problem.createTestProblem();

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("", ProblemContentFragment.class)
                .add("", ProblemSolutionFragment.class)
                .add("", ProblemDiscussFragment.class)
                .add("", ProblemCommentFragment.class)
                .create());

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        setupTab();

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
    }

    private void setupTab() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);

        viewPagerTab.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) inflater.inflate(R.layout.tab_icon, container, false);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flash));
                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flash));
                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flash));
                        break;
                    case 3:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flash));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return icon;
            }
        });
    }
}
