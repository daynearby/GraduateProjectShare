package com.young.share;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.adapter.RankListAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ComparatorImpl;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.model.CommRemoteModel;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.gson.RankList;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.network.BmobApi;
import com.young.share.thread.MyRunnable;
import com.young.share.utils.CommonUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 排行棒的列表信息
 * <p/>
 * Created by Nearby Yang on 2015-12-26.
 */
public class RankListActivity extends BaseAppCompatActivity {

    @InjectView(R.id.sw_ranklist_refresh)
    private SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.list_ranklist)
    private ListView listview;
    @InjectView(R.id.ll_rank_list_bg)
    private LinearLayout bgLayout;

    private RankListAdapter rankAdapter;

    private String tag;
    private int key;
    private int skip = 0;
    private int startIndex = 0;
    private int endIndex = 20;
    private int PUSH_TIMES = 0;//下拉次数
    private boolean isGetMore = false;
    private boolean isHadData = false;//本地是否有数据，false --> 没有

    private List<CommRemoteModel> remoteList;

    private static final int HANDLER_GET_DATA = 0x01;
    private static final int HANDLER_GET_NO_DATA = 0x02;//没有数据

    @Override
    public int getLayoutId() {
        return R.layout.activity_ranklist;
    }

    @Override
    public void initData() {
        initializeToolbar();

        //标志
        tag = getIntent().getStringExtra(Contants.INTENT_RANK_TYPE);
        key = getString(R.string.tag_manywanttogo).equals(tag) ? ComparatorImpl.COMPREHENSIVE : ComparatorImpl.COMPREHENSIVE_OTHERS;
        setTitle(tag);

        remoteList = (List<CommRemoteModel>) app.getCacheInstance().getAsObject(tag);
        if (!(isHadData = remoteList != null && remoteList.size() > 0))
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
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_rank);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackStartActivity(MainActivity.class);

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

                    mStartActivity(MessageDetailActivity.class, bundle);

                } else {//商家优惠


                    mStartActivity(DiscoutDetailActivity.class, bundle);
                }
            }
        });

    }

    @Override
    public void handerMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_GET_DATA:

                refreshUI();
                break;

            case HANDLER_GET_NO_DATA:

                bgLayout.setBackgroundResource(R.drawable.icon_conten_empty);
                break;
        }

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
        if (remoteList != null && remoteList.size() > 0) {
            rankAdapter.setData(remoteList.subList(startIndex, endIndex));
        }
        swipeRefreshLayout.setRefreshing(false);
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
                LogUtils.d("添加参数失败 " + e.toString());
            }
        }

        try {
            parameters.put(Contants.PARAM_SKIP, skip);

        } catch (JSONException e) {
            LogUtils.d("添加参数失败 " + e.toString());
        }


        BmobApi.AsyncFunction(mActivity, parameters, funcationName, RankList.class, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                RankList rankLists = (RankList) object;

                List<ShareMessage_HZ> sharemessagesList = rankLists.getSharemessages();
                List<DiscountMessage_HZ> discountMessagesList = rankLists.getDiscountMessages();
/*分享信息的数据*/
                if (remoteList != null && remoteList.size() > 0) {
                    remoteList.clear();
                }
                if (sharemessagesList != null) {
                    for (ShareMessage_HZ share : sharemessagesList) {
                          /*格式化数据，通用格式*/
                        remoteList.add(DataFormateUtils.formateDataDiscover(share, Contants.DATA_MODEL_SHARE_MESSAGES));
                    }
                }

                if (discountMessagesList != null) {
                    for (DiscountMessage_HZ discountMessage : discountMessagesList) {
                        /*格式化数据，通用格式*/
                        remoteList.add(DataFormateUtils.formateDataDiscount(discountMessage));
                    }
                }

//进行排序
                if (remoteList != null && remoteList.size() > 0) {
                    Collections.sort(remoteList, new ComparatorImpl(key));

                    app.getCacheInstance().put(tag, (Serializable) remoteList);

                    mHandler.sendEmptyMessage(HANDLER_GET_DATA);
                } else {
                    mHandler.sendEmptyMessage(HANDLER_GET_NO_DATA);
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.d("get rank data failure. code = " + code + " message = " + msg);
            }
        });

    }
}
