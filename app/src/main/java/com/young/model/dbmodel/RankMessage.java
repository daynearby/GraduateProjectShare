package com.young.model.dbmodel;

import java.util.List;

/**
 * Created by Nearby Yang on 2015-10-17.
 */
public class RankMessage extends DBModel {

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

    public void setShContent(String shContent) {
        this.shContent = shContent;
    }

    public int getShCommNum() {
        return shCommNum;
    }

    public void setShCommNum(int shCommNum) {
        this.shCommNum = shCommNum;
    }

    public int getShVisitedNum() {
        return shVisitedNum;
    }

    public void setShVisitedNum(int shVisitedNum) {
        this.shVisitedNum = shVisitedNum;
    }

    public int getShWantedNum() {
        return shWantedNum;
    }

    public void setShWantedNum(int shWantedNum) {
        this.shWantedNum = shWantedNum;
    }

    public String getShTag() {
        return shTag;
    }

    public void setShTag(String shTag) {
        this.shTag = shTag;
    }

    public String getShLocation() {
        return shLocation;
    }

    public void setShLocation(String shLocation) {
        this.shLocation = shLocation;
    }

    public List<String> getShImgs() {
        return shImgs;
    }

    public void setShImgs(List<String> shImgs) {
        this.shImgs = shImgs;
    }

    public String getShContent() {
        return shContent;
    }
}
