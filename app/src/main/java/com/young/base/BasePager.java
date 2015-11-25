package com.young.base;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.young.config.ApplicationConfig;

/**
 * view pager 的基类
 * Created by Nearby Yang on 2015-10-09.
 */
public abstract class BasePager {
    public Context ctx;
    public View view;
    public ApplicationConfig app;

    public void init(Context ctx, View view) {
        this.ctx = ctx;
        this.view = view;
        app = ApplicationConfig.getInstance();

        initView();
        bindData();
    }

    public  Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler(msg);
        }
    };

    public abstract void initView();

    public abstract void bindData();

    public abstract void handler(Message msg);

    public <T extends View> T $(int resId) {

        return (T) view.findViewById(resId);
    }

}
