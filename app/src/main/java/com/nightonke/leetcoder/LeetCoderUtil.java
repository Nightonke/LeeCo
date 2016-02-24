package com.nightonke.leetcoder;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by Weiping on 2016/2/24.
 */
public class LeetCoderUtil {

    public static int textCounter(String s) {
        int counter = 0;
        for (char c : s.toCharArray()) {
            if (c < 128) {
                counter++;
            } else {
                counter += 2;
            }
        }
        return counter;
    }

    public static Spannable getDialogContent(Context mContext, String pre, String post, boolean countValid) {
        int i0 = 0, i1 = pre.length(), i2 = i1 + post.length();
        Spannable spannable = new SpannableString(pre + post);
        spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), i0, i1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!countValid) {
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.error_red)), i0, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.error_red)), i0, i1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorPrimary)), i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static void setStatusBarColor(Context mContext) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((AppCompatActivity)mContext).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }

    public static TextView getActionBarTextView(Toolbar mToolBar) {
        TextView titleTextView = null;
        try {
            Field f = mToolBar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(mToolBar);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return titleTextView;
    }

    public static String getTextDrawableString(String string) {
        int blankPosition = string.indexOf(" ");
        if (blankPosition == -1 || blankPosition == string.length() - 1) {
            if (string.length() == 1) return string.toUpperCase();
            else return string.substring(0, 2).toUpperCase();
        } else {
            return string.substring(0, 1).toUpperCase() + string.substring(blankPosition + 1, blankPosition + 2).toUpperCase();
        }
    }







    private static LeetCoderUtil ourInstance = new LeetCoderUtil();

    public static LeetCoderUtil getInstance() {
        return ourInstance;
    }

    private LeetCoderUtil() {
    }
}
