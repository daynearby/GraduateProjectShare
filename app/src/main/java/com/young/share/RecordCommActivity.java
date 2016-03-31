package com.young.share;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.adapter.RecordAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.model.Collection_HZ;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.gson.CollectionList;
import com.young.share.model.gson.ShareMessageList;
import com.young.share.network.BmobApi;
import com.young.share.thread.MyRunnable;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用记录查看
 * <p/>
 * Created by Nearby Yang on 2015-12-03.
 */
public class RecordCommActivity extends BaseAppCompatActivity {

    @InjectView(R.id.lv_record_comm)
    private ListView listview;
    @InjectView(R.id.sw_record_comm_refresh)
    private SwipeRefreshLayout swipeRefresh;
    @InjectView(R.id.im_record_content_empty)
    private ImageView contentEmpty;

    private RecordAdapter recAdapter;
    private List<ShareMessage_HZ> dataList = new ArrayList<>();
    private List<ShareMessage_HZ> currentList = new ArrayList<>();

    private int starIndex = 0;
    private int endIndex = 20;
    private int PUSH_TIMES = 1;//上拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据
    private boolean isEmpty = false;//内容是否为空

    private int RECORD_TYPE;//记录类型
    private int Skip = 0;//跳过的数量
    private static final int MESSAFE_TYPE_SHARE = 10;//分享信息记录
    private static final int MESSAFE_TYPE_COLLECTION = 11;//收藏信息记录

    // TODO: 2015-12-05 线程池多线程并发进行

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_comm;
    }

    @Override
    public void initData() {
        initialiToolbar();
        Bundle bundle = getIntent().getExtras();

        RECORD_TYPE = bundle.getInt(Contants.RECORD_TYPE, Contants.RECORD_TYPE_SHARE);//类型
//设置标题
        setTitle(RECORD_TYPE == Contants.RECORD_TYPE_SHARE ? R.string.share_record : R.string.collection_record);

        //下载数据
        SVProgressHUD.showWithStatus(mActivity, getString(R.string.tips_loading));
        threadPool.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {
                getData();//获取数据
            }
        }));

    }

    @Override
    public void findviewbyid() {

        recAdapter = new RecordAdapter(mActivity);
        listview.setAdapter(recAdapter);
        listview.setOnItemClickListener(new itemClick());


    }

    /**
     * 初始化toolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_record_com);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back2superClazz();
            }
        });

    }


    @Override
    public void bindData() {

//列表刷新
        new ListViewRefreshListener(listview, swipeRefresh, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {//上拉
                Skip += Contants.RECORD_LENGHT;

                //分享信息
                if (dataList.size() > Contants.PAGE_SIZE * PUSH_TIMES) {

                    endIndex = dataList.size() < endIndex + PUSH_TIMES * Contants.PAGE_SIZE ? dataList.size() :
                            endIndex + PUSH_TIMES * Contants.PAGE_SIZE;
                    recAdapter.setData(dataList.subList(starIndex, endIndex));

                    PUSH_TIMES++;

                } else {
                    isGetMore = true;
                    Skip = dataList.size();
                    //获取数据
                    getData();
//                        mToast(R.string.no_more_messages);
                }

                swipeRefresh.setRefreshing(false);
                LogUtils.d("上拉刷新");

            }

            @Override
            public void pullToRefresh() {//下拉
                Skip = 0;
                dataList.clear();
                isGetMore = false;
                //获取分享记录
                getData();

            }
        });

    }

    @Override
    public void handerMessage(Message msg) {
//提示框处理
        LocationUtils.processDialog(mActivity);
        if (!isEmpty) {
            switch (msg.what) {
                case MESSAFE_TYPE_SHARE://分享信息记录
                    refreshUI();
                    break;

                case MESSAFE_TYPE_COLLECTION://收藏信息记录
                    refreshUI();
                    break;
            }
        } else {

            contentEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * setdata &&  notification
     */
    private void refreshUI() {

        if (isGetMore) {
            endIndex = dataList.size() < (PUSH_TIMES + 1) * Contants.PAGE_SIZE ? dataList.size() : (PUSH_TIMES + 1) * Contants.PAGE_SIZE;
        } else {
            endIndex = dataList.size() < Contants.PAGE_SIZE ? dataList.size() : endIndex;
        }
        //刷新UI
        recAdapter.setData(dataList.subList(starIndex, endIndex));
    }

    @Override
    public void mBack() {
        back2superClazz();
    }

    /**
     * 返回上一级
     */
    private void back2superClazz() {
        mBackStartActivity(PersonalCenterActivity.class);
        this.finish();
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (RECORD_TYPE == Contants.RECORD_TYPE_SHARE) {//分享记录
            getShareRec();
        } else {//收藏记录
            getCollectionRec();

        }
    }

    /**
     * 分享记录
     */
    public void getShareRec() {

        JSONObject params = new JSONObject();

        try {
            params.put(Contants.PARAM_USERID, cuser.getObjectId());
            params.put(Contants.PARAM_SKIP, String.valueOf(Skip));
        } catch (JSONException e) {
            LogUtils.d("add params failure 　" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_SHARE_RECROD, ShareMessageList.class, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                ShareMessageList sharemessageList = (ShareMessageList) object;

                if (isGetMore) {
                    if (sharemessageList.getShareMessageHzList().size() > 0) {
                        dataList.addAll(sharemessageList.getShareMessageHzList());
                    } else {
                        mToast(R.string.no_more_messages);
                    }
                } else {
                    isEmpty = dataList.size() <= 0;
                    dataList.clear();
//                格式化数据
                    dataList = sharemessageList.getShareMessageHzList();

                }

                mHandler.sendEmptyMessage(MESSAFE_TYPE_SHARE);
            }

            @Override
            public void onFailure(int code, String msg) {
                isEmpty = dataList.size() <= 0;
                //提示框处理
                LocationUtils.processDialog(mActivity);

                mToast(R.string.tips_loading_faile);
                LogUtils.d("get share messages failure. code = " + code + " message = " + msg);

            }
        });

    }


    /**
     * 收藏记录
     */
    public void getCollectionRec() {
        JSONObject params = new JSONObject();

        try {
            params.put(Contants.PARAM_USERID, cuser.getObjectId());
            params.put(Contants.PARAM_SKIP, String.valueOf(Skip));
        } catch (JSONException e) {
            LogUtils.d("add params failure 　" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_RECOLLECTION_RECORD, CollectionList.class, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                CollectionList collectionList = (CollectionList) object;

                if (isGetMore) {
                    if (collectionList.getCollecList().size() > 0) {
                        dataList.addAll(formatCollection(collectionList.getCollecList()));
                    } else {
                        mToast(R.string.no_more_messages);
                    }
                } else {
                    isEmpty = dataList.size() <= 0;
                    dataList.clear();
//                格式化数据
                    dataList = formatCollection(collectionList.getCollecList());

                }

                mHandler.sendEmptyMessage(MESSAFE_TYPE_SHARE);
            }

            @Override
            public void onFailure(int code, String msg) {
                isEmpty = dataList.size() <= 0;
                //提示框处理
                LocationUtils.processDialog(mActivity);
                mToast(R.string.tips_loading_faile);
                LogUtils.d("get share messages failure. code = " + code + " message = " + msg);

            }
        });


    }

    private List<ShareMessage_HZ> formatCollection(List<Collection_HZ> CollecList) {
        List<ShareMessage_HZ> shareMessageList = new ArrayList<>();
        for (Collection_HZ collection : CollecList) {
            shareMessageList.add(collection.getShMsgId());
        }

        return shareMessageList;

    }

    /**
     * item的点击事件
     */
    private class itemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Contants.BUNDLE_TAG, dataList.get(position));
            bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_PERSONAL_ACTIVITY);//shareMessage
            mStartActivity(MessageDetailActivity.class, bundle);
        }
    }

}
