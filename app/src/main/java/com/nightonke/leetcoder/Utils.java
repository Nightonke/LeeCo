package com.nightonke.leetcoder;

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

}
