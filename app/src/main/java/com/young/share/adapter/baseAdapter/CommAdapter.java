package com.young.share.adapter.baseAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.young.share.R;
import com.young.share.config.ApplicationConfig;
import com.young.share.model.MyUser;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * 通用的baseAdapter
 * <p/>
 * Created by yangfujing on 15/10/10.
 */
public abstract class CommAdapter<T> extends BaseAdapter {

    public List<T> dataList;
    public Context ctx;
    public MyUser cuser;
    public ViewHolder holder;

    public CommAdapter(Context context) {
        ctx = context;
        cuser = ApplicationConfig.getInstance().getCUser();

    }

    public void setData(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return dataList;
    }

    @Override
    public int getCount() {

        return dataList != null && dataList.size() > 0 ? dataList.size() : 0;

    }

    @Override
    public T getItem(int position) {


        return null != dataList && dataList.size() > 0 ? dataList.get(position) : null;
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
            cuser = BmobUser.getCurrentUser(ctx, MyUser.class);
        }
        if (cuser == null) {
            cuser = ApplicationConfig.getInstance().getCUser();
        }
    }

    /**
     * 跳转到详细信息中
     *
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
