package com.young.base;


import android.content.Context;
import android.view.View;

/**
 * view pager 的基类
 * Created by Nearby Yang on 2015-10-09.
 */
public abstract class BasePager {
    public Context ctx;
    public View view;

    public void init(Context ctx, View view) {
        this.ctx = ctx;
        this.view = view;
    }

    public abstract void initView();

    public abstract void bindData();

}
