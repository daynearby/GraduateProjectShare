package com.young.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.young.share.R;
import com.young.utils.DisplayUtils;
import com.young.utils.LogUtils;

import java.util.List;


/**
 * 弹窗，实现输入、保存草稿
 * Created by Nearby Yang on 2015-10-22.
 */
public class PopupWinListView extends PopupWindow {

    private Context ctx;
    private View view;
    private List<String> datas;
    private ListView listview;
    private mAdapter adapter;
    private onItemClick listener;


    public PopupWinListView(Context ctx, List<String> datas) {
        this.ctx = ctx;
        this.datas = datas;

        init();
        //初始化控件
        findViews();

    }

    private void init() {
        view = LayoutInflater.from(ctx).inflate(R.layout.content_popup_window_list, null);
        setContentView(view);
        setWidth(DisplayUtils.getScreenWidthPixels((Activity) ctx) / 2);
        if (datas.size() > 4) {
            setHeight(DisplayUtils.getScreenHeightPixels((Activity) ctx) / 2);
        } else {
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setFocusable(true);
        setOutsideTouchable(true);

        setBackgroundDrawable(new BitmapDrawable());


    }


    /**
     * 初始化控件
     */
    private void findViews() {
        listview = (ListView) view.findViewById(R.id.lsv_contnet_popupwindow);
        adapter = new mAdapter();

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new itemOnClickListener());

    }


    /**
     * 设置点击事件
     *
     * @param listener
     */
    public void setItemClick(onItemClick listener) {
        this.listener = listener;

    }

    public void onShow(View v) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int offsetPx = (DisplayUtils.getScreenWidthPixels((Activity) ctx) - v.getWidth()) / 2;
            int offsetPx2dp = DisplayUtils.px2dp(ctx, offsetPx);
            showAsDropDown(v, -offsetPx2dp, 0, Gravity.CENTER);
        } else {


//            int x = DisplayUtils.px2dp(ctx, v.getLeft());
//            int y = DisplayUtils.px2dp(ctx, v.getBottom());

//            int[] location = new int[2];
//            v.getLocationInWindow(location);
//            int x = location[0];
//            int y = location[1];
//            LogUtils.logE(" v x = " + x + " y = " + y);
            int widthDp = DisplayUtils.px2dp(ctx, v.getWidth()) / 2;


//            showAtLocation(v, Gravity.NO_GRAVITY, x-v.getWidth(),y+v.getHeight());
//            LogUtils.logE(" v Width() = " + v.getWidth()+" v Height() = "+v.getHeight()  + " left = " + v.getLeft());
            showAsDropDown(v, -widthDp, 0);
        }
//        update();


    }

    private class itemOnClickListener implements AdapterView.OnItemClickListener {
        String str = null;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dismiss();
            if (datas.size() > 0) {
                str = datas.get(position);
            }
            listener.onClick(view, str, position, id);
//            LogUtils.logI("position = " + position + " id = " + id);
        }
    }

    /**
     * item点击事件 回调
     */
    public interface onItemClick {
        void onClick(View view, String str, int position, long id);
    }

    /**
     * 显示数据的baseAdapter
     */
    private class mAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ctx).inflate(R.layout.item_data_textview, parent, false);

                holder.contentTv = (TextView) convertView.findViewById(R.id.tv_item_data);
                holder.iconIm = (ImageView) convertView.findViewById(R.id.im_icon_item_data);

                convertView.setTag(holder);
            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.contentTv.setText(datas.get(position));


            return convertView;
        }


        private class ViewHolder {
            TextView contentTv;
            ImageView iconIm;
        }
    }

}
