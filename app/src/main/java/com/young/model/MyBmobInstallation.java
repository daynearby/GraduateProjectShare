package com.young.model;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * 绑定installid
 * Created by Nearby Yang on 2015-10-16.
 */
public class MyBmobInstallation extends BmobInstallation {
    public MyBmobInstallation(Context context) {
        super(context);
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    private User userId;


}
