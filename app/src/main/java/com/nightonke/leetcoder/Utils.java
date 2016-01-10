package com.nightonke.leetcoder;

import android.util.Log;

import java.util.List;

/**
 * Created by Weiping on 2016/1/10.
 */

public class Utils {

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

    public static String toLine(String string) {
        return string.replaceAll("\\$\\$\\$", "\n");
    }

}
