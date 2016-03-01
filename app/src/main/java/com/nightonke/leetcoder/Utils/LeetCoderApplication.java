package com.nightonke.leetcoder.Utils;

import android.app.Application;
import android.content.Context;

import com.nightonke.leetcoder.Model.Problem_Index;
import com.nightonke.leetcoder.Model.User;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;

/**
 * Created by Weiping on 2016/1/10.
 */
public class LeetCoderApplication extends Application {

    // version number, just for update
    public static final int VERSION = 100;

    public static final int KEYBOARD_CULT_DELAY = 400;

    public static final int MIN_COMMENT_TITLE_LENGTH = 5;
    public static final int MAX_COMMENT_TITLE_LENGTH = 100;
    public static final int MIN_COMMENT_LENGTH = 10;
    public static final int MAX_COMMENT_LENGTH = 1000;
    public static final int MIN_FEEDBACK_TITLE_LENGTH = 5;
    public static final int MAX_FEEDBACK_TITLE_LENGTH = 100;
    public static final int MIN_FEEDBACK_LENGTH = 10;
    public static final int MAX_FEEDBACK_LENGTH = 500;

    private static Context mContext;

    public static ArrayList<ArrayList<Problem_Index>> categories = null;
    public static ArrayList<String> categoriesTag = null;

    public static User user = null;
    public static ArrayList<Integer> likes = null;
    public static ArrayList<String> comments = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Bmob.initialize(this, Key.BMOB_KEY);
        CrashReport.initCrashReport(getApplicationContext(), Key.BUGLY_APP_ID, false);
    }

    public static Context getAppContext() {
        return LeetCoderApplication.mContext;
    }

}
