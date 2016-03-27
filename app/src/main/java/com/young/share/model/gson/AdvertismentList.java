package com.young.share.model.gson;

import com.google.gson.annotations.SerializedName;
import com.young.share.model.Advertisement;

import java.util.List;

/**
 * Advertisement
 *
 * json解析模板
 * Created by Nearby Yang on 2015-12-05.
 */
public class AdvertismentList {
    @SerializedName("results")
    private List<Advertisement> collecList;

    public List<Advertisement> getCollecList() {
        return collecList;
    }

    public void setCollecList(List<Advertisement> collecList) {
        this.collecList = collecList;
    }
}
