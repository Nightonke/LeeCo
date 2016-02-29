package com.nightonke.leetcoder.Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Weiping on 2016/2/28.
 */
public class Feedback extends BmobObject {

    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
