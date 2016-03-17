package com.young.share.model;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * 绑定installid
 * Created by Nearby Yang on 2015-10-16.
 */
public class MyBmobInstallation extends BmobInstallation {

    private MyUser user;

    public MyBmobInstallation(Context context) {
        super(context);
    }


    public MyUser getMyUser() {
        return user;
    }

    public void setMyUser(MyUser user) {
        this.user = user;
    }
}
