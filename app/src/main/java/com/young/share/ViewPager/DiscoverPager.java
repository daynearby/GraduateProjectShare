package com.young.share.ViewPager;

import android.support.v4.widget.SwipeRefreshLayout;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.SlideInEffect;
import com.young.adapter.DiscoListViewAdapter;
import com.young.base.BasePager;
import com.young.config.Contants;
import com.young.model.ShareMessageList;
import com.young.model.ShareMessage_HZ;
import com.young.model.dbmodel.ShareMessage;
import com.young.myCallback.GotoAsyncFunction;
import com.young.myCallback.ListViewRefreshListener;
import com.young.network.BmobApi;
import com.young.share.R;
import com.young.utils.CommonUtils;
import com.young.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 发现
 * Created by Nearby Yang on 2015-10-09.
 */
public class DiscoverPager extends BasePager {

    // TODO: 15/10/10 分页显示
    private JazzyListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DiscoListViewAdapter listviewAdapter;


    private int startRow = 0;//从第一条开始

    @Override
    public void initView() {

        listviewAdapter = new DiscoListViewAdapter(ctx);

        listView = $(R.id.list_discover);
        swipeRefreshLayout = $(R.id.sw_refresh_pager_discover);

        listView.setAdapter(listviewAdapter);
        listView.setTransitionEffect(new SlideInEffect());

        //ListView的上拉、下拉刷新
        new ListViewRefreshListener(listView, swipeRefreshLayout, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {
                startRow = startRow + Contants.PAGER_NUMBER;
            }

            @Override
            public void pullToRefresh() {

                getDataFromRemote();
            }
        });

    }


    @Override
    public void bindData() {

        if (CommonUtils.isNetworkAvailable(ctx)) {//有网络
            getDataFromRemote();
        } else {
            SVProgressHUD.showInfoWithStatus(ctx, ctx.getString(R.string.without_network));
            getDataFromLocat();//没有网络
        }

    }

    private void getDataFromLocat() {
        // TODO: 2015-11-16 从本地数据库上获取数据

    }

    private void getDataFromRemote() {
        // TODO: 2015-11-16 从远程数据库获取新的数据

        JSONObject params = new JSONObject();
        try {
            params.put(Contants.SKIP, String.valueOf(startRow));
        } catch (JSONException e) {
            LogUtils.logD("添加 网络参数 失败 = " + e.toString());
        }

        BmobApi.AsyncFunction(ctx, params, BmobApi.GET_RECENTLY_SHAREMESSAGES, ShareMessageList.class, new GotoAsyncFunction() {
                    @Override
                    public void onSuccess(Object object) {
                        @SuppressWarnings("unchecked")
                        ShareMessageList shareMessageList = (ShareMessageList) object;
                        listviewAdapter.setData(shareMessageList.getShareMessageHzList());
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
         );


    }


}
