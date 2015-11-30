package com.young.model;


import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 通用的数据结构，作为详细信息的数据结构
 * <p>
 * Created by Nearby Yang on 2015-10-16.
 */
public class CommRemoteModel extends BmobObject{

    private User user;
    private String content;
    private List<String> images;
    private String locationInfo;
    private String tag;
    private List<String> wanted;
    private List<String> visited;
    private String mcreatedAt;
    private int comment;
    private int type;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
