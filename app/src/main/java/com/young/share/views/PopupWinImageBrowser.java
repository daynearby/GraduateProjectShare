package com.young.share.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.young.share.adapter.WellcomePagerAdapter;
import com.young.share.base.BasePopupWin;
import com.young.share.R;
import com.young.share.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片查看
 * <p/>
 * Created by Nearby Yang on 2015-11-18.
 */
public class PopupWinImageBrowser extends BasePopupWin {

    private ViewPager viewPager;
    private List<View> view_list = new ArrayList<>();
    private List<String> im_url;

    private int[] pager = new int[]{R.layout.image_pager_one, R.layout.image_pager_two,
            R.layout.image_pager_three, R.layout.image_pager_four, R.layout.image_pager_five,
            R.layout.image_pager_six};

    private int[] indictor = new int[]{R.id.image_brower_indicator0, R.id.image_brower_indicator1,
            R.id.image_brower_indicator2, R.id.image_brower_indicator3, R.id.image_brower_indicator4,
            R.id.image_brower_indicator5};

    //    private PhotoView[] im_browser;
    private ImageView[] im_indictor;

    public PopupWinImageBrowser(Context context) {
        super(context);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.content_popup_window_iamge_brower;
    }

    @Override
    protected void init() {
        initSize();
    }

    private void initSize() {
        int width = DisplayUtils.getScreenWidthPixels((Activity) context);
        int height = DisplayUtils.getScreenHeightPixels((Activity) context);
        setWidth(width + 10);
        setHeight(height + 10);
//        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.layout_content_window_image_brower);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
//        layoutParams.height = ;
//        layoutParams.width = ;
//
//        relativeLayout.setLayoutParams(layoutParams);
//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

    }

    protected void findView() {
    }

    private void findViews() {
//        im_browser = new PhotoView[im_url.size()];
        im_indictor = new ImageView[im_url.size() + 1];


        for (int i = 0; i < im_url.size(); i++) {
            view_list.add(inflater.inflate(pager[i], null));
            im_indictor[i] = (ImageView) view.findViewById(indictor[i]);
            im_indictor[i].setVisibility(View.VISIBLE);
            // 启用图片缩放功能
//            im_browser[i].enable();

        }
/**
 * // TODO: 15/10/10 图片查看,以及连续查看多张图片,左右拖动


 // 获取图片信息
 Info info = holder.imageView.getInfo();
 // 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
 holder.imageView.animaFrom(info);
 // 从现在的图片变化到所给定的图片信息，用于图片放大后点击缩小到原来的位置，具体使用可以参照demo的使用
 holder.imageView.animaTo(info, new Runnable() {
@Override public void run() {
//动画完成监听
}
});
 */


        WellcomePagerAdapter myPagerAdapter = new WellcomePagerAdapter(context, view_list);
        myPagerAdapter.setData(im_url);
        viewPager = (ViewPager) view.findViewById(R.id.vp_popupwindow_image_brower);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new pageChangeListener());

    }

    @Override
    protected void bindData() {


//        for (int i = 0; i < im_url.size(); i++) {
//            ImageHandlerUtils.loadIamge(context,im_url.get(i), im_browser[i]);
//        }
    }

    /**
     * 设置image 地址
     *
     * @param im_url
     */
    public void setData(List<String> im_url) {
        this.im_url = im_url;
        findViews();
        bindData();
    }

    /**
     * 点击图片弹出对应的图片
     *
     * @param index
     */
    public void setCurrentPager(int index) {
        viewPager.setCurrentItem(index, true);
    }

    /**
     * 显示
     *
     * @param v
     */
    @Override
    public void onShow(View v) {
        showAtLocation(v, Gravity.CENTER, -10, -10);
    }


    private class pageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {

            for (int i = 0; i < im_url.size(); i++) {

                if (arg0 == i) {
                    im_indictor[i].setImageResource(R.drawable.login_point_selected);

                } else {
                    im_indictor[i].setImageResource(R.drawable.bg_login_point);
                }

            }

        }

    }
}
