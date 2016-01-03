package com.young.share.model;

import cn.bmob.v3.BmobObject;

/**
 * 评论、回复与分享信息的关系
 * Created by Nearby Yang on 2015-10-16.
 */
public class Comment_HZ extends BmobObject {

    private ShareMessage_HZ shMsgId;
    private Message_HZ messageId;
    private User senderId;
    private User reveicerId;

    public ShareMessage_HZ getShMsgId() {
        return shMsgId;
    }

    public void setShMsgId(ShareMessage_HZ shMsgId) {
        this.shMsgId = shMsgId;
    }

    public Message_HZ getMessageId() {
        return messageId;
    }

    public void setMessageId(Message_HZ messageId) {
        this.messageId = messageId;
    }

    public User getSenderId() {
        return senderId;
    }

    public void setSenderId(User senderId) {
        this.senderId = senderId;
    }

    public User getReveicerId() {
        return reveicerId;
    }

    public void setReveicerId(User reveicerId) {
        this.reveicerId = reveicerId;
    }
}
