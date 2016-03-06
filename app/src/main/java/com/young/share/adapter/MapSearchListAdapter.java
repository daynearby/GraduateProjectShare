package com.young.share.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.share.R;
import com.young.share.adapter.baseAdapter.CommAdapter;
import com.young.share.adapter.baseAdapter.ViewHolder;
import com.young.share.model.gson.PlaceSuggestion;

/**
 *
 * 百度搜索返回的ListView的适配器
 *
 * Created by Nearby Yang on 2016-03-06.
 */
public class MapSearchListAdapter extends CommAdapter<PlaceSuggestion.ResultEntity> {


    public MapSearchListAdapter(Context context) {
        super(context);

    }

    @Override
    public void convert(ViewHolder holder, PlaceSuggestion.ResultEntity resultEntity, int position) {
/**
 *  取消item中checkBox抢占焦点的问题。点击item便可以
 *  第一个选项：不显示位置，可以使用ListView的header功能
 *
 */
        TextView nameTx =  holder.getView(R.id.id_tx_search_item_name);
        TextView detailTx =  holder.getView(R.id.id_tx_search_item_detail);
        ImageView checkedIm = holder.getView(R.id.im_search_item_checked);

        nameTx.setText(resultEntity.getName());
        detailTx.setText(String.format("%s%s", resultEntity.getCity(), resultEntity.getDistrict()));



    }

    @Override
    public int getlayoutid(int position) {
        return R.layout.item_baidumap_search_item;
    }
}
