package com.young.share.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.young.share.model.User;

import java.util.List;


/**
 * popupwindows 基类
 * Created by Nearby Yang on 2015-11-19.
 */
public abstract class BasePopupWin extends PopupWindow {

    public Context context;
    public View view;
    public LayoutInflater inflater;
    public boolean selectHometown = false;
    public List<String> datas;
    public User user;

    public BasePopupWin(Context context) {
        super(context);
        config(context);

    }

    public BasePopupWin(Context context, List<String> datas, boolean selectHometown) {
        super(context);
        this.datas = datas;
        this.selectHometown = selectHometown;
        config(context);

    }

    public BasePopupWin(Context context, User user) {
        super(context);
        this.user = user;
        config(context);
    }

    private void config(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(getLayoutId(), null);
        setContentView(view);

        setFocusable(true);
        init();
        findView();
        bindData();
    }

    protected abstract int getLayoutId();

    protected abstract void init();

    protected abstract void findView();

    protected abstract void bindData();

    public abstract void onShow(View v);

}
