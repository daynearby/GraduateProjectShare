package com.young.share.model.dbmodel;

/**
 *
 * 分享消息的记录
 * user 为当前用户
 *
 * Created by Nearby Yang on 2015-12-04.
 */
public class ShareRecrod extends DBModel {

    private String shContent;
    private String shImgs;
    private String shLocation;
    private String shTag;
    private String shWantedNum;
    private String shVisitedNum;
    private int shCommNum;


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

    public int getShCommNum() {
        return shCommNum;
    }

    public void setShCommNum(int shCommNum) {
        this.shCommNum = shCommNum;
    }

    public String getShImgs() {
        return shImgs;
    }

    public void setShImgs(String shImgs) {
        this.shImgs = shImgs;
    }

    public String getShWantedNum() {
        return shWantedNum;
    }

    public void setShWantedNum(String shWantedNum) {
        this.shWantedNum = shWantedNum;
    }

    public String getShVisitedNum() {
        return shVisitedNum;
    }

    public void setShVisitedNum(String shVisitedNum) {
        this.shVisitedNum = shVisitedNum;
    }
}
