package com.young.base;


import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.young.utils.LogUtils;
import com.young.utils.ThreadUtils;

/**
 * view pager 的基类
 * Created by Nearby Yang on 2015-10-09.
 */
public abstract class BasePager {
    public Context ctx;
    public View view;
    public ThreadUtils threadUtils;

    public void init(Context ctx, View view, ThreadUtils threadUtils) {
        this.ctx = ctx;
        this.view = view;
        this.threadUtils = threadUtils;

        initData();
        initView();
        bindData();
    }

    public Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler(msg);
        }
    };

    //初始化数据
    public abstract void initData();

    //实例化控件
    public abstract void initView();

    //绑定数据
    public abstract void bindData();

    public abstract void handler(Message msg);

    public <T extends View> T $(int resId) {

        return (T) view.findViewById(resId);
    }

}
