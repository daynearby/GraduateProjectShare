package com.young.share;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.twotoasters.jazzylistview.JazzyListView;
import com.young.adapter.RecordAdapter;
import com.young.annotation.InjectView;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.model.CollectionList;
import com.young.model.Collection_HZ;
import com.young.model.ShareMessageList;
import com.young.model.ShareMessage_HZ;
import com.young.myInterface.GotoAsyncFunction;
import com.young.myInterface.ListViewRefreshListener;
import com.young.network.BmobApi;
import com.young.thread.MyRunnable;
import com.young.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用记录查看
 * <p/>
 * Created by Nearby Yang on 2015-12-03.
 */
public class RecordCommActivity extends ItemActBarActivity {

    @InjectView(R.id.lv_record_comm)
    private JazzyListView listview;
    @InjectView(R.id.sw_record_comm_refresh)
    private SwipeRefreshLayout swipeRefresh;

    private RecordAdapter recAdapter;
    private List<ShareMessage_HZ> dataList = new ArrayList<>();
    private List<ShareMessage_HZ> currentList = new ArrayList<>();

    private int starIndex = 0;
    private int endIndex = 20;
    protected static final int pageSize = 20;//一页显示的数量
    private int PUSH_TIMES = 1;//上拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据

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
        super.initData();

        Bundle bundle = getIntent().getExtras();

        RECORD_TYPE = bundle.getInt(Contants.RECORD_TYPE, Contants.RECORD_TYPE_SHARE);//类型
//设置标题
        setTvTitle(RECORD_TYPE == Contants.RECORD_TYPE_SHARE ? R.string.share_record : R.string.collection_record);

        //下载数据
        SVProgressHUD.showWithStatus(mActivity, getString(R.string.tips_loading));

        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {
                getData();//获取数据
            }
        }));

    }

    @Override
    public void findviewbyid() {

        setBarItemVisible(true, false);
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                back2superClazz();
            }

            @Override
            public void rightClivk(View v) {

            }
        });

        recAdapter = new RecordAdapter(mActivity);
        listview.setAdapter(recAdapter);
        listview.setOnItemClickListener(new itemClick());

//列表刷新
        new ListViewRefreshListener(listview, swipeRefresh, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {//上拉
                Skip += Contants.RECORD_LENGHT;

                //分享信息
                if (dataList.size() > pageSize * PUSH_TIMES) {

                    endIndex = dataList.size() < endIndex + PUSH_TIMES * pageSize ? dataList.size() :
                            endIndex + PUSH_TIMES * pageSize;
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
                LogUtils.logD("上拉刷新");

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
    public void bindData() {


    }

    @Override
    public void handerMessage(Message msg) {
//弹窗处理
        processDialog();

        switch (msg.what) {

            case MESSAFE_TYPE_SHARE://分享信息记录

                refreshUI();

                break;

            case MESSAFE_TYPE_COLLECTION://收藏信息记录

                refreshUI();
                break;
        }
    }

    /**
     * setdata &&  notification
     */
    private void refreshUI() {

        if (isGetMore) {
            endIndex = dataList.size() < (PUSH_TIMES + 1) * pageSize ? dataList.size() : (PUSH_TIMES + 1) * pageSize;
        } else {
            endIndex = dataList.size() < pageSize ? dataList.size() : endIndex;
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
            params.put(Contants.PARAM_USERID, mUser.getObjectId());
            params.put(Contants.PARAM_SKIP, String.valueOf(Skip));
        } catch (JSONException e) {
            LogUtils.logD("add params failure 　" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_SHARE_RECROD, ShareMessageList.class, new GotoAsyncFunction() {
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
                    dataList.clear();
//                格式化数据
                    dataList = sharemessageList.getShareMessageHzList();

                }

                mHandler.sendEmptyMessage(MESSAFE_TYPE_SHARE);
            }

            @Override
            public void onFailure(int code, String msg) {

                processDialog();

                mToast(R.string.tips_loading_faile);
                LogUtils.logD("get share messages failure. code = " + code + " message = " + msg);

            }
        });

    }


    /**
     * 收藏记录
     */
    public void getCollectionRec() {
        JSONObject params = new JSONObject();

        try {
            params.put(Contants.PARAM_USERID, mUser.getObjectId());
            params.put(Contants.PARAM_SKIP, String.valueOf(Skip));
        } catch (JSONException e) {
            LogUtils.logD("add params failure 　" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_RECOLLECTION_RECORD, CollectionList.class, new GotoAsyncFunction() {
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
                    dataList.clear();
//                格式化数据
                    dataList = formatCollection(collectionList.getCollecList());

                }

                mHandler.sendEmptyMessage(MESSAFE_TYPE_SHARE);
            }

            @Override
            public void onFailure(int code, String msg) {

                processDialog();

                mToast(R.string.tips_loading_faile);
                LogUtils.logD("get share messages failure. code = " + code + " message = " + msg);

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
            mStartActivity(MessageDetail.class, bundle);
        }
    }

    /**
     * 关闭弹窗
     */
    private void processDialog() {
        if (SVProgressHUD.isShowing(mActivity)) {
            SVProgressHUD.dismiss(mActivity);
        }
    }
}
