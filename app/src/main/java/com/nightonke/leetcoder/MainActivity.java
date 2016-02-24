package com.nightonke.leetcoder;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ppamorim.cult.CultView;
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
    private ImageView searchErase;
    private boolean searchEraseShouldShow = true;

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

        searchLayout = View.inflate(mContext, R.layout.fragment_search, null);
        searchInput = (EditText)searchLayout.findViewById(R.id.search_edit_text);
        searchInput.getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("".equals(searchInput.getText().toString())) {
                    YoYo.with(Techniques.FadeOutUp)
                            .duration(300)
                            .playOn(searchErase);
                    RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams)searchInput.getLayoutParams();
                    mLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.cancel);
                    mLayoutParams.addRule(RelativeLayout.START_OF, R.id.cancel);
                    searchInput.setLayoutParams(mLayoutParams);
                    searchEraseShouldShow = true;
                } else if (searchEraseShouldShow) {
                    searchEraseShouldShow = false;
                    searchErase.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceInDown)
                            .duration(500)
                            .playOn(searchErase);
                    RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams)searchInput.getLayoutParams();
                    mLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.erase);
                    mLayoutParams.addRule(RelativeLayout.START_OF, R.id.erase);
                    searchInput.setLayoutParams(mLayoutParams);
                }
            }
        });
        searchCancel = (ImageView)searchLayout.findViewById(R.id.cancel);
        searchCancel.setOnClickListener(this);
        searchErase = (ImageView)searchLayout.findViewById(R.id.erase);
        searchErase.setOnClickListener(this);
        searchErase.setVisibility(View.INVISIBLE);
        cultView.setOutToolbarLayout(searchLayout);

        cultView.setOutContentLayout(R.layout.fragment_search_result);

        ((AppCompatActivity)mContext).setSupportActionBar(cultView.getInnerToolbar());
        cultView.getInnerToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        cultView.getOutToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        (LeetCoderUtil.getActionBarTextView(cultView.getInnerToolbar())).setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
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
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
//        drawable = menu.findItem(android.R.id.home).getIcon();
//        if (drawable != null) {
//            drawable.mutate();
//            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item);
            case R.id.action_search:
                cultView.showSlide();
                showKeyboard();
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

    private void showKeyboard() {
        searchInput.requestFocus();
        searchInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(searchInput, 0);
            }
        },200);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                hideKeyboard();
                onBackPressed();
                break;
            case R.id.erase:
                searchInput.setText("");
                break;
        }
    }
}
