package com.nightonke.leetcoder.Model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Weiping on 2016/1/10.
 */
public class Problem_Index extends BmobObject {

    private int id;
    private String title;
    private String level;
    private String summary;
    private int like;
    private List<String> tags = null;

    public Problem_Index clone() {
        Problem_Index problemIndex = new Problem_Index();
        problemIndex.setObjectId(getObjectId());
        problemIndex.setCreatedAt(getCreatedAt());
        problemIndex.setUpdatedAt(getCreatedAt());
        problemIndex.setACL(getACL());
        problemIndex.setId(getId());
        problemIndex.setTitle(getTitle());
        problemIndex.setLevel(getLevel());
        problemIndex.setSummary(getSummary());
        problemIndex.setLike(getLike());
        List<String> newtags = new ArrayList<>();
        for (String s : tags) {
            newtags.add(s);
        }
        problemIndex.setTags(newtags);
        return problemIndex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
