package com.young.share.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 商家优惠信息 model
 * Created by Nearby Yang on 2015-10-16.
 */
public class DiscountMessage_HZ extends BmobObject {
    private static final long serialVersionUID =-690015927794564554L;
    private MyUser userId;
    private String dtContent;
    private List<String> dtImgs;
    private String dtLocation;
    private BmobGeoPoint geographic;
    private String dtTag;
    private int dtWantedNum;
    private int dtVisitedNum;
    private List<String> dtWanted;
    private List<String> dtVisited;

    public MyUser getMyUserId() {
        return userId;
    }

    public void setMyUserId(MyUser userId) {
        this.userId = userId;
    }

    public String getDtContent() {
        return dtContent;
    }

    public void setDtContent(String dtContent) {
        this.dtContent = dtContent;
    }

    public List<String> getDtImgs() {
        return dtImgs;
    }

    public void setDtImgs(List<String> dtImgs) {
        this.dtImgs = dtImgs;
    }

    public String getDtLocation() {
        return dtLocation;
    }

    public void setDtLocation(String dtLocation) {
        this.dtLocation = dtLocation;
    }

    public BmobGeoPoint getGeographic() {
        return geographic;
    }

    public void setGeographic(BmobGeoPoint geographic) {
        this.geographic = geographic;
    }

    public String getDtTag() {
        return dtTag;
    }

    public void setDtTag(String dtTag) {
        this.dtTag = dtTag;
    }

    public int getDtWantedNum() {
        return dtWantedNum;
    }

    public void setDtWantedNum(int dtWantedNum) {
        this.dtWantedNum = dtWantedNum;
    }

    public int getDtVisitedNum() {
        return dtVisitedNum;
    }

    public void setDtVisitedNum(int dtVisitedNum) {
        this.dtVisitedNum = dtVisitedNum;
    }

    public MyUser getUserId() {
        return userId;
    }

    public void setUserId(MyUser userId) {
        this.userId = userId;
    }

    public List<String> getDtWanted() {
        return dtWanted;
    }

    public void setDtWanted(List<String> dtWanted) {
        this.dtWanted = dtWanted;
    }

    public List<String> getDtVisited() {
        return dtVisited;
    }

    public void setDtVisited(List<String> dtVisited) {
        this.dtVisited = dtVisited;
    }

    @Override
    public void setCreatedAt(String createdAt) {
        super.setCreatedAt(createdAt);
    }
}
