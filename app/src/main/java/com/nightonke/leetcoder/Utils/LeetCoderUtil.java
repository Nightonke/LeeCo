package com.nightonke.leetcoder.Utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.aakira.expandablelayout.Utils;
import com.github.johnpersano.supertoasts.SuperToast;
import com.nightonke.leetcoder.Model.Problem_Index;
import com.nightonke.leetcoder.R;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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

    public static void setStatusBarColor(Context mContext, int id) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((AppCompatActivity)mContext).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, id));
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
        string = deleteI(string);
        int blankPosition = string.indexOf(" ");
        if (blankPosition == -1 || blankPosition == string.length() - 1) {
            if (string.length() == 1) return string.toUpperCase();
            else {
                char first = getFirstLetter(string);
                char second = getSecondLetter(string);
                if (second == ' ') return String.valueOf(first).toUpperCase();
                else return String.valueOf(first).toUpperCase() + String.valueOf(second).toUpperCase();
            }
        } else {
            char first = getFirstLetter(string);
            char second = getFirstLetter(string.substring(blankPosition + 1));
            if (second == ' ') return String.valueOf(first).toUpperCase();
            else return String.valueOf(first).toUpperCase() + String.valueOf(second).toUpperCase();
        }
    }

    public static String deleteI(String string) {
        boolean isAllI = true;
        int blankPosition = string.lastIndexOf(" ");
        for (int i = blankPosition + 1; i < string.length(); i++) {
            if (string.charAt(i) != 'I') {
                isAllI = false;
                break;
            }
        }
        if (isAllI) return string.substring(0, blankPosition);
        else return string;

    }

    public static char getFirstLetter(String string) {
        for (char c : string.toCharArray()) {
            if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) return c;
        }
        return ' ';
    }

    public static char getSecondLetter(String string) {
        boolean isFirst = true;
        for (char c : string.toCharArray()) {
            if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) {
                if (isFirst) isFirst = false;
                else return c;
            }
        }
        return ' ';
    }

    public static int GetRandomColor() {
        Random random = new Random();
        int p = random.nextInt(Colors.length);
        while (Colors[p].equals(lastColor0)
                || Colors[p].equals(lastColor1)
                || Colors[p].equals(lastColor2)) {
            p = random.nextInt(Colors.length);
        }
        lastColor0 = lastColor1;
        lastColor1 = lastColor2;
        lastColor2 = Colors[p];
        return Color.parseColor(Colors[p]);
    }

    public static int getScreenWidth(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = LeetCoderApplication.getAppContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static void sortProblemSearchResult(ArrayList<Problem_Index> problemIndices) {
        Collections.sort(problemIndices, new Comparator<Problem_Index>() {
            @Override
            public int compare(Problem_Index lhs, Problem_Index rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
    }

    public static void showToast(Context context, String text, int color) {
        SuperToast.cancelAllSuperToasts();
        SuperToast superToast = new SuperToast(context);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.SHORT);
        superToast.setTextColor(Color.parseColor("#ffffff"));
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setText(text);
        superToast.setBackground(color);
        superToast.show();
    }

    private static String lastToast = "";
    public static void showToast(Context context, String text) {
        if (context == null) return;
        if (lastToast.equals(text)) {
            SuperToast.cancelAllSuperToasts();
        } else {
            lastToast = text;
        }
        SuperToast superToast = new SuperToast(context);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.VERY_SHORT);
        superToast.setTextColor(Color.parseColor("#ffffff"));
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setText(text);
        superToast.setBackground(SuperToast.Background.BLUE);
        superToast.show();
    }

    public static void showToast(Context context, int textId) {
        String text = context.getResources().getString(textId);
        if (context == null) return;
        if (lastToast.equals(text)) {
            SuperToast.cancelAllSuperToasts();
        } else {
            lastToast = text;
        }
        SuperToast superToast = new SuperToast(context);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.VERY_SHORT);
        superToast.setTextColor(Color.parseColor("#ffffff"));
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setText(text);
        superToast.setBackground(SuperToast.Background.BLUE);
        superToast.show();
    }

    public static Drawable getDrawable(int id, int width, int height) {
        Drawable dr = ContextCompat.getDrawable(LeetCoderApplication.getAppContext(), id);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        return new BitmapDrawable(LeetCoderApplication.getAppContext().getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));
    }

    private static final String IMAGE_START_STRING = "<img src=\"";
    public static String getReadyContent(String string) {
        int position = 0;
        while(position != -1) {
            position = string.indexOf(IMAGE_START_STRING, position + 1);
            if (position != -1) {
                if (!"http".equals(string.substring(position + IMAGE_START_STRING.length(), position + IMAGE_START_STRING.length() + 4))) {
                    // this is an image in leetcode
                    string = string.substring(0, position + IMAGE_START_STRING.length())
                           + "http://leetcode.com"
                           + string.substring(position + IMAGE_START_STRING.length());
                }
            }
        }
        return string;
    }

    public static ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    public static String CODE_TYPE = "prism";

    public static String httpToHttps(String string) {
        return string.replaceAll("http", "https");
    }

    public static String listToString(List<String> list) {
        if (list == null) return "null";
        else {
            String answer = "";
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) answer += ", ";
                answer += list.get(i);
            }
            return answer;
        }
    }

    private static final int shouldHasLines = 100;
    public static String toLine(String string) {
        if (string == null || "".equals(string)) string = "class Solution {$$$public:$$$    bool LeetCoder() {$$$        if (noSolution) {$$$            clickTheFeedbackIconAtTheTopRightCorner();$$$            chooseIHaveBetterSolutions();$$$            submitASolution();$$$            waitYourWonderfulSolutionToBeUpdatedToLeetCoder();$$$        }$$$    }$$$    $$$private:$$$    bool noSolution = true;$$$    void clickTheFeedbackIconAtTheTopRightCorner();$$$    void chooseIHaveBetterSolutions();$$$    void submitASolution();$$$    void waitYourWonderfulSolutionToBeUpdatedToLeetCoder();$$$};$$$";
        string = string.replaceAll("\\$\\$\\$", "\n");
        int lines = 0;
        int linePosition = 0;
        while (linePosition != -1) {
            linePosition = string.indexOf("\n", linePosition + 1);
            if (linePosition != -1) lines++;
        }
        while (lines < shouldHasLines) {
            string += "\n";
            lines++;
        }
        return string;
    }

    public static String bmobDateToMyDate(String string) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", LeetCoderApplication.getAppContext().getResources().getConfiguration().locale);
        String[] months = LeetCoderApplication.getAppContext().getResources().getStringArray(R.array.month_short_name);
        try {
            cal.setTime(sdf.parse(string));
            return months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR);
        } catch (ParseException p) {
            p.printStackTrace();
            return string;
        }
    }

    public static Calendar bmobDateToCalendar(String string) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", LeetCoderApplication.getAppContext().getResources().getConfiguration().locale);
        try {
            cal.setTime(sdf.parse(string));
            return cal;
        } catch (ParseException p) {
            p.printStackTrace();
            return cal;
        }
    }

    public static int GetScreenWidth(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }





    private static String lastColor0, lastColor1, lastColor2;

    private static Random random;

    private static String[] Colors = {"#F44336",
            "#E91E63",
            "#9C27B0",
            "#673AB7",
            "#3F51B5",
            "#2196F3",
            "#03A9F4",
            "#00BCD4",
            "#009688",
            "#4CAF50",
            "#8BC34A",
            "#CDDC39",
            "#FFEB3B",
            "#FFC107",
            "#FF9800",
            "#FF5722",
            "#795548",
            "#9E9E9E",
            "#607D8B"};

    private static LeetCoderUtil ourInstance = new LeetCoderUtil();

    public static LeetCoderUtil getInstance() {
        return ourInstance;
    }

    private LeetCoderUtil() {
    }
}
