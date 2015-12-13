package com.young.share.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.SlideInEffect;
import com.young.adapter.DiscoverAdapter;
import com.young.base.BaseFragment;
import com.young.config.Contants;
import com.young.model.ShareMessageList;
import com.young.model.ShareMessage_HZ;
import com.young.model.User;
import com.young.model.dbmodel.ShareMessage;
import com.young.myInterface.GotoAsyncFunction;
import com.young.myInterface.ListViewRefreshListener;
import com.young.network.BmobApi;
import com.young.share.MessageDetail;
import com.young.share.R;
import com.young.thread.MyRunnable;
import com.young.utils.CommonUtils;
import com.young.utils.DBUtils;
import com.young.utils.LocationUtils;
import com.young.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现
 * Created by Nearby Yang on 2015-12-09.
 */
public class DiscoverFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private DiscoverAdapter listviewAdapter;
    private static List<ShareMessage_HZ> dataList = new ArrayList<>();

    private static final int FIRST_GETDATA = 0x1001;
    private static final int GET_LOACTIOPN_DATA = 0x1002;

    private int startIndex = 0;
    private int endIndex = 20;
    private int PUSH_TIMES = 0;//下拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据

    private int startRow = 0;//从第一条开始

    private static final String tag = "discover";

    public DiscoverFragment(Context context) {
        super(context);
    }
// TODO: 2015-12-13 修改结构

    @Override
    public int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    public void initData() {
        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {
                if (CommonUtils.isNetworkAvailable(context)) {//有网络
                    getDataFromRemote();
                } else {
                    SVProgressHUD.showInfoWithStatus(context,
                            getString(R.string.without_network));
                    getDataFromLocat();//没有网络
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_DISCOVER_ACTIVITY);
                bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, dataList.get(position));

                LocationUtils.startActivity(context, bundle, MessageDetail.class);
            }
        });

    }

    @Override
    public void bindData() {

    }

    @Override
    public void handler(Message msg) {
        switch (msg.what) {
            case GET_LOACTIOPN_DATA:

                refreshUI();

                break;
            case FIRST_GETDATA:

                break;

            default:
                LogUtils.logD("收到信息 = " + msg);
                break;
        }
    }

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
                        formatData(shareMessageList.getShareMessageHzList());

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

                        refreshUI();

                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );


    }

    private void formatData(final List<ShareMessage_HZ> shareList) {


        threadUtils.addTask(new MyRunnable(new MyRunnable.GotoRunnable() {

            @Override
            public void running() {

                for (com.young.model.ShareMessage_HZ share : shareList) {
                    //分享信息
                    ShareMessage shareMessageHZ = new ShareMessage();
                    shareMessageHZ.setObjectId(share.getObjectId());
                    shareMessageHZ.setShImgs(String.valueOf(share.getShImgs()));
                    shareMessageHZ.setShContent(share.getShContent());
                    shareMessageHZ.setCreatedAt(share.getCreatedAt());
                    shareMessageHZ.setShLocation(share.getShLocation());
                    shareMessageHZ.setShVisitedNum(String.valueOf(share.getShVisitedNum()));
                    shareMessageHZ.setShCommNum(share.getShCommNum());
                    shareMessageHZ.setShWantedNum(String.valueOf(share.getShWantedNum()));
                    shareMessageHZ.setShTag(share.getShTag());
                    shareMessageHZ.setUpdatedAt(share.getUpdatedAt());
                    //用户信息
                    User user = share.getUserId();
                    com.young.model.dbmodel.User u = new com.young.model.dbmodel.User();

                    u.setCreatedAt(user.getCreatedAt());
                    u.setUpdatedAt(user.getUpdatedAt());
                    u.setAddress(user.getAddress());
                    u.setGender(user.isGender());
                    u.setAge(user.getAge());
                    u.setQq(user.getQq());
                    u.setAvatar(user.getAvatar());
                    u.setSignture(user.getSignture());
                    u.setEmail(user.getEmail());
                    u.setMobilePhoneNumber(user.getMobilePhoneNumber());
                    u.setNickName(user.getNickName());
                    u.setMobilePhoneNumberVerified(user.getMobilePhoneNumberVerified());
                    u.setEmailVerified(user.getEmailVerified());
                    u.setObjectId(user.getObjectId());
                    u.setAccessToken(user.getSessionToken());
                    u.setUsername(user.getUsername());

                    u.save();
//                    boolean usave =
                    shareMessageHZ.setUserId(u);
                    DBUtils.saveShMessages(shareMessageHZ);
//                    boolean shareM =
//                    LogUtils.logD("save result = " + usave + " shareM = " + shareM);


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
        swipeRefreshLayout.setRefreshing(false);
    }

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
