package com.young.share.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 根据用户id查询用户信息
 * <p/>
 * Created by Nearby Yang on 2016-03-19.
 */
public class UserList {

    @SerializedName("results")
    private List<MyUser> userList;

    public List<MyUser> getUserList() {
        return userList;
    }

    public void setUserList(List<MyUser> userList) {
        this.userList = userList;
    }
}
