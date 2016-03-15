package com.young.share.model;

import cn.bmob.v3.BmobObject;

/**
 * 用户发出的评论、回复
 * Created by Nearby Yang on 2015-10-16.
 */
public class Message_HZ extends BmobObject {

    private String commContent;
    private boolean read;

    public String getCommContent() {
        return commContent;
    }

    public void setCommContent(String commContent) {
        this.commContent = commContent;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
