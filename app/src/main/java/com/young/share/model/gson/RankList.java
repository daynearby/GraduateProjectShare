package com.young.share.model.gson;

import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.ShareMessage_HZ;

import java.util.List;

/**
 * 排行榜数据的model
 * Created by Nearby Yang on 2015-12-26.
 */
public class RankList {
//sharemessages,discountMessages
    private List<ShareMessage_HZ> sharemessages;
    private List<DiscountMessage_HZ> discountMessages;

    public List<ShareMessage_HZ> getSharemessages() {
        return sharemessages;
    }

    public void setSharemessages(List<ShareMessage_HZ> sharemessages) {
        this.sharemessages = sharemessages;
    }

    public List<DiscountMessage_HZ> getDiscountMessages() {
        return discountMessages;
    }

    public void setDiscountMessages(List<DiscountMessage_HZ> discountMessages) {
        this.discountMessages = discountMessages;
    }
}
