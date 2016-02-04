package com.nightonke.leetcoder;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;

/**
 * Created by Weiping on 2016/1/10.
 */
public class LeetCoderApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Bmob.initialize(this, BmobKey.BMOB_KEY);
    }

    public static Context getAppContext() {
        return LeetCoderApplication.mContext;
    }

}
