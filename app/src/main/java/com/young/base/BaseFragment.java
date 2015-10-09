package com.young.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Nearby Yang on 2015-10-09.
 */
public abstract class BaseFragment extends Fragment {

    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(getLayoutId(), null);
        ViewGroup vg = (ViewGroup) view.getParent();
        if (vg != null) {
            vg.removeAllViewsInLayout();
        }
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
