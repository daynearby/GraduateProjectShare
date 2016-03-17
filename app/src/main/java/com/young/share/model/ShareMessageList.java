package com.young.share.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * json解析类
 * results
 * Created by Nearby Yang on 2015-11-18.
 */
public class ShareMessageList {
    @SerializedName("results")
    private List<ShareMessage_HZ> shareMessageHzList;

    public List<ShareMessage_HZ> getShareMessageHzList() {
        return shareMessageHzList;
    }

    public void setShareMessageHzList(List<ShareMessage_HZ> shareMessageHzList) {
        this.shareMessageHzList = shareMessageHzList;
    }
}
