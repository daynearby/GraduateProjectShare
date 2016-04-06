package com.young.share.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.BigPicActivity;
import com.young.share.R;
import com.young.share.config.Contants;
import com.young.share.model.PictureInfo;
import com.young.share.utils.DisplayUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 需要传入图片的url
 * <p/>
 * Created by Nearby Yang on 2015-06-28.
 */
public class GridviewAdapter extends BaseAdapter {

    private List<PictureInfo> data;
    private Activity mActivity;
    private LayoutInflater myinflater;
    private PictureInfo imageUrl;
    private boolean isUpload = false;
    private GridView gv;
//    private boolean isLocation = true;

    /**
     * 设置是否是上传图片,
     * true -> 显示红色的叉叉
     * false -> 不显示红色叉叉
     *
     * @param gv
     * @param isUpload
     */
    public GridviewAdapter(Activity mActivity, GridView gv, boolean isUpload) {
        this.mActivity = mActivity;
        this.isUpload = isUpload;
        this.gv = gv;
        data = new ArrayList<>();

        myinflater = LayoutInflater.from(mActivity);

    }

    /**
     * 设置图片的url
     *
     * @param datas
     */
    public void setDatas(List<PictureInfo> datas) {

        this.data = datas;
        notifyDataSetChanged();
    }

    public ArrayList<PictureInfo> getData() {
        return (ArrayList<PictureInfo>) data;

    }

    @Override
    public int getCount() {

        return data != null && data.size() > 0 ? data.size() : 0;
    }

    @Override
    public PictureInfo getItem(int position) {
        return data != null && data.size() > 0 ? data.get(position) : null;
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
        if (data != null) {
            imageUrl = data.get(position);
        }

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

            if (!imageUrl.getImageUrl().equals(Contants.LAST_ADD_IMG)) {
                holder.isUpload.setVisibility(View.VISIBLE);
                holder.isUpload.setOnClickListener(new ImageClickListener(position));

            } else {
                holder.isUpload.setVisibility(View.INVISIBLE);
            }

        } else {
            holder.isUpload.setVisibility(View.GONE);

        }

        if (data != null) {
            if (data.size() == 1) {
                gv.setNumColumns(2);
//            } else if (data.size() == 4) {
//                gv.setNumColumns(2);
            } else {
                gv.setNumColumns(3);
            }
        }


        GridView gv = (GridView) parent;

        int width = gv.getHorizontalSpacing();
        int numColums = gv.getNumColumns();

        int itemWidth = (gv.getWidth() - (numColums - 1) * width - gv.getPaddingLeft() - gv.getPaddingRight()) / numColums;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth, itemWidth);

        holder.imageView.setLayoutParams(params);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//
        holder.imageView.setOnClickListener(new ImageClickListener(position));

        if (isUpload) {
            ImageLoader.getInstance().displayImage(imageUrl.getImageUrl(), holder.imageView);
        } else {
            ImageLoader.getInstance().displayImage(imageUrl.getSmallImageUrl(), holder.imageView);
        }


        return convertView;

    }


    private class ViewHolder {
        public ImageView imageView;
        public ImageView isUpload;

    }


    /**
     * 计算每个item的坐标
     *
     * @param iv_image
     * @param pictureInfoList
     * @param position
     */
    private void setupCoords(ImageView iv_image, List<PictureInfo> pictureInfoList, int position) {
//        x方向的第几个
        int xn = position % 3 + 1;
//        y方向的第几个
        int yn = position / 3 + 1;
//        x方向的总间距
        int h = (xn - 1) * DisplayUtils.dip2px(mActivity, 4);
//        y方向的总间距
        int v = h;
//        图片宽高
        int height = iv_image.getHeight();
        int width = iv_image.getWidth();
//        获取当前点击图片在屏幕上的坐标
        int[] points = new int[2];
        iv_image.getLocationInWindow(points);
//        获取第一张图片的坐标
        int x0 = points[0] - (width + h) * (xn - 1);
        int y0 = points[1] - (height + v) * (yn - 1);
//        给所有图片添加坐标信息
        for (int i = 0; i < pictureInfoList.size(); i++) {
            PictureInfo iamgeInfo = pictureInfoList.get(i);
            iamgeInfo.width = width;
            iamgeInfo.height = height;
            iamgeInfo.x = x0 + (i % 3) * (width + h);
            iamgeInfo.y = y0 + (i / 3) * (height + v) - DisplayUtils.getStatusBarHeight(iv_image);
        }
    }

//    /**
//     * 设置图片的显示大小
//     * @param holder
//     */
//    private void setImageParms(ViewHolder holder) {
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImageWidth, mImageWidth);
//        holder.iv_image.setLayoutParams(params);
//    }


    public class ImageClickListener implements View.OnClickListener {

        private int pos;

        public ImageClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.im_gridview_item_dele://删除按钮

                    data.remove(pos);

                    notifyDataSetChanged();

                    break;

                case R.id.im_gridview_item:
                    setupCoords((ImageView) v,data,pos);
                    Intent intent = new Intent(mActivity, BigPicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Contants.INTENT_IMAGE_INFO_LIST, (Serializable) data);
                    intent.putExtras(bundle);
                    intent.putExtra(Contants.INTENT_CURRENT_ITEM, pos);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);

                    break;


            }
        }
    }


}
