package com.nightonke.leetcoder.Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Weiping on 2016/2/23.
 */
public class ProblemBug extends BmobObject {

    private Integer id;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
