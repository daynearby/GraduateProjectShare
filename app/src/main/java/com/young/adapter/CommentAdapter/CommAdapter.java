package com.young.adapter.CommentAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.young.adapter.myGridViewAdapter;
import com.young.share.R;

import java.util.List;

/**
 * 通用的baseAdapter
 *
 * Created by yangfujing on 15/10/10.
 */
public abstract class CommAdapter<T> extends BaseAdapter {

    public List<T> beanList;
    public Context ctx;

    public CommAdapter(Context context) {
        ctx = context;
    }

    public void setData(List<T> beanList) {
        this.beanList = beanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == beanList) {
            return 0;
        } else {
            return beanList.size();
        }


    }

    @Override
    public T getItem(int position) {

        if (null != beanList) {
            return beanList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = ViewHolder.get(ctx, position, getlayoutid(), convertView, parent);


        convert(holder,getItem(position));

        return holder.getConvertView();
    }

    //adapter获取view实例\绑定数据
    public abstract void convert(ViewHolder holder,T t);

    /**
     * item布局的layoutId
     * @return
     */
    public abstract  int getlayoutid();


}
