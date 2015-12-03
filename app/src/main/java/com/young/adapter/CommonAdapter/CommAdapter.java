package com.young.adapter.CommonAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.young.model.User;

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

    public CommAdapter(Context context) {
        ctx = context;
        cuser = BmobUser.getCurrentUser(ctx, User.class);
    }

    public void setData(List<T> beanList) {
        this.beanList = beanList;
        notifyDataSetChanged();
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

        ViewHolder holder = ViewHolder.get(ctx, position, getlayoutid(position), convertView, parent);


        convert(holder, getItem(position),position);

        return holder.getConvertView();
    }

    /**
     * toast 提示
     *
     * @param strResId 提示文字 - 资源
     */
    public void mToast(int strResId) {
        Toast.makeText(ctx, strResId, Toast.LENGTH_LONG).show();
    }


    /**
     * 再获取当前用户是否存在
     */
    public void getUser() {
        if (cuser == null) {
            cuser = BmobUser.getCurrentUser(ctx, User.class);
        }
    }

    //adapter获取view实例\绑定数据
    public abstract void convert(ViewHolder holder, T t, int position);

    /**
     * item布局的layoutId
     *
     * @return
     * @param position
     */
    public abstract int getlayoutid(int position);


}
