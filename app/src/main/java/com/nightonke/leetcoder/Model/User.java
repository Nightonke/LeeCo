package com.nightonke.leetcoder.Model;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

/**
 * Created by Weiping on 2016/2/25.
 */
public class User extends BmobUser {

    private String nickName = null;

    private String myPassword = null;

    // the sum of votes in comment
    private int votes = 0;

    // the object id of comments of this user
    private ArrayList<String> comments = new ArrayList<>();

    // the id of the problem that this user likes
    private ArrayList<Integer> likeProblems = new ArrayList<>();

    public String getMyPassword() {
        return myPassword;
    }

    public void setMyPassword(String myPassword) {
        this.myPassword = myPassword;
    }

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
