package com.young.share.model;

import cn.bmob.v3.BmobObject;

/**
 * 用户收藏记录
 * Created by Nearby Yang on 2015-10-16.
 */
public class Collection_HZ extends BmobObject {

    private MyUser shUserId;
    private MyUser collUserId;
    private ShareMessage_HZ shMsgId;
    private DiscountMessage_HZ dtMsgId;

    public MyUser getShUserId() {
        return shUserId;
    }

    public void setShUserId(MyUser shMyUserId) {
        this.shUserId = shMyUserId;
    }

    public MyUser getCollUserId() {
        return collUserId;
    }

    public void setCollUserId(MyUser collMyUserId) {
        this.collUserId = collMyUserId;
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
