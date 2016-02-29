package com.nightonke.leetcoder.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ppamorim.cult.CultView;
import com.nightonke.leetcoder.Utils.AppUpdateManager;
import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.Utils.LeetCoderApplication;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;
import com.nightonke.leetcoder.R;
import com.nightonke.leetcoder.Model.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

public class SettingsActivity extends AppCompatActivity
    implements View.OnClickListener {

    private CultView cultView;
    private Context mContext;

    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mContext = this;
        LeetCoderUtil.setStatusBarColor(mContext, R.color.colorPrimary);

        cultView = (CultView)findViewById(R.id.cult_view);
        ((AppCompatActivity)mContext).setSupportActionBar(cultView.getInnerToolbar());
        cultView.getInnerToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        cultView.getOutToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        (LeetCoderUtil.getActionBarTextView(cultView.getInnerToolbar())).setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        ActionBar actionBar = ((AppCompatActivity)mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mContext.getResources().getString(R.string.settings_activity_title));
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(mContext, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }

        findViewById(R.id.change_user_name_layout).setOnClickListener(this);
        findViewById(R.id.change_password_layout).setOnClickListener(this);
        findViewById(R.id.check_update_layout).setOnClickListener(this);

        appUpdateManager = new AppUpdateManager(mContext);
        if (appUpdateManager.getCanBeUpdated()) findViewById(R.id.newer_version).setVisibility(View.VISIBLE);
        else findViewById(R.id.newer_version).setVisibility(View.GONE);
    }

    private MaterialDialog changeUserNameDialog;
    private View changeUserNameView;
    private MaterialDialog authenticationDialog;
    private View authenticationView;
    private MaterialDialog changePasswordDialog;
    private View changePasswordView;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_user_name_layout:
                // change user name
                if (LeetCoderApplication.user == null) {
                    LeetCoderUtil.showToast(mContext, R.string.change_user_name_not_login);
                } else {
                    changeUserNameDialog = new MaterialDialog.Builder(mContext)
                            .title(R.string.change_user_name_title)
                            .customView(R.layout.dialog_change_user_name, false)
                            .positiveText(R.string.change_user_name_ok)
                            .negativeText(R.string.change_user_name_cancel)
                            .autoDismiss(false)
                            .showListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    EditText oldUserName = (EditText)changeUserNameView.findViewById(R.id.old_user_name);
                                    oldUserName.setText(LeetCoderApplication.user.getUsername());
                                    oldUserName.getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    oldUserName.setKeyListener(null);
                                    EditText newUserName = (EditText)changeUserNameView.findViewById(R.id.new_user_name);
                                    newUserName.getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    showKeyboard(newUserName);
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
                                public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.POSITIVE) {
                                        // change
                                        final String userName = ((EditText)changeUserNameView.findViewById(R.id.new_user_name)).getText().toString();
                                        if ("".equals(userName)) {
                                            LeetCoderUtil.showToast(mContext, R.string.change_user_name_empty_user_name);
                                        } else {
                                            LeetCoderUtil.showToast(mContext, R.string.change_user_name_changing);
                                            User newUser = new User();
                                            newUser.setUsername(userName);
                                            User currentUser = BmobUser.getCurrentUser(LeetCoderApplication.getAppContext(), User.class);
                                            newUser.update(LeetCoderApplication.getAppContext(), currentUser.getObjectId(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Change user name successfully: " + userName);
                                                    LeetCoderUtil.showToast(mContext, R.string.change_user_name_successfully);
                                                    LeetCoderApplication.user.setUsername(userName);
                                                    dialog.dismiss();
                                                }
                                                @Override
                                                public void onFailure(int code, String msg) {
                                                    if (BuildConfig.DEBUG) Log.d("LeetCoder", "Change user name failed: " + userName + " " + msg);
                                                    if (msg.charAt(0) == 'u') {
                                                        LeetCoderUtil.showToast(mContext, R.string.change_user_name_repeat);
                                                    } else {
                                                        LeetCoderUtil.showToast(mContext, R.string.change_user_name_failed);
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        dialog.dismiss();
                                    }
                                }
                            })
                            .show();
                    changeUserNameView = changeUserNameDialog.getCustomView();
                }
                break;
            case R.id.change_password_layout:
                // change password
                if (LeetCoderApplication.user == null) {
                    LeetCoderUtil.showToast(mContext, R.string.change_user_name_not_login);
                } else {
                    authenticationDialog = new MaterialDialog.Builder(mContext)
                            .title(R.string.change_password_title_1)
                            .customView(R.layout.dialog_authentication, false)
                            .positiveText(R.string.change_password_ok_1)
                            .negativeText(R.string.change_password_cancel_1)
                            .autoDismiss(false)
                            .showListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    EditText oldPassword = (EditText)authenticationView.findViewById(R.id.password);
                                    oldPassword.getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    showKeyboard(oldPassword);
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
                                public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.POSITIVE) {
                                        String password = ((EditText)authenticationView.findViewById(R.id.password)).getText().toString();
                                        if ("".equals(password)) {
                                            LeetCoderUtil.showToast(mContext, R.string.change_password_empty_old_password);
                                        } else if (!LeetCoderApplication.user.getMyPassword().equals(password)) {
                                            LeetCoderUtil.showToast(mContext, R.string.change_password_incorrect);
                                        } else {
                                            dialog.dismiss();
                                            changePasswordDialog = new MaterialDialog.Builder(mContext)
                                                    .title(R.string.change_password_title_2)
                                                    .customView(R.layout.dialog_change_password, false)
                                                    .positiveText(R.string.change_password_ok_2)
                                                    .negativeText(R.string.change_password_cancel_2)
                                                    .autoDismiss(false)
                                                    .showListener(new DialogInterface.OnShowListener() {
                                                        @Override
                                                        public void onShow(DialogInterface dialog) {
                                                            EditText newPassword = (EditText)changePasswordView.findViewById(R.id.password);
                                                            newPassword.getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                                            showKeyboard(newPassword);
                                                            EditText newPasswordAgain = (EditText)changePasswordView.findViewById(R.id.password_again);
                                                            newPasswordAgain.getBackground().mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
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
                                                        public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                                            if (which == DialogAction.POSITIVE) {
                                                                final String password = ((EditText)changePasswordView.findViewById(R.id.password)).getText().toString();
                                                                String passwordAgain = ((EditText)changePasswordView.findViewById(R.id.password_again)).getText().toString();
                                                                if ("".equals(password)) {
                                                                    LeetCoderUtil.showToast(mContext, R.string.change_password_empty_password);
                                                                } else if ("".equals(passwordAgain)) {
                                                                    LeetCoderUtil.showToast(mContext, R.string.change_password_empty_password_again);
                                                                } else if (!passwordAgain.equals(password)) {
                                                                    LeetCoderUtil.showToast(mContext, R.string.change_password_different_password);
                                                                } else {
                                                                    LeetCoderUtil.showToast(mContext, R.string.change_password_changing);
                                                                    final String oldPassword = LeetCoderApplication.user.getMyPassword();
                                                                    LeetCoderApplication.user.setPassword(password);
                                                                    LeetCoderApplication.user.setMyPassword(password);
                                                                    LeetCoderApplication.user.update(LeetCoderApplication.getAppContext(), new UpdateListener() {
                                                                        @Override
                                                                        public void onSuccess() {
                                                                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "Change password successfully: " + password);
                                                                            LeetCoderUtil.showToast(mContext, R.string.change_password_successfully);
                                                                            LeetCoderApplication.user.setPassword(oldPassword);
                                                                            LeetCoderApplication.user.setMyPassword(oldPassword);
                                                                            dialog.dismiss();
                                                                        }
                                                                        @Override
                                                                        public void onFailure(int code, String msg) {
                                                                            if (BuildConfig.DEBUG) Log.d("LeetCoder", "Change password failed: " + password + " " + msg);
                                                                            LeetCoderUtil.showToast(mContext, R.string.change_password_failed);
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                    })
                                                    .show();
                                            changePasswordView = changePasswordDialog.getCustomView();
                                        }
                                    } else {
                                        dialog.dismiss();
                                    }
                                }
                            })
                            .show();
                    authenticationView = authenticationDialog.getCustomView();
                }
                break;
            case R.id.check_update_layout:
                // check update
                appUpdateManager.checkUpdateInfo(true);
                break;
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
