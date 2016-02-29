package com.nightonke.leetcoder.Model;

import android.util.Log;

import com.nightonke.leetcoder.BuildConfig;
import com.nightonke.leetcoder.Utils.LeetCoderUtil;

import java.util.List;
import cn.bmob.v3.BmobObject;

/**
 * Created by Weiping on 2016/1/10.
 */
public class Problem extends BmobObject {

    private int id;
    private String content;
    private String solution;
    private List<String> similarProblems;
    private String discussLink;
    private String problemLink;

    public void show() {
        if (BuildConfig.DEBUG) {
            Log.d("LeetCoder", "Problem show:\nid: " + id +
                    "\ncontent: " + content +
                    "\nsolution: " + solution +
                    "\nsimilarProblems: " + LeetCoderUtil.listToString(similarProblems) +
                    "\ndiscussLink: " + discussLink +
                    "\nproblemLink: " + problemLink);
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDiscussLink() {
        return discussLink;
    }

    public void setDiscussLink(String discussLink) {
        this.discussLink = discussLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProblemLink() {
        return problemLink;
    }

    public void setProblemLink(String problemLink) {
        this.problemLink = problemLink;
    }

    public List<String> getSimilarProblems() {
        return similarProblems;
    }

    public void setSimilarProblems(List<String> similarProblems) {
        this.similarProblems = similarProblems;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = LeetCoderUtil.toLine(solution);
    }
}
