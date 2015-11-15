package com.young.share.ViewPager;

import android.support.v4.widget.SwipeRefreshLayout;

import com.twotoasters.jazzylistview.JazzyListView;
import com.young.base.BasePager;
import com.young.myCallback.ListViewRefreshListener;
import com.young.share.R;

/**
 * 发现
 * Created by Nearby Yang on 2015-10-09.
 */
public class DiscoverPager extends BasePager {

    // TODO: 15/10/10 绑定listview到当前页面
    private JazzyListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void initView() {
        listView = $(R.id.list_discover);
        swipeRefreshLayout = $(R.id.sw_refresh_pager_discover);

        //ListView的上拉、下拉刷新
        new ListViewRefreshListener(listView, swipeRefreshLayout, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {

            }

            @Override
            public void pullToRefresh() {

            }
        });

    }


    @Override
    public void bindData() {

    }


}
