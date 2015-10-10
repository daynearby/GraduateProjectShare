package com.young.adapter;

import android.annotation.TargetApi;
import android.content.Context;
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
import com.young.share.R;

import java.util.List;

/**
 * 需要传入图片的url
 * <p/>
 * Created by Nearby Yang on 2015-06-28.
 */
public class myGridViewAdapter extends BaseAdapter {

    private List<String> data;
    private Context ctx;
    private LayoutInflater myinflater;
    private boolean isUpload = false;

    public myGridViewAdapter(Context ctx) {
        this.ctx = ctx;
        myinflater = LayoutInflater.from(ctx);

    }

    /**
     * 设置图片的url
     *
     * @param data
     */
    public void setDatas(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /**
     * 设置是否是上传图片,
     * true -> 显示红色的叉叉
     * false -> 不显示红色叉叉
     *
     * @param isUpload
     */
    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    @Override
    public int getCount() {

        int num = 0;
        if (null != data) {
            num = data.size();
        }

        return num;
    }

    @Override
    public Object getItem(int position) {
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

        String imageUrl;
        ViewHolder holder = null;
        imageUrl = data.get(position);


        if (null == holder) {

            holder = new ViewHolder();
            convertView = myinflater.inflate(R.layout.item_gridview, parent, false);

            holder.imageView = (PhotoView) convertView.findViewById(R.id.im_gridview_item);
            holder.isUpload = (ImageView) convertView.findViewById(R.id.im_gridview_item_dele);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        //是否是上传状态
        if (isUpload) {
            holder.isUpload.setVisibility(View.VISIBLE);

        } else {
            holder.isUpload.setVisibility(View.GONE);

        }
// TODO: 15/10/10 图片查看,以及连续查看多张图片,左右拖动 
// 启用图片缩放功能
        holder.imageView.enable();
// 禁用图片缩放功能 (默认为禁用，会跟普通的ImageView一样，缩放功能需手动调用enable()启用)
        holder.imageView.disenable();
// 获取图片信息
        Info info = holder.imageView.getInfo();
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
        holder.imageView.animaFrom(info);
// 从现在的图片变化到所给定的图片信息，用于图片放大后点击缩小到原来的位置，具体使用可以参照demo的使用
        holder.imageView.animaTo(info, new Runnable() {
            @Override
            public void run() {
                //动画完成监听
            }
        });


        GridView gv = (GridView) parent;

        int width = gv.getHorizontalSpacing();
        int numColums = gv.getNumColumns();

        int itemWidth = (gv.getWidth() - (numColums - 1) * width - gv.getPaddingLeft() - gv.getPaddingRight()) / numColums;

        LayoutParams params = new LayoutParams(itemWidth, itemWidth);

        holder.imageView.setLayoutParams(params);

        ImageLoader.getInstance().displayImage(imageUrl, holder.imageView);

        return convertView;

    }


    private class ViewHolder {
        public PhotoView imageView;
        public ImageView isUpload;

    }
}
