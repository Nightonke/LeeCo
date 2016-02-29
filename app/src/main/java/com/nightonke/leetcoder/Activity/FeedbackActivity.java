package com.nightonke.leetcoder.Activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ppamorim.cult.CultView;
import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.UI.DoubleClickListener;
import com.nightonke.leetcoder.Model.Feedback;
import com.nightonke.leetcoder.Utils.LeetCoderApplication;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.R;

import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends AppCompatActivity {

    private CultView cultView;

    private ScrollView scrollView;
    private EditText title;
    private EditText content;
    private TextView help;
    private TextView number;

    private Context mContext;

    private boolean titleExceed = false;
    private boolean contentExceed = false;

    private boolean sent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext, R.color.colorPrimary);

        cultView = (CultView)findViewById(R.id.cult_view);
        ((AppCompatActivity)mContext).setSupportActionBar(cultView.getInnerToolbar());
        cultView.getInnerToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        cultView.getOutToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        (LeetCoderUtil.getActionBarTextView(cultView.getInnerToolbar())).setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        ActionBar actionBar = ((AppCompatActivity)mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mContext.getResources().getString(R.string.feedback_activity_title));
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(mContext, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }

        scrollView = (ScrollView)findViewById(R.id.scrollView);
        title = (EditText) findViewById(R.id.title);
        content = (EditText)findViewById(R.id.edittext);
        help = (TextView)findViewById(R.id.helper);
        number = (TextView)findViewById(R.id.number);

        content.setSelection(content.getText().toString().length());

        setContentNumberText();

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTitleNumberText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        scrollView.fullScroll(View.FOCUS_DOWN);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setContentNumberText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        });

        cultView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });

        showKeyboard(content);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_comment, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (findViewById(R.id.action_send) != null) {
                    findViewById(R.id.action_send).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            send();
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
                onBackPressed();
                return true;
            case R.id.action_send:
                send();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    private void send() {
        if ("".equals(title.getText().toString())) {
            LeetCoderUtil.showToast(mContext, R.string.feedback_not_title);
        } else if (titleExceed) {
            LeetCoderUtil.showToast(mContext, R.string.feedback_invalid_title);
        } else if ("".equals(content.getText().toString())) {
            LeetCoderUtil.showToast(mContext, R.string.feedback_not_content);
        } else if (contentExceed) {
            LeetCoderUtil.showToast(mContext, R.string.feedback_invalid_content);
        } else {
            final Feedback feedback = new Feedback();
            feedback.setTitle(title.getText().toString());
            feedback.setContent(content.getText().toString());
            feedback.save(LeetCoderApplication.getAppContext(), new SaveListener() {
                @Override
                public void onSuccess() {
                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Send feedback: " + feedback.getTitle() + " " + feedback.getContent());
                    LeetCoderUtil.showToast(mContext, R.string.feedback_send_successfully_2);
                    sent = true;
                    quit();
                }
                @Override
                public void onFailure(int i, String s) {
                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Send feedback failed: " + s);
                    LeetCoderUtil.showToast(mContext, R.string.feedback_send_failed_2);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        quit();
    }

    private void quit() {
        if ("".equals(title.getText().toString()) && "".equals(content.getText().toString())) {
            finish();
        } else {
            if (sent) {
                finish();
            } else {
                new MaterialDialog.Builder(mContext)
                        .title(R.string.feedback_not_send_title)
                        .content(R.string.feedback_not_send_content)
                        .positiveText(R.string.feedback_not_send_quit)
                        .negativeText(R.string.feedback_not_send_send)
                        .neutralText(R.string.feedback_not_send_cancel)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                if (dialogAction == DialogAction.POSITIVE) {
                                    finish();
                                } else if (dialogAction == DialogAction.NEGATIVE) {
                                    send();
                                }
                            }
                        })
                        .show();
            }
        }
    }

    private void setTitleNumberText() {
        int count = LeetCoderUtil.textCounter(title.getText().toString());
        if (LeetCoderApplication.MIN_FEEDBACK_TITLE_LENGTH <= count && count <= LeetCoderApplication.MAX_FEEDBACK_TITLE_LENGTH) {
            titleExceed = false;
        } else {
            titleExceed = true;
        }
    }

    private void setContentNumberText() {
        int count = LeetCoderUtil.textCounter(content.getText().toString());
        number.setText(count + "/" + LeetCoderApplication.MIN_FEEDBACK_LENGTH + "-" + LeetCoderApplication.MAX_FEEDBACK_LENGTH);
        if (LeetCoderApplication.MIN_FEEDBACK_LENGTH <= count && count <= LeetCoderApplication.MAX_FEEDBACK_LENGTH) {
            number.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            contentExceed = false;
        } else {
            number.setTextColor(ContextCompat.getColor(mContext, R.color.error_red));
            contentExceed = true;
        }
    }
}