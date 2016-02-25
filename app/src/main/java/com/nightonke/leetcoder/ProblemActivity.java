package com.nightonke.leetcoder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


public class ProblemActivity extends AppCompatActivity
        implements
        ProblemContentFragment.ReloadListener,
        View.OnClickListener {

    public Problem_Index problem_index;
    public Problem problem;

    private Context mContext;

    private int lastPagerPosition = 0;
    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;
    private FragmentPagerItemAdapter adapter;

    private TextView title;
    private FrameLayout icon;
    private ImageView contentImageView;
    private ImageView solutionImageView;
    private ImageView discussImageView;
    private ImageView commentImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext);

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
                    YoYo.with(Techniques.FadeOutUp)
                            .duration(300)
                            .playOn(getIcon(lastPagerPosition));
                } else if (position < lastPagerPosition) {
                    YoYo.with(Techniques.BounceInDown)
                            .duration(500)
                            .playOn(getIcon(position));
                    YoYo.with(Techniques.FadeOutDown)
                            .duration(300)
                            .playOn(getIcon(lastPagerPosition));
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

        icon = (FrameLayout)findViewById(R.id.icon);
        icon.setOnClickListener(this);
        contentImageView = (ImageView)findViewById(R.id.content_icon);
        solutionImageView = (ImageView)findViewById(R.id.solution_icon);
        solutionImageView.setVisibility(View.INVISIBLE);
        discussImageView = (ImageView)findViewById(R.id.discuss_icon);
        discussImageView.setVisibility(View.INVISIBLE);
        commentImageView = (ImageView)findViewById(R.id.comment_icon);
        commentImageView.setVisibility(View.INVISIBLE);

        if (getIntent().getIntExtra("categoryPosition", -1) == -1 && getIntent().getIntExtra("problemPosition", -1) == -1) {
            // from search result
            int id = getIntent().getIntExtra("id", -1);
            for (ArrayList<Problem_Index> category : LeetCoderApplication.categories) {
                for (Problem_Index problemIndex : category) {
                    if (id == problemIndex.getId()) {
                        problem_index = problemIndex;
                    }
                }
            }
        } else {
            // from categories
            problem_index = LeetCoderApplication.categories.
                    get(getIntent().getIntExtra("categoryPosition", -1)).
                    get(getIntent().getIntExtra("problemPosition", -1));
        }

        title.setText(problem_index.getTitle());
        getData();
    }

    private void getData() {
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

        Fragment discussFragment = adapter.getPage(2);
        if (discussFragment != null) {
            if (discussFragment instanceof ProblemDiscussFragment) {
                ((ProblemDiscussFragment) discussFragment).setLoading();
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

                Fragment discussFragment = adapter.getPage(2);
                if (discussFragment != null) {
                    if (discussFragment instanceof ProblemDiscussFragment) {
                        ((ProblemDiscussFragment) discussFragment).setDiscuss();
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

                Fragment discussFragment = adapter.getPage(2);
                if (discussFragment != null) {
                    if (discussFragment instanceof ProblemDiscussFragment) {
                        ((ProblemDiscussFragment) discussFragment).setReload();
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
            case 0: return contentImageView;
            case 1: return solutionImageView;
            case 2: return discussImageView;
            case 3: return commentImageView;
        }
        return null;
    }

    @Override
    public void reload() {
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (R.id.icon) {
            case R.id.icon:
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        // like
                        break;
                    case 1:
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
                                                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Better Solution For " + problem_index.getTitle());
                                                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                                                                mContext.startActivity(Intent.createChooser(emailIntent, mContext.getResources().getString(R.string.better_solution_email_title)));
                                                            } else if (which == DialogAction.NEGATIVE) {
                                                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                                                ClipData clip = ClipData.newPlainText("Email address copied.", "Nightonke@outlook.com");
                                                                clipboard.setPrimaryClip(clip);
                                                                Toast.makeText(mContext, R.string.better_solution_copied, Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(mContext, R.string.feedback_send_successfully, Toast.LENGTH_SHORT).show();
                                                }
                                                @Override
                                                public void onFailure(int code, String arg0) {
                                                    Toast.makeText(mContext, R.string.feedback_send_failed, Toast.LENGTH_SHORT).show();
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
                    case 2:
                        // sort
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
                                                Toast.makeText(mContext, "Sorting...", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                return true;
                                            }
                                        })
                                        .negativeText(R.string.cancel)
                                        .show();
                            }
                        }
                        break;
                    case 3:
                        // add comment
                        break;
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
                                    Toast.makeText(mContext, R.string.feedback_send_successfully, Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(int code, String arg0) {
                                    Toast.makeText(mContext, R.string.feedback_send_failed, Toast.LENGTH_SHORT).show();
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
}
