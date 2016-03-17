package com.young.share.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.DiscoutDetailActivity;
import com.young.share.R;
import com.young.share.adapter.DiscountAdapter;
import com.young.share.base.BaseFragment;
import com.young.share.config.Contants;
import com.young.share.model.DiscountMessageList;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.MyUser;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.network.BmobApi;
import com.young.share.thread.MyRunnable;
import com.young.share.utils.CommonUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.LogUtils;
import com.young.share.views.Dialog4Tips;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * 商家优惠
 * <p/>
 * Created by Nearby Yang on 2015-12-09.
 */
@SuppressLint("ValidFragment")
public class DiscountFragment extends BaseFragment {


    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listview;
    private DiscountAdapter discAdapter;

    private int startIndex = 0;
    private int endIndex = 20;
    private int skip = 0;
    private int PUSH_TIMES = 0;//下拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据
    private List<DiscountMessage_HZ> dataList = new ArrayList<>();

    private static final int MESSAGES_NEW_MESSAGE = 101;//最新消息
    private boolean isFirstIn = true;//第一次进入该界面
    private static final String tag = "discount";

    public DiscountFragment() {
        super();
    }

    public DiscountFragment(Context context) {
        super(context);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_discount;
    }

    @Override
    public void initData() {
        dataList = (List<DiscountMessage_HZ>) app.getCacheInstance().getAsObject(Contants.ACAHE_KEY_DISCOUNT);
        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {
                getRemoteData();

            }
        }));

    }

    @Override
    public void initView() {


        swipeRefreshLayout = $(R.id.sw_discount_refresh);
        listview = $(R.id.list_discount);
        discAdapter = new DiscountAdapter(context);
        listview.setOnItemClickListener(new itemClick());

        listview.setAdapter(discAdapter);

        //下拉与上拉
        new ListViewRefreshListener(listview, swipeRefreshLayout, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {

                if (CommonUtils.isNetworkAvailable(context)) {//有网络

//                    skip += Contants.PAGER_NUMBER;

                    if (dataList.size() > Contants.PAGE_SIZE * PUSH_TIMES) {

                        endIndex = dataList.size() < Contants.PAGE_SIZE +
                                Contants.PAGE_SIZE * PUSH_TIMES ? dataList.size() :
                                Contants.PAGE_SIZE + Contants.PAGE_SIZE * PUSH_TIMES;

                        discAdapter.setData(dataList.subList(startIndex, endIndex));

                        PUSH_TIMES++;

                    } else {
                        isGetMore = true;
//                                Toast.makeText(context, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                        skip = dataList.size();
                        getRemoteData();
                    }
                } else {
                    SVProgressHUD.showInfoWithStatus(context, getString(R.string.without_network));

                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void pullToRefresh() {
                skip = 0;
                isGetMore = false;
                getRemoteData();
            }
        });

    }


    @Override
    public void bindData() {
//        Log.d(tag, "bindData");
        swipeRefreshLayout.setRefreshing(true);
        if (dataList != null && dataList.size() > 0) {
            mhandler.sendEmptyMessage(MESSAGES_NEW_MESSAGE);
        }
    }

    @Override
    public void handler(Message msg) {
        switch (msg.what) {
            case MESSAGES_NEW_MESSAGE://最新消息

                refreshUI();

                break;
        }
    }


    /**
     * 获取数据
     */
    public void getRemoteData() {
        if (isFirstIn) {
            swipeRefreshLayout.setRefreshing(true);
            isFirstIn = false;
        }
        JSONObject params = new JSONObject();

        try {
            params.put(Contants.SKIP, String.valueOf(skip));

        } catch (JSONException e) {

            LogUtils.d("添加 网络参数 失败 = " + e.toString());
        }

        BmobApi.AsyncFunction(context, params, BmobApi.GET_RECENTLY_DICOUNT,
                DiscountMessageList.class, new AsyncListener() {
                    @Override
                    public void onSuccess(Object object) {

                        @SuppressWarnings("unchecked")
                        DiscountMessageList disMessageList = (DiscountMessageList) object;

                        if (isGetMore) {//上拉获取更多

                            if (disMessageList.getDiscountList().size() > 0) {
                                dataList.addAll(disMessageList.getDiscountList());
                            } else {
                                Toast.makeText(context, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            if (dataList!=null&&dataList.size()>0){
                                dataList.clear();
                            }
                            dataList = disMessageList.getDiscountList();
                        }

                        mhandler.sendEmptyMessage(MESSAGES_NEW_MESSAGE);

                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        LogUtils.d("get discountMessage failure. code  = " + code + " message = " + msg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }


    /**
     * 刷新列表，最新数据
     */
    private void refreshUI() {

        if (isGetMore) {

            endIndex = dataList.size() < (PUSH_TIMES + 1) * Contants.PAGE_SIZE ?
                    dataList.size() : (PUSH_TIMES + 1) * Contants.PAGE_SIZE;

        } else {

            endIndex = dataList.size() < Contants.PAGE_SIZE ? dataList.size() : endIndex;

        }

        discAdapter.setData(dataList.subList(startIndex, endIndex));
        //停止刷新动画
        swipeRefreshLayout.setRefreshing(false);
    }


    /**
     * listview item 点击事件
     */
    private class itemClick implements AdapterView.OnItemClickListener {

        private MyUser cu = BmobUser.getCurrentUser(context, MyUser.class);

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (cu == null) {//用户没有登录

                Dialog4Tips.loginFunction((Activity) context);

            } else {//用户已经登录

                isFirstIn = true;
                Bundle bundle = new Bundle();

                bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, DataFormateUtils.formateDataDiscount(dataList.get(position)));
                LocationUtils.startActivity(context, bundle, DiscoutDetailActivity.class);

            }
        }
    }
}
