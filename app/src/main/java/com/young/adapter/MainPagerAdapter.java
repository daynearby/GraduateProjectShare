package com.young.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.young.config.Contants;
import com.young.share.fragment.DiscountFragment;
import com.young.share.fragment.DiscoverFragment;
import com.young.share.fragment.RankFragment;
import com.young.utils.LogUtils;

import java.util.List;

public class MainPagerAdapter extends PagerAdapter {

//    private Context context;
    private List<Fragment> fragmentList;
    //    private DiscountPager discountPager;
//    private DiscoverPager discoverPager;
//    private RankPager rankPager;
    private FragmentManager fragmentManager;
    private OnPageSelected onPageSelected; //滑动的回调
    private int currentPageIndex = 0; // 当前page索引（切换之前）
    private RankFragment rankFragment;

    public MainPagerAdapter( List<Fragment> fragmentList, FragmentManager fragmentManager,
                            ViewPager viewPager, OnPageSelected onPageSelected) {
        this.fragmentList = fragmentList;
        this.fragmentManager = fragmentManager;
        this.onPageSelected = onPageSelected;

        viewPager.setAdapter(this);
        viewPager.addOnPageChangeListener(new changListener());

    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {


        container.removeView(fragmentList.get(position).getView());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Fragment fragment = fragmentList.get(position);

        if (!fragment.isAdded()) { // 如果fragment还没有added
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            ft.commit();
            /**
             * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
             * 会在进程的主线程中，用异步的方式来执行。
             * 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
             * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
             */
            fragmentManager.executePendingTransactions();
        }

        if (fragment.getView() != null) {
            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
        }


        return fragment.getView();
    }


    /**
     * 刷新ui
     *
     * @param requestCode 广播得到的请求码
     *
     */
    public void refreshUI(int requestCode) {
        LogUtils.logD("pageAdapter refresh UI .code = " + requestCode);

        DiscountFragment discountFragment;
        if (requestCode == Contants.REFRESH_TYPE_DISCOUNT) {

            discountFragment = (DiscountFragment) fragmentList.get(0);
            discountFragment.getRemoteData();

        } else {
            DiscoverFragment discoverFragment = (DiscoverFragment) fragmentList.get(1);
            discoverFragment.initData();

        }

    }

    /**
     * 滑动监听
     * 需要添加回调
     */
    private class changListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            fragmentList.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
            //        fragments.get(currentPageIndex).onStop(); // 调用切换前Fargment的onStop()
            if (fragmentList.get(position).isAdded()) {
                //            fragments.get(i).onStart(); // 调用切换后Fargment的onStart()
                fragmentList.get(position).onResume(); // 调用切换后Fargment的onResume()
            }
            currentPageIndex = position;

            if (onPageSelected != null) {
                onPageSelected.onselected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 滑动的回调
     */
    public interface OnPageSelected {
        void onselected(int position);
    }
}
