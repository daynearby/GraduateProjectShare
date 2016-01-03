package com.young.share.model;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * 绑定installid
 * Created by Nearby Yang on 2015-10-16.
 */
public class MyBmobInstallation extends BmobInstallation {

    private User user;

    public MyBmobInstallation(Context context) {
        super(context);
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
