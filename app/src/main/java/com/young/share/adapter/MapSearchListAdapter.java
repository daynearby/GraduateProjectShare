package com.young.share.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.young.share.R;
import com.young.share.adapter.baseAdapter.CommAdapter;
import com.young.share.adapter.baseAdapter.ViewHolder;
import com.young.share.model.gson.PlaceSearch;

/**
 * 百度搜索返回的ListView的适配器
 * <p/>
 * Created by Nearby Yang on 2016-03-06.
 */
public class MapSearchListAdapter extends CommAdapter<PlaceSearch.ResultsEntity> {

    private boolean visible = true;//默认。可见，有数据刷新完之后不可见

    public MapSearchListAdapter(Context context) {
        super(context);

    }

    /**
     * 圆形进度条是否可见
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void convert(ViewHolder holder, PlaceSearch.ResultsEntity resultEntity, int position) {
/**
 *  取消item中checkBox抢占焦点的问题。点击item便可以
 *  第一个选项：不显示位置，可以使用ListView的header功能
 *
 */
        TextView nameTx = holder.getView(R.id.id_tx_search_item_name);
        TextView detailTx = holder.getView(R.id.id_tx_search_item_detail);
        ImageView checkedIm = holder.getView(R.id.im_search_item_checked);
        ProgressBar pb = holder.getView(R.id.pb_search_item_loading);

        pb.setVisibility(position == 0 && visible? View.VISIBLE : View.INVISIBLE);

        nameTx.setText(resultEntity.getName());
        detailTx.setText(resultEntity.getAddress());
//        detailTx.setText(String.format("%s%s", resultEntity.getCity(), resultEntity.getDistrict()));

    }

    @Override
    public int getlayoutid(int position) {
        return R.layout.item_baidumap_search_item;
    }
}
