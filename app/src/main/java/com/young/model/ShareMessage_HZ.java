package com.young.model;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 分享信息，对应的云端的表
 * <p>
 * Created by Nearby Yang on 2015-10-16.
 */
public class ShareMessage_HZ extends BmobObject {

    private User userId;
    private String shContent;
    private List<String> shImgs;
    private String shLocation;
    private String shTag;
    private int shWantedNum;
    private int shVisitedNum;
    private int shCommNum;

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

    public String getShTag() {
        return shTag;
    }

    public void setShTag(String shTag) {
        this.shTag = shTag;
    }

    public int getShWantedNum() {
        return shWantedNum;
    }

    public void setShWantedNum(int shWantedNum) {
        this.shWantedNum = shWantedNum;
    }

    public int getShVisitedNum() {
        return shVisitedNum;
    }

    public void setShVisitedNum(int shVisitedNum) {
        this.shVisitedNum = shVisitedNum;
    }

    public int getShCommNum() {
        return shCommNum;
    }

    public void setShCommNum(int shCommNum) {
        this.shCommNum = shCommNum;
    }
}
