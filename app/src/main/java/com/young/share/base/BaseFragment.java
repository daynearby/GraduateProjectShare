package com.young.share.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.young.share.R;
import com.young.share.config.ApplicationConfig;
import com.young.share.utils.ThreadUtils;

/**
 * fragment基类
 * Created by Nearby Yang on 2015-10-09.
 */
public abstract class BaseFragment extends Fragment {

    private View view;
    public ThreadUtils threadUtils;
    public Context context;
    public ApplicationConfig app;

    public BaseFragment() {
        app = ApplicationConfig.getInstance();
    }
    public BaseFragment(Context context) {
        this.context = context;
        app = ApplicationConfig.getInstance();
    }



    public  Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler(msg);
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(getLayoutId(), null);
        ViewGroup vg = (ViewGroup) view.getParent();
        view.findViewById(R.id.pb_item_photoview);
        if (vg != null) {
            vg.removeAllViewsInLayout();
        }
        threadUtils = ApplicationConfig.getInstance().getThreadInstance();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
        bindData();

    }


    public abstract int getLayoutId();

    //初始化数据
    public abstract void initData();

    //实例化控件
    public abstract void initView();

    //绑定数据
    public abstract void bindData();

    public abstract void handler(Message msg);

    /**
     * 简化findviewbyid
     *
     * @param viewID
     * @param <T>
     * @return
     */
    public <T> T $(int viewID) {
        return (T) view.findViewById(viewID);
    }
}
