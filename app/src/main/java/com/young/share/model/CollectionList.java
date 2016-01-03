package com.young.share.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * collection
 * json解析模板
 * Created by Nearby Yang on 2015-12-05.
 */
public class CollectionList {
    @SerializedName("results")
    private List<Collection_HZ> collecList;

    public List<Collection_HZ> getCollecList() {
        return collecList;
    }

    public void setCollecList(List<Collection_HZ> collecList) {
        this.collecList = collecList;
    }
}
