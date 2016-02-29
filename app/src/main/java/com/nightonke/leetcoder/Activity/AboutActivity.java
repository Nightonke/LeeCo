package com.nightonke.leetcoder.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.ppamorim.cult.CultView;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.R;
import com.thefinestartist.finestwebview.FinestWebView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private CultView cultView;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext, R.color.colorPrimary);

        cultView = (CultView) findViewById(R.id.cult_view);
        ((AppCompatActivity) mContext).setSupportActionBar(cultView.getInnerToolbar());
        cultView.getInnerToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        cultView.getOutToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        (LeetCoderUtil.getActionBarTextView(cultView.getInnerToolbar())).setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        ActionBar actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mContext.getResources().getString(R.string.about_activity_title));
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(mContext, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }

        findViewById(R.id.app_layout).setOnClickListener(this);
        findViewById(R.id.expandableLayout).setOnClickListener(this);
        findViewById(R.id.developer_layout).setOnClickListener(this);
        findViewById(R.id.open_source_layout).setOnClickListener(this);
        findViewById(R.id.blog_layout).setOnClickListener(this);
        findViewById(R.id.email_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_layout:
            case R.id.expandableLayout:
                ((ExpandableRelativeLayout)findViewById(R.id.expandableLayout)).toggle();
                break;
            case R.id.developer_layout:
                openUrl("https://github.com/Nightonke");
                break;
            case R.id.open_source_layout:
                openUrl("https://github.com/Nightonke/LeetCoder2");
                break;
            case R.id.blog_layout:
                openUrl("http://blog.csdn.net/u012925008");
                break;
            case R.id.email_layout:
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Nightonke@outlook.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                mContext.startActivity(Intent.createChooser(emailIntent, mContext.getResources().getString(R.string.about_email_me)));
                break;
        }
    }

    private void openUrl(String url) {
        new FinestWebView.Builder(this)
                .statusBarColorRes(R.color.colorPrimary)
                .iconDefaultColorRes(R.color.white)
                .iconDisabledColorRes(R.color.white)
                .iconPressedColorRes(R.color.white)
                .swipeRefreshColorRes(R.color.colorPrimary)
                .titleColorRes(R.color.white)
                .urlColorRes(R.color.white)
                .progressBarColorRes(R.color.white)
                .menuTextColorRes(R.color.colorPrimary)
                .stringResRefresh(R.string.refresh)
                .stringResShareVia(R.string.share)
                .stringResCopyLink(R.string.copy_link)
                .stringResOpenWith(R.string.open_with)
                .stringResCopiedToClipboard(R.string.copy_link_toast)
                .show(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}