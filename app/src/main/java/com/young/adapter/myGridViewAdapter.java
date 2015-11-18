package com.young.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.config.Contants;
import com.young.share.R;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.LogUtils;
import com.young.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 需要传入图片的url
 * <p>
 * Created by Nearby Yang on 2015-06-28.
 */
public class myGridViewAdapter extends BaseAdapter {

    private List<String> data;
    private Activity mactivity;
    private LayoutInflater myinflater;
    private int num = 0;
    private String imageUrl;
    private boolean isUpload = false;
    private GridView gv;
    private boolean isLocation = true;

    /**
     * 设置是否是上传图片,
     * true -> 显示红色的叉叉
     * false -> 不显示红色叉叉
     *
     * @param gv_img
     * @param isUpload
     */
    public myGridViewAdapter(Activity mactivity, GridView gv_img, boolean isUpload) {
        this.mactivity = mactivity;
        this.isUpload = isUpload;
        gv = gv_img;
        data = new ArrayList<>();

        myinflater = LayoutInflater.from(mactivity);

    }

    /**
     * 设置图片的url
     *
     * @param datas
     */
    public void setDatas(List<String> datas,boolean isLocation) {

        this.data = datas;
        this.isLocation = isLocation;

//        if (data != null) {
//            for (String s : data) {
//                LogUtils.logE("set data = " + s);
//            }
//        }

//        if (isUpload) {
//
//            if (data == null) {
//                data = new ArrayList<>();
//            }
//
////1~5需要加
//            if (data.size() >= 0 && data.size() < Contants.IMAGENUMBER) {
//
//
//                data.add(data.size(), Contants.LAST_ADD_IMG);
//            }
//
//        }

        notifyDataSetChanged();
    }

    public ArrayList<String> getData() {
        ArrayList<String> dataL = (ArrayList<String>) data;
//
//        if (dataL != null) {
//            //1 ·· 5
//            if (dataL.size() > 0 && dataL.size() < Contants.IMAGENUMBER) {
//                dataL.remove(data.size() - 1);
//
//                // 6
//            } else if (dataL.size() == Contants.IMAGENUMBER) {
//
//                if (dataL.get(Contants.IMAGENUMBER - 1).equals(Contants.LAST_ADD_IMG)) {
//                    dataL.remove(Contants.IMAGENUMBER - 1);
//                }
//            }
//
//        }
//
//        //log
//        for (String s : dataL) {
//            LogUtils.logE("get data = " + s);
//        }

        return dataL;
    }

    @Override
    public int getCount() {

        if (null != data) {
            num = data.size();
        }

        return num;
    }

    @Override
    public String getItem(int position) {
        if (data != null) {
            return data.get(position);

        }

        return null;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        LogUtils.logE("position = " + position);

        ViewHolder holder = null;
        imageUrl = data.get(position);
// TODO: 2015-11-12 get数据的时候偶尔会空指针

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = myinflater.inflate(R.layout.item_gridview, parent, false);

            holder.imageView = (ImageView) convertView.findViewById(R.id.im_gridview_item);
            holder.isUpload = (ImageView) convertView.findViewById(R.id.im_gridview_item_dele);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        //是否是上传状态,删除按钮的状态
        if (isUpload) {

            if (!imageUrl.equals(Contants.LAST_ADD_IMG)) {
                holder.isUpload.setVisibility(View.VISIBLE);
                holder.isUpload.setOnClickListener(new deleteListener(position));
//                holder.imageView.setClickable(true);
//                holder.imageView.setOnClickListener(new deleteListener(position));

            } else {
                holder.isUpload.setVisibility(View.INVISIBLE);
            }

//            if (data.size() > Contants.IMAGENUMBER) {
//                data.remove(data.size() - 1);
//            }


//            holder.imageView.enable();
        } else {
            holder.isUpload.setVisibility(View.GONE);

        }



        GridView gv = (GridView) parent;

        int width = gv.getHorizontalSpacing();
        int numColums = gv.getNumColumns();

        int itemWidth = (gv.getWidth() - (numColums - 1) * width - gv.getPaddingLeft() - gv.getPaddingRight()) / numColums;

        LayoutParams params = new LayoutParams(itemWidth, itemWidth);

        holder.imageView.setLayoutParams(params);

        ImageLoader.getInstance().displayImage(NetworkUtils.getRealUrl(mactivity, imageUrl,isLocation), holder.imageView, ImageHandlerUtils.imageloaderOption());


        return convertView;

    }


    private class ViewHolder {
        public ImageView imageView;
        public ImageView isUpload;

    }

    public class deleteListener implements View.OnClickListener {

        private int _postion;

        public deleteListener(int pos) {
            _postion = pos;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.im_gridview_item_dele://删除按钮

//                    LogUtils.logE("点击删除图片按钮 position = " + _postion);
                    data.remove(_postion);

//                    if (data.size() == Contants.IMAGENUMBER-1) {
//                        if (!data.get(Contants.IMAGENUMBER - 2).equals(Contants.LAST_ADD_IMG)) {
//                            data.add(Contants.LAST_ADD_IMG);
//                        }
//                    }
                    notifyDataSetChanged();

                    break;


            }
        }
    }


}
