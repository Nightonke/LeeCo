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
        Bmob.initialize(this, "9a4bdd4236a81824ac48ce7296ecd186");
    }

    public static Context getAppContext() {
        return LeetCoderApplication.mContext;
    }

}
