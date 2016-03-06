package com.young.share.adapter.baseAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.young.share.R;
import com.young.share.model.User;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * 通用的baseAdapter
 * <p>
 * Created by yangfujing on 15/10/10.
 */
public abstract class CommAdapter<T> extends BaseAdapter {

    public List<T> beanList;
    public Context ctx;
    public User cuser;
    public ViewHolder holder;

    public CommAdapter(Context context) {
        ctx = context;
        cuser = BmobUser.getCurrentUser(ctx, User.class);
    }

    public void setData(List<T> beanList) {
        this.beanList = beanList;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return beanList;
    }

    @Override
    public int getCount() {

        return null == beanList ? 0 : beanList.size();

    }

    @Override
    public T getItem(int position) {


        return null != beanList && beanList.size() > 0 ? beanList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

         holder = ViewHolder.get(ctx, position, getlayoutid(position), convertView, parent);


        convert(holder, getItem(position), position);

        return holder.getConvertView();
    }


    /**
     * 再获取当前用户是否存在
     */
    public void getUser() {
        if (cuser == null) {
            cuser = BmobUser.getCurrentUser(ctx, User.class);
        }
    }

    /**
     * 跳转到详细信息中
     * @param clazz
     * @param bundle
     */
    public void startActivity(Class clazz, Bundle bundle) {
        Intent intent = new Intent(ctx, clazz);
        if (bundle != null) {

            intent.putExtras(bundle);
        }
        ctx.startActivity(intent);
        ((Activity) ctx).overridePendingTransition(R.animator.activity_slid_right_in, R.animator.activity_slid_left_out);
    }

    //adapter获取view实例\绑定数据
    public abstract void convert(ViewHolder holder, T t, int position);

    /**
     * item布局的layoutId
     *
     * @param position
     * @return
     */
    public abstract int getlayoutid(int position);


}
