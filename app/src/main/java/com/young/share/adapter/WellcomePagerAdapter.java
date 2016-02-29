package com.young.share.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.young.share.config.Contants;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.views.photoview.PhotoView;

import java.util.List;

public class WellcomePagerAdapter extends PagerAdapter {

    private Context ctx;
    private List<View> views;
    private List<String> urlList = null;
    private ImageClickListener imageClickListener;

    public WellcomePagerAdapter(Context ctx, List<View> views) {

        this.ctx = ctx;
        this.views = views;

    }

    /**
     * 加载图片的url
     *
     * @param urlList
     */
    public void setData(List<String> urlList) {
        this.urlList = urlList;
    }

    /**
     * 图片点击监听
     * @param imageClickListener
     */
    public void setImageClickListener(ImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);

        if (urlList != null) {

                    PhotoView photo0 = (PhotoView) view.findViewById(Contants.photoId[position]);
                    // 获取图片信息
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
// 从现在的图片变化到所给定的图片信息，用于图片放大后点击缩小到原来的位置，具体使用可以参照demo的使用
//                    photo.animaTo(info, new Runnable() {
//                        @Override
//                        public void run() {
//                            //动画完成监听
//                        }
//                    });
                    photo0.setOnClickListener(new OnClick());
                    ImageHandlerUtils.loadIamge(ctx, urlList.get(position), photo0, false);

        }
        return view;

    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {


        return (arg0 == arg1);
    }

    /**
     * 图片点击事件
     */
    private class OnClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (imageClickListener != null) {
                imageClickListener.onClick();
            }
        }
    }

    public interface ImageClickListener{
        void onClick();
    }

}
