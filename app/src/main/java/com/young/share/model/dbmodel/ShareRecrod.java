package com.young.share.model.dbmodel;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 *
 * 分享消息的记录
 * user 为当前用户
 *
 * Created by Nearby Yang on 2015-12-04.
 */
public class ShareRecrod extends DataSupport implements Serializable {
    private int id;
    @Column(unique = true)
    private String objectId;
    private String createdAt;//createdAt
    private String updatedAt;
    private String shContent;
    private String shImgs;
    private String shLocation;
    private String shTag;
    private String shWantedNum;
    private String shVisitedNum;
    private int shCommNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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
