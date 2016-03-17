package com.young.share.model;


import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 分享信息，对应的云端的表
 * <p>
 * Created by Nearby Yang on 2015-10-16.
 */
public class ShareMessage_HZ extends BmobObject{

    private MyUser userId;
    private String shContent;
    private List<String> shImgs;
    private String shLocation;
    private BmobGeoPoint geographic;
    private String shTag;
    private List<String> shWantedNum;
    private List<String> shVisitedNum;
    private Integer shCommNum;

    public MyUser getMyUserId() {
        return userId;
    }

    public void setMyUserId(MyUser userId) {
        this.userId = userId;
    }

    public String getShContent() {
        return shContent;
    }

    public void setShContent(String shContent) {
        this.shContent = shContent;
    }

    public List<String> getShImgs() {
        return shImgs;
    }

    public void setShImgs(List<String> shImgs) {
        this.shImgs = shImgs;
    }

    public String getShLocation() {
        return shLocation;
    }

    public void setShLocation(String shLocation) {
        this.shLocation = shLocation;
    }

    public BmobGeoPoint getGeographic() {
        return geographic;
    }

    public void setGeographic(BmobGeoPoint geographic) {
        this.geographic = geographic;
    }

    public String getShTag() {
        return shTag;
    }

    public void setShTag(String shTag) {
        this.shTag = shTag;
    }

    public List<String> getShWantedNum() {
        return shWantedNum;
    }

    public void setShWantedNum(List<String> shWantedNum) {
        this.shWantedNum = shWantedNum;
    }

    public List<String> getShVisitedNum() {
        return shVisitedNum;
    }

    public void setShVisitedNum(List<String> shVisitedNum) {
        this.shVisitedNum = shVisitedNum;
    }

    public Integer getShCommNum() {
        return shCommNum;
    }

    public void setShCommNum(Integer shCommNum) {
        this.shCommNum = shCommNum;
    }
}
