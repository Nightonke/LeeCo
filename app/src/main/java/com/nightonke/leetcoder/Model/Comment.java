package com.nightonke.leetcoder.Model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Weiping on 2016/2/27.
 */
public class Comment extends BmobObject {

    // corresponding problem id
    private int id;

    private String title;

    private String content;

    private List<String> likers = new ArrayList<>();

    private List<String> replies = new ArrayList<>();

    private String userName;

    // the target comment's id
    private String targetComment;

    public List<String> getReplies() {
        return replies;
    }

    public void setReplies(List<String> replies) {
        this.replies = replies;
    }

    public String getContent() {
        return content;
    }

    public List<String> getLikers() {
        return likers;
    }

    public void setLikers(List<String> likers) {
        this.likers = likers;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTargetComment() {
        return targetComment;
    }

    public void setTargetComment(String targetComment) {
        this.targetComment = targetComment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
