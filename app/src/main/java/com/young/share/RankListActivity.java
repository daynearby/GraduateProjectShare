package com.young.share;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.adapter.RankListAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.ItemActBarActivity;
import com.young.share.config.Contants;
import com.young.share.model.dbmodel.CommRemoteModel;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.RankList;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.myInterface.ComparatorImpl;
import com.young.share.myInterface.GotoAsyncFunction;
import com.young.share.myInterface.ListViewRefreshListener;
import com.young.share.network.BmobApi;
import com.young.share.thread.MyRunnable;
import com.young.share.utils.CommonUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.LogUtils;

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

    @InjectView(R.id.sw_ranklist_refresh)
    private SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.list_ranklist)
    private ListView listview;
    private RankListAdapter rankAdapter;

    private String tag;
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
        tag = getIntent().getStringExtra(Contants.INTENT_RANK_TYPE);
        key = getString(R.string.tag_manywanttogo).equals(tag) ? ComparatorImpl.COMPREHENSIVE : ComparatorImpl.COMPREHENSIVE_OTHERS;

        setTvTitle(tag);
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
        String funcationName;

        if (key == ComparatorImpl.COMPREHENSIVE) {
            funcationName = BmobApi.GET_HEAT_MESSAGES;
        } else {
            funcationName = BmobApi.GET_RANK_DATA;
            try {
                parameters.put(Contants.PARAM_TAG, tag);
            } catch (JSONException e) {
                LogUtils.logD("添加参数失败 " + e.toString());
            }
        }

        try {
            parameters.put(Contants.PARAM_SKIP, skip);

        } catch (JSONException e) {
            LogUtils.logD("添加参数失败 " + e.toString());
        }


        BmobApi.AsyncFunction(mActivity, parameters, funcationName, RankList.class, new GotoAsyncFunction() {
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

        rankAdapter = new RankListAdapter(mActivity);

        listview.setAdapter(rankAdapter);

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
                bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, comm);

                if (comm.getType() == Contants.DATA_MODEL_SHARE_MESSAGES) {//分享信息

                    bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_RANK_LIST_ACTIVITY);

                    mStartActivity(MessageDetail.class, bundle);

                } else {//商家优惠


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
