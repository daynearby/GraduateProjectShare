package com.young.share.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.SlideInEffect;
import com.young.share.MessageDetail;
import com.young.share.R;
import com.young.share.adapter.DiscoverAdapter;
import com.young.share.base.BaseFragment;
import com.young.share.config.Contants;
import com.young.share.model.ShareMessageList;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.dbmodel.ShareMessage;
import com.young.share.model.dbmodel.User;
import com.young.share.myInterface.GotoAsyncFunction;
import com.young.share.myInterface.ListViewRefreshListener;
import com.young.share.network.BmobApi;
import com.young.share.thread.MyRunnable;
import com.young.share.utils.CommonUtils;
import com.young.share.utils.DBUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现
 * Created by Nearby Yang on 2015-12-09.
 */
@SuppressLint("ValidFragment")
public class DiscoverFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private DiscoverAdapter listviewAdapter;
    private  List<ShareMessage_HZ> dataList = new ArrayList<>();

    private static final int FIRST_GETDATA = 0x1001;
    private static final int GET_LOACTIOPN_DATA = 0x1002;
    private static final int HANDLER_GET_DATA = 0x1003;

    private int startIndex = 0;
    private int endIndex = 20;
    private int PUSH_TIMES = 0;//下拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据

    private int startRow = 0;//从第一条开始
    private boolean isFirstIn = true;//第一次进入该界面
    private static final String tag = "discover";

    public DiscoverFragment(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    public void initData() {


//        getDataFromLocat();//没有网络
        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {
                if (CommonUtils.isNetworkAvailable(context)) {//有网络

                    getDataFromRemote();

                } else {
                    SVProgressHUD.showInfoWithStatus(context,
                            getString(R.string.without_network));

                }

            }
        }));

    }

    @Override
    public void initView() {
        listviewAdapter = new DiscoverAdapter(context);

        JazzyListView listView = $(R.id.list_discover);
        swipeRefreshLayout = $(R.id.sw_refresh_pager_discover);

        listView.setAdapter(listviewAdapter);
        listView.setTransitionEffect(new SlideInEffect());

        //ListView的上拉、下拉刷新
        new ListViewRefreshListener(listView, swipeRefreshLayout,
                new ListViewRefreshListener.RefreshListener() {
                    @Override
                    public void pushToRefresh() {//上拉刷新
                        if (CommonUtils.isNetworkAvailable(context)) {//有网络
//                            startRow += Contants.PAGER_NUMBER;

                            if (dataList.size() > Contants.PAGE_SIZE * PUSH_TIMES) {

                                endIndex = dataList.size() < Contants.PAGE_SIZE +
                                        Contants.PAGE_SIZE * PUSH_TIMES ? dataList.size() :
                                        Contants.PAGE_SIZE + Contants.PAGE_SIZE * PUSH_TIMES;

                                listviewAdapter.setData(dataList.subList(startIndex, endIndex));

                                PUSH_TIMES++;

                            } else {
                                isGetMore = true;
//                                Toast.makeText(ctx, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                                startRow = dataList.size();
                                getDataFromRemote();
                            }
                        } else {
                            SVProgressHUD.showInfoWithStatus(context, getString(R.string.without_network));
                            getDataFromLocat();//没有网络
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        LogUtils.logD("上拉刷新");
                    }

                    @Override
                    public void pullToRefresh() {
                        LogUtils.logD("下拉刷新");
                        PUSH_TIMES = 1;
                        startRow = 0;
                        isGetMore = false;

                        if (CommonUtils.isNetworkAvailable(context)) {//有网络

                            getDataFromRemote();
                        } else {
                            SVProgressHUD.showInfoWithStatus(context, getString(R.string.without_network));
                            getDataFromLocat();//没有网络
                        }

                    }
                });

        //item点击事件监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isFirstIn = true;
                Bundle bundle = new Bundle();
                bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_DISCOVER_ACTIVITY);
                bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, dataList.get(position));

                LocationUtils.startActivity(context, bundle, MessageDetail.class);
            }
        });


    }

    @Override
    public void bindData() {

        if (isFirstIn) {
            swipeRefreshLayout.setRefreshing(true);
            isFirstIn = false;
        }

    }

    @Override
    public void handler(Message msg) {
        switch (msg.what) {
            case GET_LOACTIOPN_DATA:

                refreshUI();

                break;
            case FIRST_GETDATA:
                LogUtils.logD("获取数据");
                break;
            case HANDLER_GET_DATA:
                if (dataList != null && dataList.size() > 0) {

                    refreshUI();
                } else {
                   LinearLayout linearLayout=$(R.id. llayout_disvocer_bg);
                    linearLayout.setBackgroundResource(R.drawable.icon_conten_empty);
                }
                break;

        }
    }

    /**
     *
     */
    private void getDataFromRemote() {


        JSONObject params = new JSONObject();

        try {
            params.put(Contants.SKIP, String.valueOf(startRow));
        } catch (JSONException e) {

            LogUtils.logD("添加 网络参数 失败 = " + e.toString());
        }

        BmobApi.AsyncFunction(context, params, BmobApi.GET_RECENTLY_SHAREMESSAGES,
                ShareMessageList.class, new GotoAsyncFunction() {
                    @Override
                    public void onSuccess(Object object) {
                        @SuppressWarnings("unchecked")
                        ShareMessageList shareMessageList = (ShareMessageList) object;


                        if (isGetMore) {
                            if (shareMessageList.getShareMessageHzList().size() > 0) {
                                dataList.addAll(shareMessageList.getShareMessageHzList());
                            } else {
                                Toast.makeText(context, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            dataList.clear();
                            dataList = shareMessageList.getShareMessageHzList();
                        }
                        mhandler.sendEmptyMessage(HANDLER_GET_DATA);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );


    }

    /**
     * 格式化数据
     * 存储到本地
     *
     * @param shareList
     */
    private void saveData(final List<ShareMessage_HZ> shareList) {


        threadUtils.addTask(new MyRunnable(new MyRunnable.GotoRunnable() {

            @Override
            public void running() {

                for (ShareMessage_HZ share : shareList) {

                    //分享信息
                    ShareMessage shareMessageHZ = DataFormateUtils.formateShareMessage(share);

                    //用户信息
                    User u = DataFormateUtils.formateUser(share.getUserId());
//保存
                    u.save();

                    shareMessageHZ.setUserId(u);

                    DBUtils.saveShMessages(shareMessageHZ);


                }

                mhandler.sendEmptyMessage(FIRST_GETDATA);

            }
        }));


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

        listviewAdapter.setData(dataList.subList(startIndex, endIndex));
        //停止刷新动画
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 从本地获取数据
     */
    private void getDataFromLocat() {

        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {

            @Override
            public void running() {
                dataList = DBUtils.getShareMessages();
                mhandler.sendEmptyMessage(GET_LOACTIOPN_DATA);

            }
        }));

    }


}