package com.nightonke.leetcoder;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;

/**
 * Created by Weiping on 2016/1/10.
 */
public class LeetCoderApplication extends Application {

    public static final int KEYBOARD_CULT_DELAY = 400;

    public static final int MIN_COMMENT_TITLE_LENGTH = 5;
    public static final int MAX_COMMENT_TITLE_LENGTH = 100;
    public static final int MIN_COMMENT_LENGTH = 10;
    public static final int MAX_COMMENT_LENGTH = 1000;

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
        Bmob.initialize(this, BmobKey.BMOB_KEY);
        LeakCanary.install(this);
    }

    public static Context getAppContext() {
        return LeetCoderApplication.mContext;
    }

}
