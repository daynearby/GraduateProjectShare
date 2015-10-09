package com.young.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.young.share.ViewPager.DiscountPager;
import com.young.share.ViewPager.DiscoverPager;
import com.young.share.ViewPager.RankPager;

import java.util.List;

public class MainPagerAdapter extends PagerAdapter {

    private Context context;
    private List<View> views;
    private DiscountPager discountPager;
    private DiscoverPager discoverPager;
    private RankPager rankPager;

    public MainPagerAdapter(Context context, List<View> views) {
        this.context = context;
        this.views = views;

    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        switch (position) {
            case 0:
                initDiscountPager(view);
                break;
            case 1:
                initDiscoverPager(view);
                break;
            case 2:
                initRankPager(view);
                break;
        }
        return view;
    }
    private void initDiscountPager(View v) {
        discountPager = new DiscountPager();
        discountPager.init(context, v);


    }
  
    private void initDiscoverPager(View v) {
        discoverPager = new DiscoverPager();
        discoverPager.init(context, v);
    }
    private void initRankPager(View v) {
        rankPager = new RankPager();
        rankPager.init(context, v);
    }

  


}
