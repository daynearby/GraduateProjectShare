package com.young.share.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.gc.flashview.FlashView;
import com.gc.flashview.constants.EffectConstants;
import com.gc.flashview.listener.FlashViewListener;
import com.young.share.DiscoutDetailActivity;
import com.young.share.R;
import com.young.share.adapter.DiscountAdapter;
import com.young.share.base.BaseFragment;
import com.young.share.config.ApplicationConfig;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.model.Advertisement;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.gson.AdvertismentList;
import com.young.share.model.gson.DiscountMessageList;
import com.young.share.network.BmobApi;
import com.young.share.network.NetworkReuqest;
import com.young.share.utils.CommonUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.LogUtils;
import com.young.share.views.Dialog4Tips;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 商家优惠
 * <p/>
 * Created by Nearby Yang on 2015-12-09.
 */
public class DiscountFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listview;
    private DiscountAdapter discAdapter;
    private FlashView flashView;
    private View headerView;
    private ImageView tipsIm;

    private int startIndex = 0;
    private int endIndex = 20;
    private int skip = 0;
    private int PUSH_TIMES = 0;//下拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据
    private boolean moreData = false;//从远程数据库获取更多数据
    private List<DiscountMessage_HZ> dataList = new ArrayList<>();
    private List<Advertisement> adList = new ArrayList<>();
    private List<String> imageUrlList = new ArrayList<>();
    private boolean isFirstIn = true;//第一次进入该界面

    private static final int MESSAGES_NEW_MESSAGE = 0x01;//最新消息
    private static final int MESSAGES_GET_AD = 0x02;//获取广告信息
    private static final int MESSAGES_NO_MORE_DATA = 0x03;//上拉刷新，没有更多数据了
    private static final int MESSAGES_LOAD_DATA_NULL = 0x04;//下拉刷新，没有更多数据了
    private static final int MESSAGES_LOAD_DATA_FAILURE = 0x05;//加载数据失败


    public DiscountFragment() {

    }


    @Override
    protected void onSaveState(Bundle outState) {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {

    }

    @Override
    protected void getDataFromBunlde(Bundle bundle) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_discount;
    }

    @Override
    public void initData() {
        dataList = (List<DiscountMessage_HZ>) app.getCacheInstance().getAsObject(Contants.ACAHE_KEY_DISCOUNT);
        adList = (List<Advertisement>) app.getCacheInstance().getAsObject(Contants.ACAHE_KEY_ADVERTISMENT);

//        threadPool.startTask(new MyRunnable() {
//            @Override
//            public void run() {
//
//            }
//        });

        initDataByThread();
    }

    /**
     * 获取数据
     */
    private void initDataByThread() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                     /*数据*/
//                getRemoteData();
//        /*广告*/
//                getADInfo();
//            }
//        }).start();
                      /*数据*/
        getRemoteData();
        /*广告*/
        getADInfo();
    }

    @Override
    public void initView() {

        headerView = LayoutInflater.from(context).inflate(R.layout.item_flashview, null);
        flashView = (FlashView) headerView.findViewById(R.id.flash_view);
        swipeRefreshLayout = $(R.id.sw_discount_refresh);
        listview = $(R.id.list_discount);
        tipsIm = $(R.id.im_discount_tips);

        discAdapter = new DiscountAdapter(context);


    }


    @Override
    public void bindData() {
           /*设置flashview宽高*/
        ViewGroup.LayoutParams layoutParams = flashView.getLayoutParams();
        layoutParams.height = DisplayUtils.getScreenWidthPixels((Activity) context) / 2;
        flashView.setOnPageClickListener(flashViewListener);
        flashView.setEffect(EffectConstants.DEPTH_PAGE_EFFECT);/*淡出淡入*/

        listview.addHeaderView(headerView);
        listview.setOnItemClickListener(new itemClick());
        listview.setAdapter(discAdapter);
          /*上拉刷新*/
        pullToRefresh();

//        Log.d(tag, "bindData");
        swipeRefreshLayout.setRefreshing(true);

        if (dataList != null && dataList.size() > 0) {
            mhandler.sendEmptyMessage(MESSAGES_NEW_MESSAGE);
        }
   /*根据缓存数据，有数据那么就先刷新*/
        if (adList != null && adList.size() > 0) {
            /*如果异步任务先完成，返回数据，那么需要*/
            if (imageUrlList != null && imageUrlList.size() > 0) {
                imageUrlList.clear();
            }
            for (Advertisement ad : adList) {

                imageUrlList.add(ad.getAdImage().getFileUrl(context));
            }
            mhandler.sendEmptyMessage(MESSAGES_GET_AD);
        }

        if (isFirstIn) {
            swipeRefreshLayout.setRefreshing(true);
            isFirstIn = false;
        }
    }

    @Override
    public void handler(Message msg) {
        switch (msg.what) {
            case MESSAGES_NEW_MESSAGE://最新消息
                tipsIm.setVisibility(View.GONE);
                refreshUI();

                break;
            case MESSAGES_GET_AD:/*广告*/

                flashView.setImageUris(imageUrlList);

                break;

            case MESSAGES_NO_MORE_DATA://加载更多数据，没有更多数据
                Toast.makeText(context, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                break;

            case MESSAGES_LOAD_DATA_NULL://第一次加载数据，没有更多的数据
                tipsIm.setVisibility(View.VISIBLE);
                tipsIm.setImageResource(R.drawable.icon_conten_empty);
                break;

            case MESSAGES_LOAD_DATA_FAILURE://加载数据失败，如下拉刷新
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                break;
        }
    }

    /**
     * 上拉刷新
     */
    private void pullToRefresh() {
        //下拉与上拉
        new ListViewRefreshListener(listview, swipeRefreshLayout, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {

                if (CommonUtils.isNetworkAvailable()) {//有网络

//                    skip += Contants.PAGER_NUMBER;
                    if (dataList != null) {


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
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void pullToRefresh() {
                skip = 0;
                isGetMore = false;
                getRemoteData();
            }
        }

        );

    }


    /**
     * 获取数据
     */
    public void getRemoteData() {


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
                                mhandler.sendEmptyMessage(MESSAGES_NEW_MESSAGE);

                            } else {
                             mhandler.sendEmptyMessage(MESSAGES_NO_MORE_DATA);

                            }

                        } else {

                            if (disMessageList.getDiscountList().size() > 0) {

                                dataList = disMessageList.getDiscountList();
                                app.getCacheInstance().put(Contants.ACAHE_KEY_DISCOUNT, (Serializable) dataList);
                                mhandler.sendEmptyMessage(MESSAGES_NEW_MESSAGE);

                            } else {
                              mhandler.sendEmptyMessage(MESSAGES_LOAD_DATA_NULL);
                            }

                        }


                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        mhandler.sendEmptyMessage(MESSAGES_LOAD_DATA_FAILURE);
                        LogUtils.d("get discountMessage failure. code  = " + code + " message = " + msg);

                    }
                }
        );

    }

    /**
     * 获取最新的广告信息
     * 进行广告展示
     */
    private void getADInfo() {
//        ?limit=5&order=-createdAt
        HashMap<String, String> params = new HashMap<>();
        params.put(Contants.PARAM_LIMIT, String.valueOf(5));
        params.put(Contants.PARAM_ORDER, "-createdAt");

//        adList
        NetworkReuqest.getHttpsReponse(
                NetworkReuqest.BMOB_HOST + NetworkReuqest.ADVERTISERMENT,
                params, AdvertismentList.class,
                new NetworkReuqest.SimpleRequestCallback<AdvertismentList>() {
                    @Override
                    public void response(AdvertismentList advertismentList) {
                        if (advertismentList != null && advertismentList.getCollecList().size() > 0) {

                            adList = advertismentList.getCollecList();

                            if (imageUrlList != null && imageUrlList.size() > 0) {
                                imageUrlList.clear();
                            }
                            for (Advertisement ad : advertismentList.getCollecList()) {
                                imageUrlList.add(ad.getAdImage().getFileUrl(context));
                            }

                            /*建立缓存，并且刷新*/
                            if (adList != null && adList.size() > 0) {
                                app.getCacheInstance().put(Contants.ACAHE_KEY_ADVERTISMENT, (Serializable) adList);
                                mhandler.sendEmptyMessage(MESSAGES_GET_AD);
                            }
                        }
                    }

                });

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


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (ApplicationConfig.getInstance().getCUser() == null) {//用户没有登录

                Dialog4Tips.loginFunction((Activity) context);

            } else {//用户已经登录

                isFirstIn = true;
                Bundle bundle = new Bundle();

                bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, DataFormateUtils.formateDataDiscount(dataList.get(position)));
                CommonFunctionUtils.startActivity(context, bundle, DiscoutDetailActivity.class);

            }
        }
    }

    /**
     * 广告页面点击监听
     */
    private FlashViewListener flashViewListener = new FlashViewListener() {
        @Override
        public void onClick(int position) {
//            Toast.makeText(context, "点击广告  " + position, Toast.LENGTH_SHORT).show();

            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(adList.get(position).getAdLink()));
            it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
            startActivity(it);
        }
    };
}
