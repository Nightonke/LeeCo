package com.nightonke.leetcoder.Activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ppamorim.cult.CultView;
import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.Fragment.ProblemCommentFragment;
import com.nightonke.leetcoder.Fragment.ProblemContentFragment;
import com.nightonke.leetcoder.Fragment.ProblemDiscussFragment;
import com.nightonke.leetcoder.Fragment.ProblemSolutionFragment;
import com.nightonke.leetcoder.Model.Problem;
import com.nightonke.leetcoder.Model.ProblemBug;
import com.nightonke.leetcoder.Model.Problem_Index;
import com.nightonke.leetcoder.Model.User;
import com.nightonke.leetcoder.R;
import com.nightonke.leetcoder.Utils.LeetCoderApplication;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nineoldandroids.animation.Animator;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import me.grantland.widget.AutofitTextView;


public class ProblemActivity extends AppCompatActivity
        implements
        ProblemContentFragment.ReloadListener,
        View.OnClickListener,
        ProblemContentFragment.OnTagClickListener,
        ProblemContentFragment.OnSimilarProblemClickListener {

    public static final int START_COMMENT = 4;
    public static final int BACK_COMMENT_CHANGED = 5;
    public static final int BACK_COMMENT_UNCHANGED = 6;

    private boolean loading = false;
    public Problem_Index problem_index;
    public Problem problem;

    private Context mContext;

    private CultView cultView;

    private int lastPagerPosition = 0;
    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;
    private FragmentPagerItemAdapter adapter;

    private TextView title;
    private FrameLayout icon;
    private ImageView originalProblemIcon;
    private FrameLayout likesLayout;
    private ImageView likesIcon;
    private TextView likes;
    private ImageView solutionCopyIcon;
    private ImageView solutionBugIcon;
    private ImageView discussSortIcon;
    private ImageView commentSortIcon;
    private ImageView commentMessageIcon;

    private CoordinatorLayout snackbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext, R.color.colorPrimary);

        cultView = (CultView)findViewById(R.id.cult_view);
        ((AppCompatActivity)mContext).setSupportActionBar(cultView.getInnerToolbar());
        cultView.getInnerToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        cultView.getOutToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        (LeetCoderUtil.getActionBarTextView(cultView.getInnerToolbar())).setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        ActionBar actionBar = ((AppCompatActivity)mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getIcon(position).setVisibility(View.VISIBLE);
                if (position > lastPagerPosition) {
                    YoYo.with(Techniques.BounceInUp)
                            .duration(500)
                            .playOn(getIcon(position));
                    final View goneView = getIcon(lastPagerPosition);
                    YoYo.with(Techniques.FadeOutUp)
                            .duration(300)
                            .withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    goneView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .playOn(goneView);
                } else if (position < lastPagerPosition) {
                    YoYo.with(Techniques.BounceInDown)
                            .duration(500)
                            .playOn(getIcon(position));
                    final View goneView = getIcon(lastPagerPosition);
                    YoYo.with(Techniques.FadeOutDown)
                            .duration(300)
                            .withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    goneView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .playOn(goneView);
                }
                lastPagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPagerTab.setViewPager(viewPager);
        for (int i = 0; i < 4; i++) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LeetCoderUtil.getScreenWidth(mContext) / 4, LeetCoderUtil.dpToPx(56));
            viewPagerTab.getTabAt(i).setLayoutParams(layoutParams);
        }

        title = (TextView)findViewById(R.id.title);
        title.setSelected(true);

        originalProblemIcon = (ImageView)findViewById(R.id.original_problem_icon);
        originalProblemIcon.setOnClickListener(this);
        likesLayout = (FrameLayout)findViewById(R.id.like_number_layout);
        likesLayout.setOnClickListener(this);
        likesIcon = (ImageView)findViewById(R.id.like_number_icon);
        likes = (TextView)findViewById(R.id.like_number);
        solutionCopyIcon = (ImageView)findViewById(R.id.solution_copy_icon);
        solutionCopyIcon.setOnClickListener(this);
        solutionBugIcon = (ImageView)findViewById(R.id.solution_bug_icon);
        solutionBugIcon.setOnClickListener(this);
        discussSortIcon = (ImageView)findViewById(R.id.discuss_sort_icon);
        discussSortIcon.setOnClickListener(this);
        commentSortIcon = (ImageView)findViewById(R.id.comment_sort_icon);
        commentSortIcon.setOnClickListener(this);
        commentMessageIcon = (ImageView)findViewById(R.id.comment_message_icon);
        commentMessageIcon.setOnClickListener(this);

        findViewById(R.id.solution_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.discuss_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.comment_layout).setVisibility(View.INVISIBLE);

        snackbarLayout = (CoordinatorLayout)findViewById(R.id.container);

        int id = getIntent().getIntExtra("id", -1);
        if (id == -1) finish();  // error happens
        if (getIntent().getIntExtra("categoryPosition", -1) == -1 && getIntent().getIntExtra("problemPosition", -1) == -1) {
            // from search result
            if (LeetCoderApplication.categories == null) {
                getProblemIndexFromId(id);
            } else {
                for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
                    for (Problem_Index problemIndex : category) {
                        if (id == problemIndex.getId()) {
                            problem_index = problemIndex;
                        }
                    }
                }
            }
        } else {
            // from categories
            if (LeetCoderApplication.categories == null) {
                getProblemIndexFromId(id);
            } else {
                problem_index = LeetCoderApplication.categories.
                        get(getIntent().getIntExtra("categoryPosition", -1)).
                        get(getIntent().getIntExtra("problemPosition", -1));
            }
        }

        likes.setText(problem_index.getLike() + "");
        title.setText(problem_index.getTitle());
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLikes();
    }

    private void setLikes() {
        if (LeetCoderApplication.user == null || LeetCoderApplication.likes == null || LeetCoderApplication.comments == null) {
            LeetCoderApplication.user = BmobUser.getCurrentUser(LeetCoderApplication.getAppContext(), User.class);
            if (LeetCoderApplication.user == null) {
                LeetCoderApplication.likes = null;
                LeetCoderApplication.comments = null;
            } else {
                LeetCoderApplication.likes = LeetCoderApplication.user.getLikeProblems();
                LeetCoderApplication.comments = LeetCoderApplication.user.getComments();
                if (LeetCoderApplication.user.getLikeProblems().contains(problem_index.getId())) {
                    likesIcon.setImageResource(R.drawable.icon_like_red);
                    likes.setTextColor(ContextCompat.getColor(mContext, R.color.like_red));
                } else {
                    likesIcon.setImageResource(R.drawable.icon_like_blue);
                    likes.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                }
            }
        } else {
            if (LeetCoderApplication.user.getLikeProblems().contains(problem_index.getId())) {
                likesIcon.setImageResource(R.drawable.icon_like_red);
                likes.setTextColor(ContextCompat.getColor(mContext, R.color.like_red));
            } else {
                likesIcon.setImageResource(R.drawable.icon_like_blue);
                likes.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
        }
    }

    private MaterialDialog loadingDialog;
    private void getProblemIndexFromId(final int id) {
        if (BuildConfig.DEBUG) Log.d("LeetCoder", "Get problem index: " + id + " ing");
        loadingDialog = new MaterialDialog.Builder(this)
                .title(R.string.loading_data_title)
                .content(R.string.loading_data_content)
                .negativeText(R.string.loading_data_cancel)
                .cancelable(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.NEGATIVE) {
                            finish();
                        }
                    }
                })
                .show();
        BmobQuery<Problem_Index> queryProblemIndex = new BmobQuery<Problem_Index>();
        queryProblemIndex.addWhereEqualTo("id", id);
        queryProblemIndex.setLimit(1);
        queryProblemIndex.findObjects(LeetCoderApplication.getAppContext(), new FindListener<Problem_Index>() {
            @Override
            public void onSuccess(List<Problem_Index> object) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Get problem index: " + id + " " + object.get(0).getTitle());
                if (loadingDialog != null) loadingDialog.dismiss();
                problem_index = object.get(0);
                likes.setText(problem_index.getLike() + "");
                title.setText(problem_index.getTitle());
                getData();
            }
            @Override
            public void onError(int code, String msg) {
                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Get problem index failed: " + id);
                if (loadingDialog != null) loadingDialog.dismiss();
                LeetCoderUtil.showToast(mContext, R.string.loading_data_failed);
                finish();
            }
        });
    }

    private void getData() {

        loading = true;
        problem = new Problem();
        problem.setId(problem_index.getId());

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

        Fragment discussFragment = adapter.getPage(2);
        if (discussFragment != null) {
            if (discussFragment instanceof ProblemDiscussFragment) {
                ((ProblemDiscussFragment) discussFragment).setLoading();
            }
        }

        Fragment commentFragment = adapter.getPage(3);
        if (commentFragment != null) {
            if (commentFragment instanceof ProblemCommentFragment) {
                ((ProblemCommentFragment) commentFragment).setLoading();
            }
        }

        BmobQuery<Problem> query = new BmobQuery<>();
        query.addWhereEqualTo("id", problem.getId());
        query.setLimit(1);
        query.findObjects(mContext, new FindListener<Problem>() {
            @Override
            public void onSuccess(List<Problem> list) {
                loading = false;
                problem.setContent(list.get(0).getContent());
                problem.setSolution(list.get(0).getSolution());
                problem.setDiscussLink(list.get(0).getDiscussLink());
                problem.setProblemLink(list.get(0).getProblemLink());
                problem.setSimilarProblems(list.get(0).getSimilarProblems());
//                problem.show();

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

                Fragment discussFragment = adapter.getPage(2);
                if (discussFragment != null) {
                    if (discussFragment instanceof ProblemDiscussFragment) {
                        ((ProblemDiscussFragment) discussFragment).setDiscuss();
                    }
                }

                Fragment commentFragment = adapter.getPage(3);
                if (commentFragment != null) {
                    if (commentFragment instanceof ProblemCommentFragment) {
                        ((ProblemCommentFragment) commentFragment).setComment();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                loading = false;
                LeetCoderUtil.showToast(mContext, R.string.loading_failed);

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

                Fragment discussFragment = adapter.getPage(2);
                if (discussFragment != null) {
                    if (discussFragment instanceof ProblemDiscussFragment) {
                        ((ProblemDiscussFragment) discussFragment).setReload();
                    }
                }

                Fragment commentFragment = adapter.getPage(3);
                if (commentFragment != null) {
                    if (commentFragment instanceof ProblemCommentFragment) {
                        ((ProblemCommentFragment) commentFragment).setReload();
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
                ImageView icon = (ImageView)inflater.inflate(R.layout.item_tab_icon, container, false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LeetCoderUtil.getScreenWidth(mContext) / 4, LeetCoderUtil.dpToPx(56));
                icon.setLayoutParams(layoutParams);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_content));
                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_solution));
                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_discuss));
                        break;
                    case 3:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_comment));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return icon;
            }
        });
    }

    private View getIcon(int i) {
        switch (i) {
            case 0: return findViewById(R.id.content_layout);
            case 1: return findViewById(R.id.solution_layout);
            case 2: return findViewById(R.id.discuss_layout);
            case 3: return findViewById(R.id.comment_layout);
        }
        return null;
    }

    @Override
    public void reload() {
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.original_problem_icon:
                // view this problem in web
                if (loading) {
                    LeetCoderUtil.showToast(mContext, R.string.problem_is_loading);
                } else {
                    new FinestWebView.Builder(ProblemActivity.this)
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
                            .show(problem.getProblemLink());
                }
                break;
            case R.id.like_number_layout:
                // like
                if (LeetCoderApplication.user == null) {
                    LeetCoderUtil.showToast(mContext, R.string.like_not_login);
                } else {
                    if (LeetCoderApplication.user.getLikeProblems().contains(problem_index.getId())) {
                        // dislike
                        problem_index.setLike(problem_index.getLike() - 1);
                        problem_index.update(LeetCoderApplication.getAppContext(), problem_index.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                int index = LeetCoderApplication.user.getLikeProblems().indexOf(problem_index.getId());
                                LeetCoderApplication.user.getLikeProblems().remove(index);
                                LeetCoderApplication.user.update(LeetCoderApplication.getAppContext(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        LeetCoderUtil.showToast(mContext, R.string.like_dislike_successfully);
                                        likesIcon.setImageResource(R.drawable.icon_like_blue);
                                        likes.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                                        likes.setText(problem_index.getLike() + "");
                                    }
                                    @Override
                                    public void onFailure(int i, String s) {
                                        if (BuildConfig.DEBUG) Log.d("LeetCoder", "Dislike failed: " + s);
                                        LeetCoderApplication.user.getLikeProblems().add(problem_index.getId());
                                        LeetCoderUtil.showToast(mContext, R.string.like_dislike_failed);
                                    }
                                });
                            }
                            @Override
                            public void onFailure(int i, String s) {
                                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Dislike failed: " + s);
                                LeetCoderUtil.showToast(mContext, R.string.like_dislike_failed);
                                problem_index.setLike(problem_index.getLike() + 1);
                            }
                        });
                    } else {
                        // like
                        problem_index.setLike(problem_index.getLike() + 1);
                        problem_index.update(LeetCoderApplication.getAppContext(), problem_index.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                LeetCoderApplication.user.getLikeProblems().add(problem_index.getId());
                                LeetCoderApplication.user.update(LeetCoderApplication.getAppContext(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        LeetCoderUtil.showToast(mContext, R.string.like_like_successfully);
                                        likesIcon.setImageResource(R.drawable.icon_like_red);
                                        likes.setTextColor(ContextCompat.getColor(mContext, R.color.like_red));
                                        likes.setText(problem_index.getLike() + "");
                                    }
                                    @Override
                                    public void onFailure(int i, String s) {
                                        if (BuildConfig.DEBUG) Log.d("LeetCoder", "Like failed: " + s);
                                        int index = LeetCoderApplication.user.getLikeProblems().indexOf(problem_index.getId());
                                        LeetCoderApplication.user.getLikeProblems().remove(index);
                                        LeetCoderUtil.showToast(mContext, R.string.like_like_failed);
                                    }
                                });
                            }
                            @Override
                            public void onFailure(int i, String s) {
                                if (BuildConfig.DEBUG) Log.d("LeetCoder", "Like failed: " + s);
                                LeetCoderUtil.showToast(mContext, R.string.like_like_failed);
                                problem_index.setLike(problem_index.getLike() - 1);
                            }
                        });
                    }
                }
                break;
            case R.id.solution_copy_icon:
                // copy solution
                if (loading) {
                    LeetCoderUtil.showToast(mContext, R.string.problem_is_loading);
                } else {
                    ClipboardManager clipboard = (ClipboardManager)getSystemService(Activity.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(mContext.getResources().getString(R.string.solution_copied), problem.getSolution());
                    clipboard.setPrimaryClip(clip);
                    LeetCoderUtil.showToast(mContext, R.string.solution_copied);
                }
                break;
            case R.id.solution_bug_icon:
                // bug of solution
                new MaterialDialog.Builder(mContext)
                        .title(R.string.feedback_title)
                        .items(R.array.feedback_types)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which == 4) {
                                    inputFeedback();
                                } else if (which == 3) {
                                    new MaterialDialog.Builder(mContext)
                                            .title(R.string.better_solution_title)
                                            .content(R.string.better_solution_content)
                                            .positiveText(R.string.better_solution_write)
                                            .negativeText(R.string.better_solution_copy)
                                            .neutralText(R.string.cancel)
                                            .forceStacking(true)
                                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    if (which == DialogAction.POSITIVE) {
                                                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                        emailIntent.setType("plain/text");
                                                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Nightonke@outlook.com"});
                                                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.better_solution_title_for) + problem_index.getTitle());
                                                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                                                        mContext.startActivity(Intent.createChooser(emailIntent, mContext.getResources().getString(R.string.better_solution_email_title)));
                                                    } else if (which == DialogAction.NEGATIVE) {
                                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                                        ClipData clip = ClipData.newPlainText("Email address copied.", "Nightonke@outlook.com");
                                                        clipboard.setPrimaryClip(clip);
                                                        LeetCoderUtil.showToast(mContext, R.string.better_solution_copied);
                                                    }
                                                }
                                            })
                                            .show();
                                } else {
                                    ProblemBug problemBug = new ProblemBug();
                                    problemBug.setId(problem.getId());
                                    problemBug.setContent(mContext.getResources().getStringArray(R.array.feedback_types)[which]);
                                    problemBug.save(LeetCoderApplication.getAppContext(), new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            LeetCoderUtil.showToast(mContext, R.string.feedback_send_successfully);
                                        }
                                        @Override
                                        public void onFailure(int code, String arg0) {
                                            LeetCoderUtil.showToast(mContext, R.string.feedback_send_failed);
                                        }
                                    });
                                }
                                dialog.dismiss();
                                return true;
                            }
                        })
                        .negativeText(R.string.cancel)
                        .show();
                break;
            case R.id.discuss_sort_icon:
                // sort discuss
                final Fragment discussFragment = adapter.getPage(2);
                if (discussFragment != null) {
                    if (discussFragment instanceof ProblemDiscussFragment) {
                        new MaterialDialog.Builder(mContext)
                                .title(R.string.sort_title)
                                .items(R.array.sort_types_discuss)
                                .itemsCallbackSingleChoice(((ProblemDiscussFragment) discussFragment).sortType, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        ((ProblemDiscussFragment) discussFragment).sort(which);
                                        LeetCoderUtil.showToast(mContext, R.string.sorting);
                                        dialog.dismiss();
                                        return true;
                                    }
                                })
                                .negativeText(R.string.cancel)
                                .show();
                    }
                }
                break;
            case R.id.comment_sort_icon:
                // sort comment
                final Fragment commentFragment = adapter.getPage(3);
                if (commentFragment != null) {
                    if (commentFragment instanceof ProblemCommentFragment) {
                        if (((ProblemCommentFragment) commentFragment).isLoading()) {
                            LeetCoderUtil.showToast(mContext, R.string.problem_is_loading);
                        } else {
                            new MaterialDialog.Builder(mContext)
                                    .title(R.string.sort_title)
                                    .items(R.array.sort_types_comment)
                                    .itemsCallbackSingleChoice(((ProblemCommentFragment) commentFragment).sortType, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            ((ProblemCommentFragment) commentFragment).sort(which);
                                            LeetCoderUtil.showToast(mContext, R.string.sorting);
                                            dialog.dismiss();
                                            return true;
                                        }
                                    })
                                    .negativeText(R.string.cancel)
                                    .show();
                        }

                    }
                }
                break;
            case R.id.comment_message_icon:
                // new comment
                if (LeetCoderApplication.user == null) {
                    LeetCoderUtil.showToast(mContext, R.string.comment_not_login);
                } else {
                    Intent intent = new Intent(mContext, EditCommentActivity.class);
                    intent.putExtra("id", problem_index.getId());
                    intent.putExtra("title", "");
                    intent.putExtra("content", "");
                    startActivityForResult(intent, START_COMMENT);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case START_COMMENT:
                if (resultCode == BACK_COMMENT_UNCHANGED) {
                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Comment unchanged");
                } else if (resultCode == BACK_COMMENT_CHANGED) {
                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Comment changed");
                    Fragment commentFragment = adapter.getPage(3);
                    if (commentFragment != null) {
                        if (commentFragment instanceof ProblemCommentFragment) {
                            ((ProblemCommentFragment) commentFragment).setComment();
                        }
                    }
                }
                break;
        }
    }

    private MaterialDialog inputDialog;
    private void inputFeedback() {
        final int min = 1;
        final int max = 400;
        inputDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.feedback_title)
                .negativeText(R.string.cancel)
                .positiveText(R.string.ok)
                .content("")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(mContext.getResources().getString(R.string.feedback_hint), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        int count = LeetCoderUtil.textCounter(String.valueOf(String.valueOf(input)));
                        dialog.setContent(
                                LeetCoderUtil.getDialogContent(mContext,
                                        "",
                                        count + "/" + min + "-" + max,
                                        (min <= count && count <= max)));
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        if (!(min <= count && count <= max)) {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        }
                    }
                })
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        if (dialogAction == DialogAction.POSITIVE) {
                            // send
                            ProblemBug problemBug = new ProblemBug();
                            problemBug.setId(problem.getId());
                            problemBug.setContent(materialDialog.getInputEditText().getText().toString());
                            problemBug.save(LeetCoderApplication.getAppContext(), new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    LeetCoderUtil.showToast(mContext, R.string.feedback_send_successfully);
                                }
                                @Override
                                public void onFailure(int code, String arg0) {
                                    LeetCoderUtil.showToast(mContext, R.string.feedback_send_failed);
                                }
                            });
                        }
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        int count = LeetCoderUtil.textCounter(String.valueOf(""));
                        inputDialog.setContent(
                                LeetCoderUtil.getDialogContent(mContext,
                                        "",
                                        count + "/" + min + "-" + max,
                                        (min <= count && count <= max)));
                        inputDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        if (!(min <= count && count <= max)) {
                            inputDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        }
                    }
                })
                .alwaysCallInputCallback()
                .show();
    }

    private Problem_Index target = null;
    @Override
    public void onSimilarProblemClick(final String similarProblem) {
        final String titleString = similarProblem.substring(5);
        target = null;
        if (LeetCoderApplication.categories == null || LeetCoderApplication.categoriesTag == null) {

        } else {
            for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
                for (Problem_Index problemIndex : category) {
                    if (problemIndex.getTitle().equals(titleString)) {
                        target = problemIndex.clone();
                        break;
                    }
                }
                if (target != null) break;
            }
        }
        final Snackbar snackbar = Snackbar.make(snackbarLayout, "", Snackbar.LENGTH_LONG);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        // Hide the text
        layout.findViewById(android.support.design.R.id.snackbar_text).setVisibility(View.INVISIBLE);
        layout.findViewById(android.support.design.R.id.snackbar_action).setVisibility(View.GONE);

        // Inflate our custom view
        View snackView = getLayoutInflater().inflate(R.layout.snackbar_similar_problem, null);
        // Configure the view
        AutofitTextView problemTitle = (AutofitTextView) snackView.findViewById(R.id.title);
        problemTitle.setText(titleString);
        problemTitle.setSelected(true);
        ImageView view = (ImageView) snackView.findViewById(R.id.view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                problem_index = target;
                likes.setText(problem_index.getLike() + "");
                title.setText(problem_index.getTitle());
                setLikes();
                getData();
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                snackbar.dismiss();
                problem_index = target;
                likes.setText(problem_index.getLike() + "");
                title.setText(problem_index.getTitle());
                setLikes();
                getData();
                return true;
            }
        });
        if (target == null) view.setVisibility(View.GONE);

        layout.addView(snackView, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.height = LeetCoderUtil.dpToPx(76);
        snackView.setLayoutParams(layoutParams);
        snackbar.show();
    }

    @Override
    public void onTagClick(final String tag) {
        int position = 0;
        int targetPosition = -1;
        if (LeetCoderApplication.categories == null || LeetCoderApplication.categoriesTag == null) {

        } else {
            for (String t : LeetCoderApplication.categoriesTag) {
                if (t.equals(tag)) {
                    targetPosition = position;
                    break;
                }
                position++;
            }
        }
        final Snackbar snackbar = Snackbar.make(snackbarLayout, "", Snackbar.LENGTH_LONG);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        // Hide the text
        layout.findViewById(android.support.design.R.id.snackbar_text).setVisibility(View.INVISIBLE);
        layout.findViewById(android.support.design.R.id.snackbar_action).setVisibility(View.GONE);

        // Inflate our custom view
        View snackView = getLayoutInflater().inflate(R.layout.snackbar_tag, null);
        // Configure the view
        AutofitTextView problemTitle = (AutofitTextView) snackView.findViewById(R.id.title);
        problemTitle.setText(tag);
        problemTitle.setSelected(true);
        ImageView view = (ImageView) snackView.findViewById(R.id.view);
        final int finalTargetPosition = targetPosition;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("category", finalTargetPosition);
                setResult(MainActivity.BACK_CATEGORY, resultIntent);
                finish();
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                snackbar.dismiss();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("category", finalTargetPosition);
                setResult(MainActivity.BACK_CATEGORY, resultIntent);
                finish();
                return true;
            }
        });
        if (targetPosition == -1) view.setVisibility(View.GONE);

        layout.addView(snackView, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.height = LeetCoderUtil.dpToPx(76);
        snackView.setLayoutParams(layoutParams);
        snackbar.show();
    }
}
