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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.R;

import java.util.List;

/**
 * 需要传入图片的url
 *
 * Created by Nearby Yang on 2015-06-28.
 */
public class myGridViewAdapter extends BaseAdapter {

    private List<String> data ;
    private Context ctx;
    private LayoutInflater myinflater;

    public myGridViewAdapter(Context ctx) {
        this.ctx = ctx;
        myinflater = LayoutInflater.from(ctx);

    }

    public void setDatas(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
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
            convertView = myinflater.inflate(R.layout.item_gridview, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.im_gridview_item);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }


        GridView gv = (GridView) parent;

        int width = gv.getHorizontalSpacing();
        int numColums = gv.getNumColumns();

        int itemWidth = (gv.getWidth() - (numColums - 1) * width - gv.getPaddingLeft() - gv.getPaddingRight()) / numColums;

        LayoutParams params = new LayoutParams(itemWidth, itemWidth);

        holder.imageView.setLayoutParams(params);

        ImageLoader.getInstance().displayImage(imageUrl,holder.imageView);

        return convertView;

    }


    private class ViewHolder {
        public ImageView imageView;

    }
}
