package com.young.share.ViewPager;

import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.SlideInEffect;
import com.young.adapter.DiscoListViewAdapter;
import com.young.base.BasePager;
import com.young.config.Contants;
import com.young.model.ShareMessageList;
import com.young.model.ShareMessage_HZ;
import com.young.model.User;
import com.young.model.dbmodel.ShareMessage;
import com.young.myInterface.GotoAsyncFunction;
import com.young.myInterface.ListViewRefreshListener;
import com.young.network.BmobApi;
import com.young.share.R;
import com.young.thread.MyRunnable;
import com.young.utils.CommonUtils;
import com.young.utils.DBUtils;
import com.young.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private static List<ShareMessage_HZ> dataList = new ArrayList<>();

    private static final int FIRST_GETDATA = 0x1001;
    private static final int GET_LOACTIOPN_DATA = 0x1002;

    private int starIndex = 0;
    private int endIndex = 20;
    protected static final int pageSize = 20;
    private int PUSH_TIMES = 1;


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

                if (dataList.size() > pageSize) {

                    endIndex = dataList.size() < endIndex + PUSH_TIMES * pageSize ? dataList.size() :
                            endIndex + PUSH_TIMES * pageSize;
                    listviewAdapter.setData(dataList.subList(starIndex, endIndex));


                    PUSH_TIMES++;

                } else {
                    Toast.makeText(ctx, R.string.no_more_messages, Toast.LENGTH_SHORT).show();

                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void pullToRefresh() {
                PUSH_TIMES = 1;
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

    @Override
    public void handler(Message msg) {
        switch (msg.what) {
            case GET_LOACTIOPN_DATA:

                refreshUI();

                break;

            default:
                LogUtils.logD("收到信息 = " + msg);
                break;
        }


    }

    private void getDataFromLocat() {

        // TODO: 2015-11-16 从本地数据库上获取数据
        app.getThreadInstance().startTask(new MyRunnable(new MyRunnable.GotoRunnable() {

            @Override
            public void running() {
                dataList = DBUtils.getShareMessages();
                mhandler.sendEmptyMessage(GET_LOACTIOPN_DATA);

            }
        }));

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
                        formatData(shareMessageList.getShareMessageHzList());

                        dataList = shareMessageList.getShareMessageHzList();
                        refreshUI();

                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );


    }

    private void formatData(final List<com.young.model.ShareMessage_HZ> shareList) {
// TODO: 2015-11-25 在handler中更新数据


        app.getThreadInstance().startTask(new MyRunnable(new MyRunnable.GotoRunnable() {

            @Override
            public void running() {

                for (com.young.model.ShareMessage_HZ share : shareList) {
                    //分享信息
                    ShareMessage shareMessageHZ = new ShareMessage();
                    shareMessageHZ.setObjectId(share.getObjectId());
                    shareMessageHZ.setShImgs(share.getShImgs());
                    shareMessageHZ.setShContent(share.getShContent());
                    shareMessageHZ.setCreatedAt(share.getCreatedAt());
                    shareMessageHZ.setShLocation(share.getShLocation());
                    shareMessageHZ.setShVisitedNum(share.getShVisitedNum());
                    shareMessageHZ.setShCommNum(share.getShCommNum());
                    shareMessageHZ.setShWantedNum(share.getShWantedNum());
                    //用户信息
                    User user = share.getUserId();
                    com.young.model.dbmodel.User u = new com.young.model.dbmodel.User();
                    u.setCreatedAt(user.getCreatedAt());
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
                    u.setObjectId(user.getObjectId());
                    u.setAccessToken(user.getSessionToken());
                    u.save();

                    shareMessageHZ.setUserId(u);
                    shareMessageHZ.save();


                }

                mhandler.sendEmptyMessage(FIRST_GETDATA);

            }
        }));
    }

    /**
     * 刷新列表，最新数据
     */
    private void refreshUI() {
        endIndex = dataList.size() < pageSize ? dataList.size() : endIndex;

        listviewAdapter.setData(dataList.subList(starIndex, endIndex));
        swipeRefreshLayout.setRefreshing(false);
    }

}
