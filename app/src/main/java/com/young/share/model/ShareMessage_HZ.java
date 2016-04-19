package com.young.share.model;


import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 分享信息，对应的云端的表
 * <p/>
 * Created by Nearby Yang on 2015-10-16.
 */
public class ShareMessage_HZ extends BmobObject {

    private MyUser userId;
    private String shContent;
    private List<String> shImgs;
    private String shLocation;
    private BmobGeoPoint geographic;
    private String shTag;
    private int shWantedNum;
    private int shVisitedNum;
    private int shCommNum;
    private BmobFile video;
    private BmobFile videoPreview;
    private List<String> shWanted;
    private List<String> shVisited;

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


    public BmobFile getVideo() {
        return video;
    }

    public void setVideo(BmobFile video) {
        this.video = video;
    }

    public BmobFile getVideoPreview() {
        return videoPreview;
    }

    public void setVideoPreview(BmobFile videoPreview) {
        this.videoPreview = videoPreview;
    }

    public List<String> getShWanted() {
        return shWanted;
    }

    public void setShWanted(List<String> shWanted) {
        this.shWanted = shWanted;
    }

    public List<String> getShVisited() {
        return shVisited;
    }

    public void setShVisited(List<String> shVisited) {
        this.shVisited = shVisited;
    }

    @Override
    public void setCreatedAt(String createdAt) {
        super.setCreatedAt(createdAt);
    }

}
