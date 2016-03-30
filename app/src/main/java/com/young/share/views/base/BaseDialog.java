package com.young.share.views.base;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

/**
 * dialog的基类，需要多种自定义样式，可以先继承该类，
 * dialog布局文件中，需要有一个父布局，只有一个子布局的情况下，系统可以测量到宽度，
 * 但是直接将空间放在一个父布局中，宽度又没有固定，那么该dialog尺寸会出现不是预期尺寸
 * Created by yangFujin on 2016/1/22.
 */
public abstract class BaseDialog extends Dialog {

    public Context context;
    public View view;

    public BaseDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = LayoutInflater.from(context).inflate(getLayoutId(), null);
        setContentView(view);
        this.context = context;
        findview();

        bindData();
    }

    protected abstract int getLayoutId();

    protected abstract void findview();

    protected abstract void bindData();


}
