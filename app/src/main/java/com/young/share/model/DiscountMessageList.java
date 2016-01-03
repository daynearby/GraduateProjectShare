package com.young.share.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 商家优惠gson解析模板
 * Created by Nearby Yang on 2015-12-07.
 */
public class DiscountMessageList {

    @SerializedName("results")
    private List<DiscountMessage_HZ> discountList;

    public List<DiscountMessage_HZ> getDiscountList() {
        return discountList;
    }

    public void setDiscountList(List<DiscountMessage_HZ> discountList) {
        this.discountList = discountList;
    }
}
