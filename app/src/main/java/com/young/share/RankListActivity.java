package com.young.share;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.twotoasters.jazzylistview.JazzyListView;
import com.young.adapter.RankListAdapter;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.model.CommRemoteModel;
import com.young.model.DiscountMessage_HZ;
import com.young.model.RankList;
import com.young.model.ShareMessage_HZ;
import com.young.myInterface.ComparatorImpl;
import com.young.myInterface.GotoAsyncFunction;
import com.young.myInterface.ListViewRefreshListener;
import com.young.network.BmobApi;
import com.young.thread.MyRunnable;
import com.young.utils.CommonUtils;
import com.young.utils.DataFormateUtils;
import com.young.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 排行棒的列表信息
 * <p/>
 * Created by Nearby Yang on 2015-12-26.
 */
public class RankListActivity extends ItemActBarActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private JazzyListView listview;
    private RankListAdapter rankAdapter;

    private int key;
    private int skip = 0;
    private int startIndex = 0;
    private int endIndex = 20;
    private int PUSH_TIMES = 0;//下拉次数
    private boolean isGetMore = false;

    private List<CommRemoteModel> remoteList;

    private static final int HANDLER_GET_DATA = 10;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ranklist;
    }

    @Override
    public void initData() {
        super.initData();
        setBarItemVisible(true, false);
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                mBackStartActivity(MainActivity.class);
            }

            @Override
            public void rightClivk(View v) {

            }
        });

        //标志
        String tag = getIntent().getStringExtra(Contants.INTENT_RANK_TYPE);
        key = getString(R.string.tag_manywanttogo).equals(tag) ? ComparatorImpl.COMPREHENSIVE : ComparatorImpl.COMPREHENSIVE_OTHERS;

        remoteList = new ArrayList<>();

        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {
//获取数据
                getDataFromRemote();
            }
        }));

    }

    /**
     * 从远程数据库获取数据
     * 解析并且转换成remoteModel
     */
    private void getDataFromRemote() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put(Contants.SKIP, skip);
        } catch (JSONException e) {
            LogUtils.logD(e.toString());
        }

        BmobApi.AsyncFunction(mActivity, parameters, BmobApi.GET_RANK_DATA, RankList.class, new GotoAsyncFunction() {
            @Override
            public void onSuccess(Object object) {
                RankList rankLists = (RankList) object;

                List<ShareMessage_HZ> sharemessagesList = rankLists.getSharemessages();
                List<DiscountMessage_HZ> discountMessagesList = rankLists.getDiscountMessages();

                for (ShareMessage_HZ share : sharemessagesList) {
                    remoteList.add(DataFormateUtils.formateDataDiscover(share, Contants.DATA_MODEL_SHARE_MESSAGES));
                }

                for (DiscountMessage_HZ discountMessage : discountMessagesList) {
                    remoteList.add(DataFormateUtils.formateDataDiscount(discountMessage));
                }
//进行排序
                Collections.sort(remoteList, new ComparatorImpl(key));

                mHandler.sendEmptyMessage(HANDLER_GET_DATA);
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.logD("get rank data failure. code = " + code + " message = " + msg);
            }
        });

    }

    @Override
    public void findviewbyid() {

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sw_ranklist_refresh);
        listview = (JazzyListView) findViewById(R.id.list_ranklist);
        new ListViewRefreshListener(listview, swipeRefreshLayout, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {//上拉

                if (CommonUtils.isNetworkAvailable(mActivity)) {//有网络
//                            startRow += Contants.PAGER_NUMBER;

                    if (remoteList.size() > Contants.PAGE_SIZE * PUSH_TIMES) {

                        endIndex = remoteList.size() < Contants.PAGE_SIZE +
                                Contants.PAGE_SIZE * PUSH_TIMES ? remoteList.size() :
                                Contants.PAGE_SIZE + Contants.PAGE_SIZE * PUSH_TIMES;

                        rankAdapter.setData(remoteList.subList(startIndex, endIndex));

                        PUSH_TIMES++;

                    } else {
                        isGetMore = true;
//                                Toast.makeText(ctx, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                        skip = remoteList.size();
                        getDataFromRemote();
                    }

                } else {//没有网络
                    SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.without_network));
                }

                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void pullToRefresh() {//下拉

                PUSH_TIMES = 1;
                skip = 0;
                isGetMore = false;

                if (CommonUtils.isNetworkAvailable(mActivity)) {//有网络

                    getDataFromRemote();

                } else {

                    SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.without_network));

                }


            }
        });


    }

    @Override
    public void bindData() {

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommRemoteModel comm = remoteList.get(position);
                Bundle bundle = new Bundle();

                if (comm.getType() == Contants.DATA_MODEL_SHARE_MESSAGES) {//分享信息

                    bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_RANK_LIST_ACTIVITY);
                    bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, comm);

                    mStartActivity(MessageDetail.class, bundle);

                } else {//商家优惠


                    bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, remoteList.get(position));
                    mStartActivity(DiscoutDetailActivity.class, bundle);
                }
            }
        });

    }

    @Override
    public void handerMessage(Message msg) {

        refreshUI();

    }

    @Override
    public void mBack() {
        mBackStartActivity(MainActivity.class);

    }

    /**
     * 刷新列表，最新数据
     */
    private void refreshUI() {

        if (isGetMore) {
            endIndex = remoteList.size() < (PUSH_TIMES + 1) * Contants.PAGE_SIZE ?
                    remoteList.size() : (PUSH_TIMES + 1) * Contants.PAGE_SIZE;
        } else {
            endIndex = remoteList.size() < Contants.PAGE_SIZE ? remoteList.size() : endIndex;
        }

        rankAdapter.setData(remoteList.subList(startIndex, endIndex));

        swipeRefreshLayout.setRefreshing(false);
    }

}
