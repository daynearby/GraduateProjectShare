package com.young.share.model.dbmodel;

import java.sql.Blob;

/**
 * 分享信息，本地数据库的表
 * <p>
 * Created by Nearby Yang on 2015-10-16.
 */
public class DiscountMessage extends DBModel {

    private User userId;
    private String shContent;
    private Blob shImgs;
    private String shLocation;
    private String shTag;
    private Blob shWantedNum;
    private Blob shVisitedNum;

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getShContent() {
        return shContent;
    }

    public void setShContent(String shContent) {
        this.shContent = shContent;
    }


    public String getShLocation() {
        return shLocation;
    }

    public void setShLocation(String shLocation) {
        this.shLocation = shLocation;
    }

    public String getShTag() {
        return shTag;
    }

    public void setShTag(String shTag) {
        this.shTag = shTag;
    }

    public Blob getShImgs() {
        return shImgs;
    }

    public void setShImgs(Blob shImgs) {
        this.shImgs = shImgs;
    }

    public Blob getShWantedNum() {
        return shWantedNum;
    }

    public void setShWantedNum(Blob shWantedNum) {
        this.shWantedNum = shWantedNum;
    }

    public Blob getShVisitedNum() {
        return shVisitedNum;
    }

    public void setShVisitedNum(Blob shVisitedNum) {
        this.shVisitedNum = shVisitedNum;
    }
}
