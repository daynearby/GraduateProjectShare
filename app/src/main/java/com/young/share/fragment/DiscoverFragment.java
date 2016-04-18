package com.young.share.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.MessageDetailActivity;
import com.young.share.R;
import com.young.share.adapter.DiscoverAdapter;
import com.young.share.base.BaseFragment;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.gson.ShareMessageList;
import com.young.share.network.BmobApi;
import com.young.share.network.NetworkReuqest;
import com.young.share.shareSocial.SocialShareByIntent;
import com.young.share.shareSocial.SocialShareManager;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.CommonUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 发现
 * Created by Nearby Yang on 2015-12-09.
 */
public class DiscoverFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private DiscoverAdapter adapter;
    private List<ShareMessage_HZ> dataList = new ArrayList<>();
    private ImageView tipsIm;
    private ListView listView;

    private int startIndex = 0;
    private int endIndex = 15;
    private int PUSH_TIMES = 0;//下拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据

    private int startRow = 0;//从第一条开始
    private boolean isFirstIn = true;//第一次进入该界面
    private static final int FIRST_GETDATA = 0x1001;
    private static final int GET_LOACTIOPN_DATA = 0x1002;
    private static final int HANDLER_GET_DATA = 0x1003;
    private static final int MESSAGE_NO_MORE_DATA = 0x1004;
    private static final int MESSAGE_LOAD_DATA_FAILURE = 0x1005;

    /**
     * 默认构造函数
     */
    public DiscoverFragment() {

    }

    @Override
    protected void onSaveState(Bundle outState) {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {

    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    public void initData() {

//        getDataFromLocat();//先从本地加载数据
        dataList = (List<ShareMessage_HZ>) app.getCacheInstance().getAsObject(Contants.ACAHE_KEY_DISCOVER);

        if (CommonUtils.isNetworkAvailable()) {//有网络
////
//            threadPool.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
//                @Override
//                public void running() {
//                    getDataFromRemote();
//                }
//            }));
            //
            initDataByThread();

        } else {
            SVProgressHUD.showInfoWithStatus(context,
                    getString(R.string.without_network));
        }

    }

    /**
     * 通过一个新的线程进行获取数据
     */
    private void initDataByThread() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getDataFromRemote();
//            }
//        }).start();

        getDataFromRemote();
    }

    @Override
    public void initView() {
        adapter = new DiscoverAdapter(context);
        listView = $(R.id.list_discover);
        swipeRefreshLayout = $(R.id.sw_refresh_pager_discover);
        tipsIm = $(R.id.im_discover_tips);

    }

    @Override
    public void bindData() {
//
        listView.setAdapter(adapter);
        adapter.bindListView(listView);

        //下拉上拉，点击
        setListPullAndClickListener();

        swipeRefreshLayout.setRefreshing(true);

        if (isFirstIn) {
            swipeRefreshLayout.setRefreshing(true);
            isFirstIn = false;
        }

        if (dataList != null && dataList.size() > 0) {
            mhandler.sendEmptyMessage(HANDLER_GET_DATA);
        }

    }

    /**
     * 下拉刷新、上拉加载更多
     * 点击事件
     */
    private void setListPullAndClickListener() {
        //ListView的上拉、下拉刷新
        new ListViewRefreshListener(listView, swipeRefreshLayout,
                new ListViewRefreshListener.RefreshListener() {
                    @Override
                    public void pushToRefresh() {//上拉刷新
//                            startRow += Contants.PAGER_NUMBER;

                        if (dataList.size() > Contants.PAGE_SIZE * PUSH_TIMES) {

                            endIndex = dataList.size() < Contants.PAGE_SIZE +
                                    Contants.PAGE_SIZE * PUSH_TIMES ? dataList.size() :
                                    Contants.PAGE_SIZE + Contants.PAGE_SIZE * PUSH_TIMES;

                            adapter.setData(dataList.subList(startIndex, endIndex));

                            PUSH_TIMES++;

                        } else {
                            isGetMore = true;
//                                Toast.makeText(ctx, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                            startRow = dataList.size();
                            getDataFromRemote();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        LogUtils.d("上拉刷新");
                    }

                    @Override
                    public void pullToRefresh() {
                        LogUtils.d("下拉刷新");
                        PUSH_TIMES = 1;
                        startRow = 0;
                        isGetMore = false;

                        getDataFromRemote();


                    }
                });

        //item点击事件监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isFirstIn = true;
                Bundle bundle = new Bundle();
                bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_DISCOVER_ACTIVITY);
                bundle.putSerializable(Contants.INTENT_KEY_DISCOVER, dataList.get(position));

                CommonFunctionUtils.startActivity(context, bundle, MessageDetailActivity.class);
            }
        });
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_content_copy://复制文本
                StringUtils.CopyText(context, adapter.getContentString());
                return true;

            case R.id.menu_content_share://分享文本
                SocialShareManager.shareText(context, adapter.getContentString());
                return true;
            case R.id.menu_image_save://保存图片
                NetworkReuqest.call2(context, adapter.getImageUrl());
                return true;


            case R.id.menu_image_share_all://分享全部图片
//下载图片
                SocialShareByIntent.downloadImagesAndShare(context, adapter.getImageList());
//                SocialShareManager.shareImage(context, discAdapter.getImageUrl());

             return true;
            case R.id.menu_image_share_singal://分享打仗图片
                SocialShareByIntent.downloadImageAndShare(context, adapter.getImageUrl());
                return true;

        }

        return super.onContextItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(" discover " + resultCode);
        if (resultCode == Contants.RESULT_SHARE_DISCOVER) {
            //发送成功进行刷新
            if (data.getBooleanExtra(Contants.INTENT_KEY_REFRESH,false)){
                getDataFromRemote();
            }

        }

    }

    @Override
    public void handler(Message msg) {
        switch (msg.what) {
            case GET_LOACTIOPN_DATA:

                refreshUI();

                break;
//            case FIRST_GETDATA:
//                LogUtils.d("获取数据");
//                break;
            case HANDLER_GET_DATA:

                if (dataList != null && dataList.size() > 0) {
                    tipsIm.setVisibility(View.GONE);
                    refreshUI();
                } else {
                    tipsIm.setVisibility(View.VISIBLE);
                    tipsIm.setImageResource(R.drawable.icon_conten_empty);
                }
                break;

            case MESSAGE_NO_MORE_DATA://上拉刷新，没有更多的信息
                Toast.makeText(context, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                break;

            case MESSAGE_LOAD_DATA_FAILURE://加载数据失败，例如下拉刷新的时候
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context, R.string.without_network, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 从远程数据库获取数据，可以通过上、下拉刷新实现触发
     */
    private void getDataFromRemote() {

        JSONObject params = new JSONObject();

        String LONGITUDE = app.getCacheInstance().getAsString(Contants.ACAHE_KEY_LONGITUDE);
        double longitude;
        double latitude;

        if (TextUtils.isEmpty(LONGITUDE)) {
            longitude = 114.424984;
            latitude = 23.043472;

        } else {
            String[] strs = LONGITUDE.split(",");
            longitude = Double.valueOf(strs[0]);
            latitude = Double.valueOf(strs[1]);

        }

        try {
            params.put(Contants.SKIP, String.valueOf(startRow));
            params.put(Contants.PARAMS_LATITUDE, String.valueOf(latitude));
            params.put(Contants.PARAMS_LONGITUDE, String.valueOf(longitude));

        } catch (JSONException e) {

            LogUtils.d("添加 网络参数 失败 = " + e.toString());
        }

        BmobApi.AsyncFunction(context, params, BmobApi.GET_RECENTLY_SHAREMESSAGES,
                ShareMessageList.class, new AsyncListener() {

                    @Override
                    public void onSuccess(Object object) {
                        ShareMessageList shareMessageList = (ShareMessageList) object;

                        if (isGetMore) {//上拉加载更多
                            if (shareMessageList.getShareMessageHzList().size() > 0) {
                                dataList.addAll(shareMessageList.getShareMessageHzList());
                            } else {
                                mhandler.sendEmptyMessage(MESSAGE_NO_MORE_DATA);
                            }

                        } else {

                            if (shareMessageList.getShareMessageHzList().size() > 0) {
                                dataList = shareMessageList.getShareMessageHzList();
                                //保存数据到本地数据库
//                            saveData(dataList);
                                app.getCacheInstance().put(Contants.ACAHE_KEY_DISCOVER, (Serializable) dataList);
                            }

                        }
                        mhandler.sendEmptyMessage(HANDLER_GET_DATA);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        mhandler.sendEmptyMessage(MESSAGE_LOAD_DATA_FAILURE);
                        LogUtils.e("load data failure ! code = " + code + " message = " + msg);
                    }
                }
        );


    }

//    /**
//     * 格式化数据
//     * 存储到本地
//     *
//     * @param shareList
//     */
//    private void saveData(final List<ShareMessage_HZ> shareList) {
//
//
//        for (ShareMessage_HZ share : shareList) {
//
//            //分享信息
//            ShareMessage shareMessageHZ = DataFormateUtils.formateShareMessae(share);
//
//            //用户信息
//            User u = DataFormateUtils.formateUser(share.getMyUserId());
////保存
//            u.save();
//
//            shareMessageHZ.setUserId(u);
//
//            DBUtils.saveShMessages(shareMessageHZ);
//
//
//        }
//
////                mhandler.sendEmptyMessage(FIRST_GETDATA);
//
//    }

    /**
     * 刷新列表，最新数据
     */
    private void refreshUI() {

        if (isGetMore) {
            int tempEnd = endIndex;
            endIndex = dataList.size() < (PUSH_TIMES + 1) * Contants.PAGE_SIZE ?
                    dataList.size() : (PUSH_TIMES + 1) * Contants.PAGE_SIZE;

            adapter.getData().addAll(dataList.subList(tempEnd, endIndex));
            adapter.notifyDataSetChanged();

        } else {
            endIndex = dataList.size() < Contants.PAGE_SIZE ? dataList.size() : endIndex;

            adapter.setData(dataList.subList(startIndex, endIndex));

        }

        //停止刷新动画
        swipeRefreshLayout.setRefreshing(false);
    }

//    /**
//     * 从本地获取数据
//     */
//    private void getDataFromLocat() {
//
//        dataList = DBUtils.getShareMessages();
//        if (dataList != null && dataList.size() > 0)
//            mhandler.sendEmptyMessage(GET_LOACTIOPN_DATA);
//
//
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.d("onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.d("onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d("onDestroyView");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d("onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("onStop");
    }
}
