package com.nightonke.leetcoder.Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Weiping on 2016/2/29.
 */
public class Vote extends BmobObject {

    private String userName;
    private int vote;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
