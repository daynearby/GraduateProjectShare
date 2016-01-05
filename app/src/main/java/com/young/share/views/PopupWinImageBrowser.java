package com.young.share.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.young.share.R;
import com.young.share.adapter.WellcomePagerAdapter;
import com.young.share.base.BasePopupWin;

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
    WindowManager.LayoutParams lp;

    public PopupWinImageBrowser(Context context) {
        super(context);
        lp = ((Activity) context).getWindow().getAttributes();

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
//        int width = DisplayUtils.getScreenWidthPixels((Activity) context);
//        int height = DisplayUtils.getScreenHeightPixels((Activity) context);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);

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


        WellcomePagerAdapter myPagerAdapter = new WellcomePagerAdapter(context, view_list);
        myPagerAdapter.setData(im_url);
        myPagerAdapter.setImageClickListener(new WellcomePagerAdapter.ImageClickListener() {
            @Override
            public void onClick() {
                dismiss();
            }
        });
        viewPager = (ViewPager) view.findViewById(R.id.vp_popupwindow_image_brower);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new pageChangeListener());
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {

                lp.alpha = 1.0f;

                ((Activity) context).getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    protected void bindData() {

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

        lp.alpha = 0.1f;
        ((Activity) context).getWindow().setAttributes(lp);

        showAtLocation(getContentView(), Gravity.CENTER, 0, 0);

        this.update();
    }


    /**
     * 灰色背景效果
     */
    private void windowAlpha() {

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
