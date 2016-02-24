package com.nightonke.leetcoder;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.ppamorim.cult.CultView;
import com.github.ppamorim.cult.util.ViewUtil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;

public class MainActivity extends AppCompatActivity
        implements
        View.OnClickListener {

    private Context mContext;

    private CultView cultView;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentPagerItemAdapter adapter;
    private DrawerLayout drawerLayout;
    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;

    private View searchLayout;
    private EditText searchInput;
    private ImageView searchCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext);

        cultView = (CultView)findViewById(R.id.cult_view);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_left);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        smartTabLayout = (SmartTabLayout)findViewById(R.id.smart_tab_layout);

        searchLayout = View.inflate(mContext, R.layout.layout_search, null);
        searchInput = (EditText)searchLayout.findViewById(R.id.search_edit_text);
        searchCancel = (ImageView)searchLayout.findViewById(R.id.cancel);
        searchCancel.setOnClickListener(this);
        cultView.setOutToolbarLayout(searchLayout);

        cultView.setOutContentLayout(R.layout.fragment_search_result);

        ((AppCompatActivity)mContext).setSupportActionBar(cultView.getInnerToolbar());
        ActionBar actionBar = ((AppCompatActivity)mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mContext.getResources().getString(R.string.app_name));
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this ,drawerLayout, R.string.ok, R.string.cancel);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item);
            case R.id.action_search:
                cultView.showSlide();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (cultView.isSecondViewAdded()) {
            cultView.hideSlideTop();
            return;
        }
        super.onBackPressed();
    }

    private void hideKeyboard() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (getCurrentFocus() != null) {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                hideKeyboard();
                onBackPressed();
                break;
        }
    }
}
