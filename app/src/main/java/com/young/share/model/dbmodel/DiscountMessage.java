package com.young.share.model.dbmodel;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.sql.Blob;

/**
 * 分享信息，本地数据库的表
 * <p>
 * Created by Nearby Yang on 2015-10-16.
 */
public class DiscountMessage extends DataSupport implements Serializable {
    private int id;
    @Column(unique = true)
    private String objectId;
    private String createdAt;//createdAt
    private String updatedAt;
    private User userId;
    private String shContent;
    private Blob shImgs;
    private String shLocation;
    private String shTag;
    private Blob shWantedNum;
    private Blob shVisitedNum;

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
