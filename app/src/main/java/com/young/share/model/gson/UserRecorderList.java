package com.young.share.model.gson;

import com.google.gson.annotations.SerializedName;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.ShareMessage_HZ;

import java.io.Serializable;
import java.util.List;

/**
 * 解析用户记录的gson模板
 * Created by Nearby Yang on 2016-04-03.
 */
public class UserRecorderList implements Serializable {
    @SerializedName("ShareMessage")
    private List<ShareMessage_HZ> shareMessage;
    @SerializedName("discountMessages")
    private List<DiscountMessage_HZ> discountMessage;

    public List<ShareMessage_HZ> getShareMessage() {
        return shareMessage;
    }

    public void setShareMessage(List<ShareMessage_HZ> shareMessage) {
        this.shareMessage = shareMessage;
    }

    public List<DiscountMessage_HZ> getDiscountMessage() {
        return discountMessage;
    }

    public void setDiscountMessage(List<DiscountMessage_HZ> discountMessage) {
        this.discountMessage = discountMessage;
    }
}
