package com.nightonke.leetcoder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ppamorim.cult.CultView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
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
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        CategoryFragment.OnRefreshListener,
        ProblemSearchResultAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

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
    private TextView nickName;
    private TextView votes;

    private LeetCoderGridView gridView;
    private TagGridViewAdapter tagAdapter;
    private TextView tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext);

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
        nickName = (TextView)findViewById(R.id.nickname);
        votes = (TextView)findViewById(R.id.votes);

        gridView = (LeetCoderGridView)findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                YoYo.with(Techniques.Bounce).delay(0).duration(700).playOn(view);
                viewPager.setCurrentItem(position);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawers();
                    }
                }, 700);
            }
        });
        tags = (TextView)findViewById(R.id.tags);
        tags.setText("0 tags");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
        if (LeetCoderApplication.categories == null || LeetCoderApplication.categoriesTag == null) {
            reloadLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            reload.setText(mContext.getResources().getString(R.string.loading));
            getData();
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
            case R.id.reload:

                break;
        }
    }

    private void sort() {
        if (LeetCoderApplication.categories == null || LeetCoderApplication.categoriesTag == null) {
            Toast.makeText(mContext, "Data not found, please refresh data.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mContext, "Sorting...", Toast.LENGTH_SHORT).show();
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
                FragmentPagerItems pages = new FragmentPagerItems(mContext);
                for (String tag : LeetCoderApplication.categoriesTag) {
                    LeetCoderApplication.categories.add(hash.get(tag));
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
                tags.setText(LeetCoderApplication.categories.size() + " tags");

                reload.setText(mContext.getResources().getString(R.string.reload));  // for refreshing
                reloadLayout.setVisibility(View.GONE);
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
            }
        });
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
        searchResult = new ArrayList<>();
        if (!"".equals(s)) {
            if (LeetCoderApplication.categories != null && LeetCoderApplication.categoriesTag != null) {
                HashSet<Integer> ids = new HashSet<>();
                for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
                    for (Problem_Index problemIndex : category) {
                        String titleString = problemIndex.getTitle();
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
                Log.d("LeetCoder", "Last: " + searchResult.get(searchResult.size() - 1).getTitle());
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
        mContext.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        Toast.makeText(mContext, "Researching...", Toast.LENGTH_SHORT).show();
        search(searchInput.getText().toString());
    }
}
