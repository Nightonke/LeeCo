package com.nightonke.leetcoder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;

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







    private static LeetCoderUtil ourInstance = new LeetCoderUtil();

    public static LeetCoderUtil getInstance() {
        return ourInstance;
    }

    private LeetCoderUtil() {
    }
}
