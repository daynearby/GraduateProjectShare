package com.young.share.model;

import cn.bmob.v3.BmobObject;

/**
 * 用户发出的评论、回复
 * Created by Nearby Yang on 2015-10-16.
 */
public class Message_HZ extends BmobObject {

    private String commContent;
    private Boolean read;

    public String getCommContent() {
        return commContent;
    }

    public void setCommContent(String commContent) {
        this.commContent = commContent;
    }

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
