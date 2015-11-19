package com.young.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.young.share.R;
import com.young.utils.ImageHandlerUtils;

import java.util.List;

public class WellcomePagerAdapter extends PagerAdapter {

    private Context ctx;
    private List<View> views;
    private List<String> urlList = null;

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


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);

        if (urlList != null) {
            switch (position) {
                case 0:
                    PhotoView photo0 = (PhotoView) view.findViewById(R.id.im_image_pager_one);
                    photo0.enable();
                    // 获取图片信息
                    Info info0 = photo0.getInfo();
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
                    photo0.animaFrom(info0);
// 从现在的图片变化到所给定的图片信息，用于图片放大后点击缩小到原来的位置，具体使用可以参照demo的使用
//                    photo.animaTo(info, new Runnable() {
//                        @Override
//                        public void run() {
//                            //动画完成监听
//                        }
//                    });
                    ImageHandlerUtils.loadIamge(ctx, urlList.get(0), photo0);
                    break;
                case 1:
                    PhotoView photo1 = (PhotoView) view.findViewById(R.id.im_image_pager_two);
                    photo1.enable();
                    // 获取图片信息
                    Info info1 = photo1.getInfo();
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
                    photo1.animaFrom(info1);

                    ImageHandlerUtils.loadIamge(ctx, urlList.get(1), photo1);
                    break;
                case 2:
                    PhotoView photo2 = (PhotoView) view.findViewById(R.id.im_image_pager_three);
                    photo2.enable();
                    // 获取图片信息
                    Info info = photo2.getInfo();
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
                    photo2.animaFrom(info);


                    ImageHandlerUtils.loadIamge(ctx, urlList.get(2), photo2);
                    break;
                case 3:
                    PhotoView photo3 = (PhotoView) view.findViewById(R.id.im_image_pager_four);
                    photo3.enable();
                    // 获取图片信息
                    Info info3 = photo3.getInfo();
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
                    photo3.animaFrom(info3);

                    ImageHandlerUtils.loadIamge(ctx, urlList.get(3), photo3);
                    break;
                case 4:
                    PhotoView photo4 = (PhotoView) view.findViewById(R.id.im_image_pager_five);
                    photo4.enable();
                    // 获取图片信息
                    Info info4 = photo4.getInfo();
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
                    photo4.animaFrom(info4);

                    ImageHandlerUtils.loadIamge(ctx, urlList.get(4), photo4);
                    break;
                case 5:
                    PhotoView photo5 = (PhotoView) view.findViewById(R.id.im_image_pager_six);
                    photo5.enable();
                    // 获取图片信息
                    Info info5 = photo5.getInfo();
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
                    photo5.animaFrom(info5);

                    ImageHandlerUtils.loadIamge(ctx, urlList.get(5), photo5);

                    break;
            }
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


}
