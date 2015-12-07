package com.young.share.ViewPager;

import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;

import com.twotoasters.jazzylistview.JazzyListView;
import com.young.base.BasePager;
import com.young.share.R;
import com.young.thread.MyRunnable;

/**
 * 商家优惠
 * Created by Nearby Yang on 2015-10-09.
 */
public class DiscountPager extends BasePager {

    private SwipeRefreshLayout swipeRefreshLayout;
    private JazzyListView listview;

    @Override
    public void initData() {

        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {


            }
        }));

    }

    @Override
    public void initView() {
        swipeRefreshLayout = $(R.id.sw_discount_refresh);
        listview = $(R.id.list_discount);



    }


    @Override
    public void bindData() {


    }

    @Override
    public void handler(Message msg) {


    }
}
