package com.nightonke.leetcoder.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ppamorim.cult.CultView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.nightonke.leetcoder.Model.Vote;
import com.nightonke.leetcoder.Utils.AppUpdateManager;
import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.Fragment.CategoryFragment;
import com.nightonke.leetcoder.Adapter.CategoryProblemIndexAdapter;
import com.nightonke.leetcoder.Utils.LeetCoderApplication;
import com.nightonke.leetcoder.UI.LeetCoderGridView;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.Adapter.ProblemSearchResultAdapter;
import com.nightonke.leetcoder.Model.Problem_Index;
import com.nightonke.leetcoder.R;
import com.nightonke.leetcoder.Adapter.TagGridViewAdapter;
import com.nightonke.leetcoder.Model.User;
import com.nineoldandroids.animation.Animator;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        CategoryFragment.OnRefreshListener,
        ProblemSearchResultAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        CategoryProblemIndexAdapter.OnItemLongClickListener,
        CategoryFragment.OnJumpListener {

    public static final int START_PROBLEM = 1;
    public static final int START_LIKES = 2;
    public static final int BACK_CATEGORY = 3;
    public static final int START_SETTINGS = 4;
    public static final int BACK_USER_NAME_CHANGED = 5;

    private Context mContext;

    private CultView cultView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawerLayout;

    private RelativeLayout reloadLayout;
    private ProgressBar progressBar;
    private TextView reload;

    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;
    private FragmentPagerItemAdapter adapter;

    private View searchLayout;
    private EditText searchInput;
    private ImageView searchCancel;
    private ImageView searchErase;
    private boolean searchEraseShouldShow = true;

    private View searchResultLayout;
    private SwipeRefreshLayout searchResultRefreshLayout;
    private SuperRecyclerView searchRecyclerView;
    private ProblemSearchResultAdapter searchResultAdapter;
    private ArrayList<Problem_Index> searchResult = null;

    private LinearLayout userLayout;
    private TextView userName;
    private TextView votes;

    private LinearLayout likeDivider;
    private LinearLayout likeLayout;
    private TextView likes;

    private LinearLayout tagDivider;
    private LeetCoderGridView gridView;
    private TagGridViewAdapter tagAdapter;
    private TextView tags;

    private LinearLayout settings;
    private LinearLayout help;
    private LinearLayout feedback;
    private LinearLayout about;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext, R.color.colorPrimary);

        cultView = (CultView)findViewById(R.id.cult_view);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_left);

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
                search(searchInput.getText().toString());
                if ("".equals(searchInput.getText().toString())) {
                    YoYo.with(Techniques.FadeOutUp)
                            .duration(300)
                            .withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    searchErase.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
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

        searchResultLayout = View.inflate(mContext, R.layout.fragment_search_result, null);
        searchResultRefreshLayout = (SwipeRefreshLayout)searchResultLayout.findViewById(R.id.refresh_layout);
        searchResultRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorPrimary));
        searchResultRefreshLayout.setOnRefreshListener(this);
        searchRecyclerView = (SuperRecyclerView)searchResultLayout.findViewById(R.id.recyclerview);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        searchResult = new ArrayList<>();
        searchResultAdapter = new ProblemSearchResultAdapter(searchResult, this);
        searchRecyclerView.setAdapter(searchResultAdapter);
        cultView.setOutContentLayout(searchResultLayout);

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

        reloadLayout = (RelativeLayout)findViewById(R.id.loading_layout);
        reloadLayout.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        reload = (TextView)findViewById(R.id.reload);
        reload.setText(mContext.getResources().getString(R.string.loading));
        reload.setOnClickListener(this);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        smartTabLayout = (SmartTabLayout)findViewById(R.id.smart_tab_layout);

        userLayout = (LinearLayout)findViewById(R.id.user_layout);
        userLayout.setOnClickListener(this);
        userName = (TextView)findViewById(R.id.username);
        votes = (TextView)findViewById(R.id.votes);

        likeDivider = (LinearLayout)findViewById(R.id.like_divider);
        likeLayout = (LinearLayout)findViewById(R.id.like_layout);
        likeLayout.setOnClickListener(this);
        likes = (TextView)findViewById(R.id.likes);

        gridView = (LeetCoderGridView)findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(position, false);
                    }
                }, 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawers();
                    }
                }, 700);
            }
        });
        tagDivider = (LinearLayout)findViewById(R.id.tag_divider);
        tagDivider.setVisibility(View.GONE);
        tags = (TextView)findViewById(R.id.tags);

        settings = (LinearLayout)findViewById(R.id.settings);
        settings.setOnClickListener(this);
        help = (LinearLayout)findViewById(R.id.help);
        help.setOnClickListener(this);
        feedback = (LinearLayout)findViewById(R.id.feedback);
        feedback.setOnClickListener(this);
        about = (LinearLayout)findViewById(R.id.about);
        about.setOnClickListener(this);

        AppUpdateManager appUpdateManager = new AppUpdateManager(mContext);
        appUpdateManager.checkUpdateInfo(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
        if (LeetCoderApplication.categories == null || LeetCoderApplication.categoriesTag == null) {
            if (BuildConfig.DEBUG) Log.d("LeetCoder", "categories == null || categoriesTag == null: getData()");
            reloadLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            reload.setText(mContext.getResources().getString(R.string.loading));
            getData();
        } else {
            if (BuildConfig.DEBUG) Log.d("LeetCoder", "categories != null && categoriesTag != null: writeData()");
            if (adapter == null) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "adapter == null, just set");
                setView();
            } else {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "adapter != null, don't call");
            }
        }
        if (LeetCoderApplication.user == null || LeetCoderApplication.likes == null || LeetCoderApplication.comments == null) {
            LeetCoderApplication.user = BmobUser.getCurrentUser(LeetCoderApplication.getAppContext(), User.class);
            if (LeetCoderApplication.user == null) {
                userName.setText(mContext.getResources().getString(R.string.click_to_login));
                votes.setText("");
                LeetCoderApplication.likes = null;
                LeetCoderApplication.comments = null;
                likeDivider.setVisibility(View.GONE);
                likeLayout.setVisibility(View.GONE);
            } else {
                userName.setText(LeetCoderApplication.user.getUsername());
                int votesNumber = LeetCoderApplication.user.getVotes();
                if (votesNumber == 1 || votesNumber == -1) {
                    votes.setText(votesNumber + mContext.getResources().getString(R.string.vote_post));
                } else {
                    votes.setText(votesNumber + mContext.getResources().getString(R.string.votes_post));
                }
                LeetCoderApplication.likes = LeetCoderApplication.user.getLikeProblems();
                LeetCoderApplication.comments = LeetCoderApplication.user.getComments();
                likeDivider.setVisibility(View.VISIBLE);
                likeLayout.setVisibility(View.VISIBLE);
                if (LeetCoderApplication.likes.size() == 1) likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.like_post));
                else likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.likes_post));
            }
        } else {
            getVote();
            userName.setText(LeetCoderApplication.user.getUsername());
            LeetCoderApplication.likes = LeetCoderApplication.user.getLikeProblems();
            LeetCoderApplication.comments = LeetCoderApplication.user.getComments();
            likeDivider.setVisibility(View.VISIBLE);
            likeLayout.setVisibility(View.VISIBLE);
            if (LeetCoderApplication.likes.size() == 1) likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.like_post));
            else likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.likes_post));
        }
    }

    private void getVote() {
        BmobQuery<Vote> query = new BmobQuery<Vote>();
        query.addWhereEqualTo("userName", LeetCoderApplication.user.getUsername());
        query.setLimit(1);
        query.findObjects(LeetCoderApplication.getAppContext(), new FindListener<Vote>() {
            @Override
            public void onSuccess(List<Vote> object) {
                LeetCoderApplication.user.setVotes(object.get(0).getVote());
                int votesNumber = object.get(0).getVote();
                if (votesNumber == 1 || votesNumber == -1) {
                    votes.setText(votesNumber + mContext.getResources().getString(R.string.vote_post));
                } else {
                    votes.setText(votesNumber + mContext.getResources().getString(R.string.votes_post));
                }
            }
            @Override
            public void onError(int code, String msg) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Get vote failed: " + msg);
                int votesNumber = LeetCoderApplication.user.getVotes();
                if (votesNumber == 1 || votesNumber == -1) {
                    votes.setText(votesNumber + mContext.getResources().getString(R.string.vote_post));
                } else {
                    votes.setText(votesNumber + mContext.getResources().getString(R.string.votes_post));
                }
            }
        });
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
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (findViewById(R.id.action_sort) != null) {
                    findViewById(R.id.action_sort).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            sort();
                            return true;
                        }
                    });
                }
                if (findViewById(R.id.action_search) != null) {
                    findViewById(R.id.action_search).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            cultView.showSlide();
                            showKeyboard(searchInput);
                            return true;
                        }
                    });
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item);
            case R.id.action_search:
                cultView.showSlide();
                showKeyboard(searchInput);
                return true;
            case R.id.action_sort:
                sort();
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
        } else if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        } else {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return;
        }
    }

    private void hideKeyboard() {
        cultView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getCurrentFocus() != null) {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, LeetCoderApplication.KEYBOARD_CULT_DELAY);
    }

    private void showKeyboard(final View view) {
        view.requestFocus();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(view, 0);
            }
        }, LeetCoderApplication.KEYBOARD_CULT_DELAY);
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
            case R.id.reload:
                reloadLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                reload.setText(mContext.getResources().getString(R.string.loading));
                getData();
                break;
            case R.id.user_layout:
                login();
                break;
            case R.id.like_layout:
                if (LeetCoderApplication.categories == null) {
                    LeetCoderUtil.showToast(mContext, R.string.loading_data_content);
                } else {
                    startActivityForResult(new Intent(mContext, LikesActivity.class), START_LIKES);
                }
                break;
            case R.id.settings:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            case R.id.help:
                startActivity(new Intent(mContext, HelpActivity.class));
                break;
            case R.id.feedback:
                startActivity(new Intent(mContext, FeedbackActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(mContext, AboutActivity.class));
                break;
        }
    }

    private MaterialDialog loginDialog;
    private View loginView;

    private MaterialDialog registerDialog;
    private View registerView;

    private void login() {
        if (LeetCoderApplication.user == null || LeetCoderApplication.likes == null || LeetCoderApplication.comments == null) {
            LeetCoderApplication.user = BmobUser.getCurrentUser(LeetCoderApplication.getAppContext(), User.class);
            if (LeetCoderApplication.user == null) {
                // need to login or register
                new MaterialDialog.Builder(mContext)
                        .title(R.string.login_or_register_title)
                        .positiveText(R.string.login_or_register_login)
                        .negativeText(R.string.login_or_register_register)
                        .neutralText(R.string.login_or_register_cancel)
                        .cancelable(false)
                        .customView(R.layout.dialog_login_or_register, false)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (which == DialogAction.POSITIVE) {
                                    // login
                                    loginDialog = new MaterialDialog.Builder(mContext)
                                            .title(R.string.login_title)
                                            .customView(R.layout.dialog_login, false)
                                            .positiveText(R.string.login_ok)
                                            .negativeText(R.string.login_cancel)
                                            .neutralText(R.string.login_forget_password)
                                            .cancelable(false)
                                            .autoDismiss(false)
                                            .showListener(new DialogInterface.OnShowListener() {
                                                @Override
                                                public void onShow(DialogInterface dialog) {
                                                    loginView.findViewById(R.id.username).getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                    loginView.findViewById(R.id.password).getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                    showKeyboard(loginView.findViewById(R.id.username));
                                                }
                                            })
                                            .dismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    hideKeyboard();
                                                }
                                            })
                                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    if (which == DialogAction.POSITIVE) {
                                                        // login
                                                        final String userName = ((EditText)loginView.findViewById(R.id.username)).getText().toString();
                                                        String password = ((EditText)loginView.findViewById(R.id.password)).getText().toString();
                                                        if ("".equals(userName)) {
                                                            LeetCoderUtil.showToast(mContext, R.string.login_empty_user_name);
                                                        } else if ("".equals(password)) {
                                                            LeetCoderUtil.showToast(mContext, R.string.login_empty_password);
                                                        } else {
                                                            LeetCoderUtil.showToast(mContext, R.string.login_ing);
                                                            LeetCoderApplication.user = new User();
                                                            LeetCoderApplication.user.setUsername(userName);
                                                            LeetCoderApplication.user.setMyPassword(password);
                                                            LeetCoderApplication.user.setPassword(password);
                                                            LeetCoderApplication.user.login(LeetCoderApplication.getAppContext(), new SaveListener() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    // find the vote
                                                                    BmobQuery<Vote> query = new BmobQuery<Vote>();
                                                                    query.addWhereEqualTo("userName", userName);
                                                                    query.setLimit(1);
                                                                    query.findObjects(LeetCoderApplication.getAppContext(), new FindListener<Vote>() {
                                                                        @Override
                                                                        public void onSuccess(List<Vote> object) {
                                                                            loginDialog.dismiss();
                                                                            LeetCoderUtil.showToast(mContext, R.string.login_successfully);
                                                                            MainActivity.this.userName.setText(LeetCoderApplication.user.getUsername());
                                                                            int votesNumber = object.get(0).getVote();
                                                                            LeetCoderApplication.user.setVotes(votesNumber);
                                                                            if (votesNumber == 1 || votesNumber == -1) {
                                                                                votes.setText(votesNumber + mContext.getResources().getString(R.string.vote_post));
                                                                            } else {
                                                                                votes.setText(votesNumber + mContext.getResources().getString(R.string.votes_post));
                                                                            }
                                                                            LeetCoderApplication.user = BmobUser.getCurrentUser(LeetCoderApplication.getAppContext(), User.class);
                                                                            LeetCoderApplication.likes = LeetCoderApplication.user.getLikeProblems();
                                                                            LeetCoderApplication.comments = LeetCoderApplication.user.getComments();
                                                                            likeDivider.setVisibility(View.VISIBLE);
                                                                            likeLayout.setVisibility(View.VISIBLE);
                                                                            if (LeetCoderApplication.likes.size() == 1) likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.like_post));
                                                                            else likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.likes_post));
                                                                        }
                                                                        @Override
                                                                        public void onError(int code, String msg) {
                                                                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "Sign in failed: " + msg);
                                                                            LeetCoderUtil.showToast(mContext, R.string.login_internet_error);
                                                                        }
                                                                    });

                                                                }
                                                                @Override
                                                                public void onFailure(int code, String msg) {
                                                                    if ("username or password incorrect.".equals(msg)) {
                                                                        LeetCoderUtil.showToast(mContext, R.string.login_user_name_or_password_error);
                                                                    } else {
                                                                        LeetCoderUtil.showToast(mContext, R.string.login_internet_error);
                                                                    }
                                                                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Sign in failed: " + msg);
                                                                }
                                                            });
                                                        }
                                                    } else if (which == DialogAction.NEGATIVE) {
                                                        loginDialog.dismiss();
                                                    } else {
                                                        // forget password
                                                        loginDialog.dismiss();
                                                        new MaterialDialog.Builder(mContext)
                                                                .title(R.string.forget_password_title)
                                                                .content(R.string.forget_password_content)
                                                                .positiveText(R.string.forget_password_write)
                                                                .negativeText(R.string.forget_password_copy)
                                                                .neutralText(R.string.forget_password_cancel)
                                                                .forceStacking(true)
                                                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                                                    @Override
                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                        if (which == DialogAction.POSITIVE) {
                                                                            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                                            emailIntent.setType("plain/text");
                                                                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Nightonke@outlook.com"});
                                                                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Get My Password Back For LeetCoder");
                                                                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                                                                            mContext.startActivity(Intent.createChooser(emailIntent, mContext.getResources().getString(R.string.forget_password_email_title)));
                                                                        } else if (which == DialogAction.NEGATIVE) {
                                                                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                                                            ClipData clip = ClipData.newPlainText("Email address copied.", "Nightonke@outlook.com");
                                                                            clipboard.setPrimaryClip(clip);
                                                                            LeetCoderUtil.showToast(mContext, R.string.forget_password_copied);
                                                                        }
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                }
                                            })
                                            .show();
                                    loginView = loginDialog.getCustomView();
                                } else if (which == DialogAction.NEGATIVE) {
                                    // register
                                    registerDialog = new MaterialDialog.Builder(mContext)
                                            .title(R.string.register_title)
                                            .customView(R.layout.dialog_register, false)
                                            .positiveText(R.string.register_ok)
                                            .negativeText(R.string.register_cancel)
                                            .cancelable(false)
                                            .autoDismiss(false)
                                            .showListener(new DialogInterface.OnShowListener() {
                                                @Override
                                                public void onShow(DialogInterface dialog) {
                                                    registerView.findViewById(R.id.username).getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                    registerView.findViewById(R.id.password).getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                    registerView.findViewById(R.id.password_again).getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                    showKeyboard(registerView.findViewById(R.id.username));
                                                }
                                            })
                                            .dismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    hideKeyboard();
                                                }
                                            })
                                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    if (which == DialogAction.POSITIVE) {
                                                        // register
                                                        final String userName = ((EditText)registerView.findViewById(R.id.username)).getText().toString();
                                                        String password = ((EditText)registerView.findViewById(R.id.password)).getText().toString();
                                                        String passwordAgain = ((EditText)registerView.findViewById(R.id.password_again)).getText().toString();
                                                        if ("".equals(userName)) {
                                                            LeetCoderUtil.showToast(mContext, R.string.register_empty_user_name);
                                                        } else if (LeetCoderUtil.textCounter(userName) > 20) {
                                                            LeetCoderUtil.showToast(mContext, R.string.register_invalid_user_name);
                                                        } else if ("".equals(password)) {
                                                            LeetCoderUtil.showToast(mContext, R.string.register_empty_password);
                                                        } else if ("".equals(passwordAgain)) {
                                                            LeetCoderUtil.showToast(mContext, R.string.register_empty_password_again);
                                                        } else if (!password.equals(passwordAgain)) {
                                                            LeetCoderUtil.showToast(mContext, R.string.register_password_error);
                                                        } else {
                                                            LeetCoderUtil.showToast(mContext, R.string.register_ing);
                                                            LeetCoderApplication.user = new User();
                                                            LeetCoderApplication.user.setUsername(userName);
                                                            LeetCoderApplication.user.setMyPassword(password);
                                                            LeetCoderApplication.user.setPassword(password);
                                                            LeetCoderApplication.user.signUp(LeetCoderApplication.getAppContext(), new SaveListener() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    Vote vote = new Vote();
                                                                    vote.setUserName(userName);
                                                                    vote.setVote(0);
                                                                    vote.save(LeetCoderApplication.getAppContext(), new SaveListener() {
                                                                        @Override
                                                                        public void onSuccess() {
                                                                            registerDialog.dismiss();
                                                                            LeetCoderUtil.showToast(mContext, R.string.register_successfully);
                                                                            MainActivity.this.userName.setText(LeetCoderApplication.user.getUsername());
                                                                            LeetCoderApplication.user.setVotes(0);
                                                                            votes.setText(LeetCoderApplication.user.getVotes() + mContext.getResources().getString(R.string.votes_post));
                                                                            LeetCoderApplication.user = BmobUser.getCurrentUser(LeetCoderApplication.getAppContext(), User.class);
                                                                            LeetCoderApplication.likes = LeetCoderApplication.user.getLikeProblems();
                                                                            LeetCoderApplication.comments = LeetCoderApplication.user.getComments();
                                                                            likeDivider.setVisibility(View.VISIBLE);
                                                                            likeLayout.setVisibility(View.VISIBLE);
                                                                            if (LeetCoderApplication.likes.size() == 1) likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.like_post));
                                                                            else likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.likes_post));
                                                                        }
                                                                        @Override
                                                                        public void onFailure(int code, String arg0) {
                                                                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "Sign up failed: " + arg0);
                                                                            LeetCoderUtil.showToast(mContext, R.string.register_internet_error);
                                                                        }
                                                                    });
                                                                }
                                                                @Override
                                                                public void onFailure(int code, String msg) {
                                                                    if (msg.charAt(0) == 'u') {
                                                                        LeetCoderUtil.showToast(mContext, R.string.register_repeat_user);
                                                                    } else {
                                                                        LeetCoderUtil.showToast(mContext, R.string.register_internet_error);
                                                                    }
                                                                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Sign up failed: " + msg);
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        registerDialog.dismiss();
                                                    }
                                                }
                                            })
                                            .show();
                                    registerView = registerDialog.getCustomView();
                                }
                            }
                        })
                        .show();
            } else {
                userName.setText(LeetCoderApplication.user.getUsername());
                int votesNumber = LeetCoderApplication.user.getVotes();
                if (votesNumber == 1 || votesNumber == -1) {
                    votes.setText(votesNumber + mContext.getResources().getString(R.string.vote_post));
                } else {
                    votes.setText(votesNumber + mContext.getResources().getString(R.string.votes_post));
                }
                LeetCoderApplication.likes = LeetCoderApplication.user.getLikeProblems();
                likeDivider.setVisibility(View.VISIBLE);
                likeLayout.setVisibility(View.VISIBLE);
                if (LeetCoderApplication.likes.size() == 1) likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.like_post));
                else likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.likes_post));
            }
        } else {
            // log out
            new MaterialDialog.Builder(mContext)
                    .title(R.string.logout_title)
                    .content(R.string.logout_content)
                    .positiveText(R.string.logout_ok)
                    .negativeText(R.string.logout_cancel)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (which == DialogAction.POSITIVE) {
                                LeetCoderApplication.user.logOut(LeetCoderApplication.getAppContext());
                                LeetCoderApplication.user = null;
                                userName.setText(mContext.getResources().getString(R.string.click_to_login));
                                votes.setText("");
                                LeetCoderApplication.likes = null;
                                LeetCoderApplication.comments = null;
                                likeDivider.setVisibility(View.GONE);
                                likeLayout.setVisibility(View.GONE);
                            }
                        }
                    })
                    .show();
        }
    }

    private void sort() {
        if (LeetCoderApplication.categories == null || LeetCoderApplication.categoriesTag == null) {
            LeetCoderUtil.showToast(mContext, R.string.problem_index_is_loading);
        } else {
            new MaterialDialog.Builder(mContext)
                    .title(R.string.sort_title)
                    .items(R.array.sort_types_problem)
                    .itemsCallbackSingleChoice(CategoryFragment.sortType, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            CategoryFragment.sortType = which;
                            switch (which) {
                                case 0: sortByDate(); break;
                                case 1: sortByDateReversely(); break;
                                case 2: sortByTitle(); break;
                                case 3: sortByTitleReversely(); break;
                                case 4: sortByLevel(); break;
                                case 5: sortByLevelReversely(); break;
                                case 6: sortByLikes(); break;
                                case 7:sortByLikesReversely(); break;
                            }
                            if (adapter != null) {
                                for (int i = 0; i < adapter.getCount(); i++) {
                                    if (adapter.getPage(i) != null) {
                                        ((CategoryFragment)adapter.getPage(i)).notifySort();
                                    }
                                }
                            }
                            LeetCoderUtil.showToast(mContext, R.string.sorting);
                            dialog.dismiss();
                            return true;
                        }
                    })
                    .negativeText(R.string.cancel)
                    .show();
        }
    }

    private void sortByDate() {
        for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
            Collections.sort(category, new Comparator<Problem_Index>() {
                @Override
                public int compare(Problem_Index lhs, Problem_Index rhs) {
                    if (lhs.getId() < rhs.getId()) return -1;
                    else if (lhs.getId() > rhs.getId()) return 1;
                    else return 0;
                }
            });
        }
    }

    private void sortByDateReversely() {
        for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
            Collections.sort(category, new Comparator<Problem_Index>() {
                @Override
                public int compare(Problem_Index rhs, Problem_Index lhs) {
                    if (lhs.getId() < rhs.getId()) return -1;
                    else if (lhs.getId() > rhs.getId()) return 1;
                    else return 0;
                }
            });
        }
    }

    private void sortByTitle() {
        for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
            Collections.sort(category, new Comparator<Problem_Index>() {
                @Override
                public int compare(Problem_Index lhs, Problem_Index rhs) {
                    return lhs.getTitle().compareTo(rhs.getTitle());
                }
            });
        }
    }

    private void sortByTitleReversely() {
        for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
            Collections.sort(category, new Comparator<Problem_Index>() {
                @Override
                public int compare(Problem_Index rhs, Problem_Index lhs) {
                    return lhs.getTitle().compareTo(rhs.getTitle());
                }
            });
        }
    }

    private void sortByLevel() {
        for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
            Collections.sort(category, new Comparator<Problem_Index>() {
                @Override
                public int compare(Problem_Index lhs, Problem_Index rhs) {
                    int l = getLevelNumber(lhs.getLevel());
                    int r = getLevelNumber(rhs.getLevel());
                    if (l < r) return -1;
                    else if (l > r) return 1;
                    else return 0;
                }
            });
        }
    }

    private void sortByLevelReversely() {
        for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
            Collections.sort(category, new Comparator<Problem_Index>() {
                @Override
                public int compare(Problem_Index rhs, Problem_Index lhs) {
                    int l = getLevelNumber(lhs.getLevel());
                    int r = getLevelNumber(rhs.getLevel());
                    if (l < r) return -1;
                    else if (l > r) return 1;
                    else return 0;
                }
            });
        }
    }

    private void sortByLikes() {
        for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
            Collections.sort(category, new Comparator<Problem_Index>() {
                @Override
                public int compare(Problem_Index rhs, Problem_Index lhs) {
                    if (lhs.getLike() < rhs.getLike()) return -1;
                    else if (lhs.getLike() > rhs.getLike()) return 1;
                    else return 0;
                }
            });
        }
    }

    private void sortByLikesReversely() {
        for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
            Collections.sort(category, new Comparator<Problem_Index>() {
                @Override
                public int compare(Problem_Index lhs, Problem_Index rhs) {
                    if (lhs.getLike() < rhs.getLike()) return -1;
                    else if (lhs.getLike() > rhs.getLike()) return 1;
                    else return 0;
                }
            });
        }
    }

    private int getLevelNumber(String levelString) {
        switch (levelString) {
            case "Easy":
                return 0;
            case "Medium":
                return 1;
            case "Hard":
                return 2;
            default:
                return 0;
        }
    }

    private boolean gettingData = false;
    private void getData() {
        gettingData = true;
        BmobQuery<Problem_Index> query = new BmobQuery<Problem_Index>();
        query.addWhereGreaterThan("id", -1);
        query.setLimit(Integer.MAX_VALUE);
        query.findObjects(LeetCoderApplication.getAppContext(), new FindListener<Problem_Index>() {
            @Override
            public void onSuccess(List<Problem_Index> object) {
                gettingData = false;
                if (BuildConfig.DEBUG) {
                    Log.d("LeetCoder", "Get " + object.size() + " problem indices");
                }
                LeetCoderApplication.categories = new ArrayList<>();
                LeetCoderApplication.categoriesTag = new ArrayList<>();
                HashMap<String, ArrayList<Problem_Index>> hash = new HashMap<String, ArrayList<Problem_Index>>();
                for (Problem_Index problemIndex : object) {
                    List<String> tags = problemIndex.getTags();
                    for (String tag : tags) {
                        if (hash.containsKey(tag)) {
                            hash.get(tag).add(problemIndex);
                        } else {
                            ArrayList<Problem_Index> category = new ArrayList<Problem_Index>();
                            category.add(problemIndex);
                            hash.put(tag, category);
                        }
                    }
                }
                for (HashMap.Entry<String, ArrayList<Problem_Index>> entry : hash.entrySet()) {
                    LeetCoderApplication.categoriesTag.add(entry.getKey());
                }
                Collections.sort(LeetCoderApplication.categoriesTag, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return lhs.compareTo(rhs);
                    }
                });
                sortByDate();
                for (String tag : LeetCoderApplication.categoriesTag) {
                    LeetCoderApplication.categories.add(hash.get(tag));
                }
                setView();
            }
            @Override
            public void onError(int code, String msg) {
                gettingData = false;
                if (adapter != null) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getPage(i) != null) {
                            ((CategoryFragment)adapter.getPage(i)).stopRefresh();
                        }
                    }
                }
                if (BuildConfig.DEBUG) {
                    Log.d("LeetCoder", "Get problem indices failed: " + msg);
                }
                reloadLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                reload.setText(mContext.getResources().getString(R.string.reload));
                reload.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setView() {
        FragmentPagerItems pages = new FragmentPagerItems(mContext);
        for (String tag : LeetCoderApplication.categoriesTag) {
            pages.add(FragmentPagerItem.of(tag, CategoryFragment.class));
        }
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);

        tagAdapter = new TagGridViewAdapter(mContext);
        gridView.setAdapter(tagAdapter);
        gridView.setFocusable(false);
        tagDivider.setVisibility(View.VISIBLE);
        int tagSize = LeetCoderApplication.categories.size();
        if (tagSize == 1) tags.setText(LeetCoderApplication.categories.size() + mContext.getResources().getString(R.string.tag_post));
        else tags.setText(LeetCoderApplication.categories.size() + mContext.getResources().getString(R.string.tags_post));


        reload.setText(mContext.getResources().getString(R.string.reload));  // for refreshing
        reload.setVisibility(View.GONE);
        reloadLayout.setVisibility(View.GONE);
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public void onRefresh(SwipeRefreshLayout swipeRefreshLayout) {
        if (gettingData) swipeRefreshLayout.setRefreshing(false);
        else {
            this.swipeRefreshLayout = swipeRefreshLayout;
            getData();
        }
    }

    private void search(String s) {
        s = s.toLowerCase();
        searchResult = new ArrayList<>();
        if (!"".equals(s)) {
            if (LeetCoderApplication.categories != null && LeetCoderApplication.categoriesTag != null) {
                HashSet<Integer> ids = new HashSet<>();
                for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
                    for (Problem_Index problemIndex : category) {
                        String titleString = problemIndex.getTitle().toLowerCase();
                        if (titleString.contains(s)) {
                            if (!ids.contains(problemIndex.getId())) {
                                ids.add(problemIndex.getId());
                                searchResult.add(problemIndex);
                            }
                        }
                    }
                }
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Search result: " + searchResult.size() + " problem(s)");
                LeetCoderUtil.sortProblemSearchResult(searchResult);
            }
        }
        if (searchResultRefreshLayout != null) searchResultRefreshLayout.setRefreshing(false);
        searchResultAdapter = new ProblemSearchResultAdapter(searchResult, this);
        searchRecyclerView.setAdapter(searchResultAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(mContext, ProblemActivity.class);
        intent.putExtra("id", searchResult.get(position).getId());
        startActivityForResult(intent, START_PROBLEM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case START_PROBLEM:
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Update search result");
                searchResultAdapter = new ProblemSearchResultAdapter(searchResult, this);
                searchRecyclerView.setAdapter(searchResultAdapter);
                int position = -1;
                if (resultCode == RESULT_OK) {
                    position = data.getIntExtra("category", -1);
                }
                if (position != -1) viewPager.setCurrentItem(position);
                break;
            case START_LIKES:

                break;
        }
    }

    @Override
    public void onRefresh() {
        LeetCoderUtil.showToast(mContext, R.string.researching);
        search(searchInput.getText().toString());
    }

    @Override
    public void onItemLongClick(final Problem_Index problemIndex) {
        if (LeetCoderApplication.user == null || LeetCoderApplication.likes == null) {

        } else {
            if (LeetCoderApplication.likes.contains(problemIndex.getId())) {
                new MaterialDialog.Builder(this)
                        .title(R.string.dis_collect_title)
                        .content(R.string.dis_collect_content)
                        .positiveText(R.string.dis_collect_ok)
                        .negativeText(R.string.dis_collect_cancel)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (which == DialogAction.POSITIVE) {
                                    problemIndex.setLike(problemIndex.getLike() - 1);
                                    problemIndex.update(LeetCoderApplication.getAppContext(), problemIndex.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            int index = LeetCoderApplication.user.getLikeProblems().indexOf(problemIndex.getId());
                                            LeetCoderApplication.user.getLikeProblems().remove(index);
                                            LeetCoderApplication.user.update(LeetCoderApplication.getAppContext(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    LeetCoderUtil.showToast(mContext, R.string.like_dislike_successfully);
                                                    if (LeetCoderApplication.likes.size() == 1) likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.like_post));
                                                    else likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.likes_post));
                                                }
                                                @Override
                                                public void onFailure(int i, String s) {
                                                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Dislike failed: " + s);
                                                    LeetCoderApplication.user.getLikeProblems().add(problemIndex.getId());
                                                    LeetCoderUtil.showToast(mContext, R.string.like_dislike_failed);
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFailure(int i, String s) {
                                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "Dislike failed: " + s);
                                            LeetCoderUtil.showToast(mContext, R.string.like_dislike_failed);
                                            problemIndex.setLike(problemIndex.getLike() + 1);
                                        }
                                    });
                                }
                            }
                        })
                        .show();
            } else {
                new MaterialDialog.Builder(this)
                        .title(R.string.collect_title)
                        .content(R.string.collect_content)
                        .positiveText(R.string.collect_ok)
                        .negativeText(R.string.collect_cancel)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (which == DialogAction.POSITIVE) {
                                    problemIndex.setLike(problemIndex.getLike() + 1);
                                    problemIndex.update(LeetCoderApplication.getAppContext(), problemIndex.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            LeetCoderApplication.user.getLikeProblems().add(problemIndex.getId());
                                            LeetCoderApplication.user.update(LeetCoderApplication.getAppContext(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    LeetCoderUtil.showToast(mContext, R.string.like_like_successfully);
                                                    if (LeetCoderApplication.likes.size() == 1) likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.like_post));
                                                    else likes.setText(LeetCoderApplication.likes.size() + mContext.getResources().getString(R.string.likes_post));
                                                }
                                                @Override
                                                public void onFailure(int i, String s) {
                                                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Like failed: " + s);
                                                    int index = LeetCoderApplication.user.getLikeProblems().indexOf(problemIndex.getId());
                                                    LeetCoderApplication.user.getLikeProblems().remove(index);
                                                    LeetCoderUtil.showToast(mContext, R.string.like_like_failed);
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFailure(int i, String s) {
                                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "Like failed: " + s);
                                            LeetCoderUtil.showToast(mContext, R.string.like_like_failed);
                                            problemIndex.setLike(problemIndex.getLike() - 1);
                                        }
                                    });
                                }
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onJump(int position) {
        if (viewPager != null) viewPager.setCurrentItem(position);
    }
}
