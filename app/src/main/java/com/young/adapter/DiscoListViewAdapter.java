package com.young.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.young.adapter.CommentAdapter.ViewHolder;
import com.young.adapter.CommentAdapter.CommAdapter;
import com.young.base.BaseModel;
import com.young.share.R;

/**
 * 实例化
 *
 * 父类中setdata并且刷新
 *
 *
 * Created by yangfujing on 15/10/10.
 */
public class DiscoListViewAdapter extends CommAdapter<BaseModel> {

    private GridView myGridview;

    /**
     * 实例化对象
     *
     * @param context
     */
    public DiscoListViewAdapter(Context context) {
        super(context);
    }


    @Override
    public void convert(ViewHolder holder, BaseModel bean) {
        myGridViewAdapter gridViewAdapter = new myGridViewAdapter((Activity) ctx,false);

        holder.getView(R.id.id_im_userH);//用户头像
        holder.getView(R.id.id_userName);//昵称
        holder.getView(R.id.id_tx_tab);//标签
        holder.getView(R.id.id_tx_share_content);//分享的文本内容
        myGridview = holder.getView(R.id.id_gv_shareimg);//分享的照片
        holder.getView(R.id.id_tx_wantogo);//想去数量
        holder.getView(R.id.id_hadgo);//去过数量
        holder.getView(R.id.id_tx_comment);//评论数量

        myGridview.setAdapter(gridViewAdapter);

    }

    @Override
    public int getlayoutid() {
        return R.layout.item_share_main;
    }


}
