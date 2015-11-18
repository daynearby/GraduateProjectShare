package com.young.model;

import com.google.gson.annotations.SerializedName;

import cn.bmob.v3.BmobObject;

/**
 * Created by Nearby Yang on 2015-10-16.
 */
public class Collection_HZ extends BmobObject {

    private User shUserId;
    private User collUserId;
    private ShareMessage_HZ shMsgId;
    private DiscountMessage_HZ dtMsgId;

    public User getShUserId() {
        return shUserId;
    }

    public void setShUserId(User shUserId) {
        this.shUserId = shUserId;
    }

    public User getCollUserId() {
        return collUserId;
    }

    public void setCollUserId(User collUserId) {
        this.collUserId = collUserId;
    }

    public ShareMessage_HZ getShMsgId() {
        return shMsgId;
    }

    public void setShMsgId(ShareMessage_HZ shMsgId) {
        this.shMsgId = shMsgId;
    }

    public DiscountMessage_HZ getDtMsgId() {
        return dtMsgId;
    }

    public void setDtMsgId(DiscountMessage_HZ dtMsgId) {
        this.dtMsgId = dtMsgId;
    }
}
