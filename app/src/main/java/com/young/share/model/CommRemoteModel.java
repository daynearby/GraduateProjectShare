package com.young.share.model;


import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 通用的数据结构，作为详细信息的数据结构
 * <p/>
 * Created by Nearby Yang on 2015-10-16.
 */
public class CommRemoteModel implements Serializable {
    final long serialVersionUID = 1L;
    private int id;
    private String objectId;
    private String createdAt;//createdAt
    private String updatedAt;
    private MyUser myUser;
    private MyUser sender;
    private MyUser receiver;
    private String content;
    private List<String> images;
    private String locationInfo;
    private BmobGeoPoint geographic;
    private String tag;
    private List<String> wanted;
    private List<String> visited;
    private String mcreatedAt;
    private int comment =0;
    private int type;
    private boolean read;
    private ShareMessage_HZ shareMessage;

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

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public MyUser getSender() {
        return sender;
    }

    public void setSender(MyUser sender) {
        this.sender = sender;
    }

    public MyUser getReceiver() {
        return receiver;
    }

    public void setReceiver(MyUser receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    public BmobGeoPoint getGeographic() {
        return geographic;
    }

    public void setGeographic(BmobGeoPoint geographic) {
        this.geographic = geographic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getWanted() {
        return wanted;
    }

    public void setWanted(List<String> wanted) {
        this.wanted = wanted;
    }

    public List<String> getVisited() {
        return visited;
    }

    public void setVisited(List<String> visited) {
        this.visited = visited;
    }

    public String getMcreatedAt() {
        return mcreatedAt;
    }

    public void setMcreatedAt(String mcreatedAt) {
        this.mcreatedAt = mcreatedAt;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public ShareMessage_HZ getShareMessage() {
        return shareMessage;
    }

    public void setShareMessage(ShareMessage_HZ shareMessage) {
        this.shareMessage = shareMessage;
    }
}
