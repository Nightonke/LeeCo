package com.nightonke.leetcoder;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

/**
 * Created by Weiping on 2016/2/25.
 */
public class User extends BmobUser {

    private String nickName;

    // the sum of votes in comment
    private int votes;

    // the object id of comments of this user
    private ArrayList<String> comments;

    // the id of the problem that this user likes
    private ArrayList<Integer> likeProblems;

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public ArrayList<Integer> getLikeProblems() {
        return likeProblems;
    }

    public void setLikeProblems(ArrayList<Integer> likeProblems) {
        this.likeProblems = likeProblems;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
