package com.nightonke.leetcoder.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ppamorim.cult.CultView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.Utils.LeetCoderApplication;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.Adapter.ProblemFavoriteAdapter;
import com.nightonke.leetcoder.Adapter.ProblemSearchResultAdapter;
import com.nightonke.leetcoder.Model.Problem_Index;
import com.nightonke.leetcoder.R;
import com.nightonke.leetcoder.Model.User;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

public class LikesActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        ProblemFavoriteAdapter.OnLikeItemClickListener,
        ProblemSearchResultAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        ProblemFavoriteAdapter.OnLikeItemLongClickListener {

    private final int START_PROBLEM = 1;
    private int sortType = 0;

    private Context mContext;

    private CultView cultView;

    private RelativeLayout reloadLayout;
    private ProgressBar progressBar;
    private TextView reload;

    private SwipeRefreshLayout swipeRefreshLayout;
    private SuperRecyclerView superRecyclerView;
    private ArrayList<Problem_Index> likes;
    private ProblemFavoriteAdapter adapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext, R.color.colorAccent);

        cultView = (CultView)findViewById(R.id.cult_view);

        searchLayout = View.inflate(mContext, R.layout.fragment_search_likes, null);
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
        searchResultRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));
        searchResultRefreshLayout.setOnRefreshListener(this);
        searchRecyclerView = (SuperRecyclerView)searchResultLayout.findViewById(R.id.recyclerview);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        searchResult = new ArrayList<>();
        searchResultAdapter = new ProblemSearchResultAdapter(searchResult, this);
        searchRecyclerView.setAdapter(searchResultAdapter);
        cultView.setOutContentLayout(searchResultLayout);

        ((AppCompatActivity)mContext).setSupportActionBar(cultView.getInnerToolbar());
        cultView.getInnerToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        cultView.getOutToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        (LeetCoderUtil.getActionBarTextView(cultView.getInnerToolbar())).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        ActionBar actionBar = ((AppCompatActivity)mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mContext.getResources().getString(R.string.favorite_activity_title));
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(LeetCoderUtil.getDrawable(R.drawable.icon_like_red, LeetCoderUtil.dpToPx(36), LeetCoderUtil.dpToPx(36)));
        }

        reloadLayout = (RelativeLayout)findViewById(R.id.loading_layout);
        reloadLayout.setVisibility(View.GONE);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        reload = (TextView)findViewById(R.id.reload);
        reload.setText(mContext.getResources().getString(R.string.loading));
        reload.setOnClickListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        superRecyclerView = (SuperRecyclerView)findViewById(R.id.recyclerview);
        superRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LeetCoderApplication.user == null || LeetCoderApplication.likes == null) {
            LeetCoderApplication.user = BmobUser.getCurrentUser(LeetCoderApplication.getAppContext(), User.class);
            if (LeetCoderApplication.user == null) {
                finish();
            } else {
                LeetCoderApplication.likes = LeetCoderApplication.user.getLikeProblems();
                likes = new ArrayList<>();
                HashSet<Integer> ids = new HashSet<>();
                for (Integer i : LeetCoderApplication.likes) {
                    for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
                        for (Problem_Index problemIndex : category) {
                            if (problemIndex.getId() == i) {
                                if (!ids.contains(i)) {
                                    ids.add(i);
                                    likes.add(problemIndex);
                                }
                            }
                        }
                    }
                }
                adapter = new ProblemFavoriteAdapter(likes, this, this);
                superRecyclerView.setAdapter(adapter);
            }
        } else {
            LeetCoderApplication.likes = LeetCoderApplication.user.getLikeProblems();
            likes = new ArrayList<>();
            HashSet<Integer> ids = new HashSet<>();
            for (Integer i : LeetCoderApplication.likes) {
                for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
                    for (Problem_Index problemIndex : category) {
                        if (problemIndex.getId() == i) {
                            if (!ids.contains(i)) {
                                ids.add(i);
                                likes.add(problemIndex);
                            }
                        }
                    }
                }
            }
            adapter = new ProblemFavoriteAdapter(likes, this, this);
            superRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_like, menu);
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
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
                return true;
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
        }
        super.onBackPressed();
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

                break;
        }
    }

    private void sort() {
        if (likes == null) {
            LeetCoderUtil.showToast(mContext, R.string.data_not_found);
        } else {
            new MaterialDialog.Builder(mContext)
                    .title(R.string.sort_title)
                    .items(R.array.sort_types_likes)
                    .itemsCallbackSingleChoice(sortType, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            sortType = which;
                            switch (which) {
                                case 0: sortByLikeDate(); break;
                                case 1: sortByLikeDateReversely(); break;
                                case 2: sortByDate(); break;
                                case 3: sortByDateReversely(); break;
                                case 4: sortByTitle(); break;
                                case 5: sortByTitleReversely(); break;
                                case 6: sortByLevel(); break;
                                case 7: sortByLevelReversely(); break;
                                case 8: sortByLikes(); break;
                                case 9:sortByLikesReversely(); break;
                            }
                            adapter = new ProblemFavoriteAdapter(likes, LikesActivity.this, LikesActivity.this);
                            superRecyclerView.setAdapter(adapter);
                            LeetCoderUtil.showToast(mContext, R.string.sorting);
                            dialog.dismiss();
                            return true;
                        }
                    })
                    .negativeText(R.string.cancel)
                    .show();
        }
    }

    private void sortByLikeDate() {
        int startPosition = 0;
        int size = likes.size();
        for (Integer id : LeetCoderApplication.likes) {
            for (int i = startPosition; i < size; i++) {
                if (likes.get(i).getId() == id) {
                    Collections.swap(likes, startPosition, i);
                    break;
                }
            }
            startPosition++;
        }
    }

    private void sortByLikeDateReversely() {
        int endPosition = likes.size() - 1;
        for (Integer id : LeetCoderApplication.likes) {
            for (int i = endPosition; i >= 0; i--) {
                if (likes.get(i).getId() == id) {
                    Collections.swap(likes, endPosition, i);
                    break;
                }
            }
            endPosition--;
        }
    }

    private void sortByDate() {
        Collections.sort(likes, new Comparator<Problem_Index>() {
            @Override
            public int compare(Problem_Index lhs, Problem_Index rhs) {
                if (lhs.getId() < rhs.getId()) return -1;
                else if (lhs.getId() > rhs.getId()) return 1;
                else return 0;
            }
        });
    }

    private void sortByDateReversely() {
        Collections.sort(likes, new Comparator<Problem_Index>() {
            @Override
            public int compare(Problem_Index rhs, Problem_Index lhs) {
                if (lhs.getId() < rhs.getId()) return -1;
                else if (lhs.getId() > rhs.getId()) return 1;
                else return 0;
            }
        });
    }

    private void sortByTitle() {
        Collections.sort(likes, new Comparator<Problem_Index>() {
            @Override
            public int compare(Problem_Index lhs, Problem_Index rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
    }

    private void sortByTitleReversely() {
        Collections.sort(likes, new Comparator<Problem_Index>() {
            @Override
            public int compare(Problem_Index rhs, Problem_Index lhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
    }

    private void sortByLevel() {
        Collections.sort(likes, new Comparator<Problem_Index>() {
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

    private void sortByLevelReversely() {
        Collections.sort(likes, new Comparator<Problem_Index>() {
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

    private void sortByLikes() {
        Collections.sort(likes, new Comparator<Problem_Index>() {
            @Override
            public int compare(Problem_Index rhs, Problem_Index lhs) {
                if (lhs.getLike() < rhs.getLike()) return -1;
                else if (lhs.getLike() > rhs.getLike()) return 1;
                else return 0;
            }
        });
    }

    private void sortByLikesReversely() {
        Collections.sort(likes, new Comparator<Problem_Index>() {
            @Override
            public int compare(Problem_Index lhs, Problem_Index rhs) {
                if (lhs.getLike() < rhs.getLike()) return -1;
                else if (lhs.getLike() > rhs.getLike()) return 1;
                else return 0;
            }
        });
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

    private void search(String s) {
        s = s.toLowerCase();
        searchResult = new ArrayList<>();
        if (!"".equals(s)) {
            if (likes != null) {
                HashSet<Integer> ids = new HashSet<>();
                for (Problem_Index problemIndex : likes) {
                    String titleString = problemIndex.getTitle().toLowerCase();
                    if (titleString.contains(s)) {
                        if (!ids.contains(problemIndex.getId())) {
                            ids.add(problemIndex.getId());
                            searchResult.add(problemIndex);
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
    public void onLikeItemClick(int position) {
        Intent intent = new Intent(mContext, ProblemActivity.class);
        intent.putExtra("id", likes.get(position).getId());
        startActivityForResult(intent, START_PROBLEM);
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
                break;
        }
    }

    @Override
    public void onRefresh() {
        LeetCoderUtil.showToast(mContext, R.string.researching);
        search(searchInput.getText().toString());
    }

    @Override
    public void onLikeItemLongClick(final Problem_Index problemIndex) {
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
                                            for (Problem_Index p : likes) {
                                                if (p.getId() == problemIndex.getId()) {
                                                    likes.remove(p);
                                                    break;
                                                }
                                            }
                                            adapter = new ProblemFavoriteAdapter(likes, LikesActivity.this, LikesActivity.this);
                                            superRecyclerView.setAdapter(adapter);
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
    }
}
