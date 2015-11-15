package com.young.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Nearby Yang on 2015-10-16.
 */
public class DiscountMessage_HZ extends BmobObject {

private User userId;
    private String dtContent;
    private List<String> dtImgs;
    private String dtLocation;
    private String dtTag;
    private List<String> dtWantedNum;
    private List<String> dtVisitedNum;

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
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

    public String getDtTag() {
        return dtTag;
    }

    public void setDtTag(String dtTag) {
        this.dtTag = dtTag;
    }

    public List<String> getDtWantedNum() {
        return dtWantedNum;
    }

    public void setDtWantedNum(List<String> dtWantedNum) {
        this.dtWantedNum = dtWantedNum;
    }

    public List<String> getDtVisitedNum() {
        return dtVisitedNum;
    }

    public void setDtVisitedNum(List<String> dtVisitedNum) {
        this.dtVisitedNum = dtVisitedNum;
    }
}
