package com.young.share;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.twotoasters.jazzylistview.JazzyListView;
import com.young.adapter.RecordAdapter;
import com.young.annotation.InjectView;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
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
 * <p>
 * Created by Nearby Yang on 2015-12-03.
 */
public class RecordCommActivity extends ItemActBarActivity {

    @InjectView(R.id.lv_record_comm)
    private JazzyListView listview;
    @InjectView(R.id.sw_record_comm_refresh)
    private SwipeRefreshLayout swipeRefresh;

    private RecordAdapter recAdapter;
    private List<ShareMessage_HZ> dataList = new ArrayList<>();

    private int RECORD_TYPE;//记录类型
    private int Skip = 0;//跳过的数量
    private static final int MESSAFE_TYPE_SHARE = 10;//分享信息记录
    private static final int MESSAFE_TYPE_COLLECTION = 11;//收藏信息记录


    @Override
    public int getLayoutId() {
        return R.layout.activity_record_comm;
    }

    @Override
    public void initData() {
        super.initData();

        RECORD_TYPE = getIntent().getIntExtra(Contants.RECORD_TYPE, Contants.RECORD_TYPE_SHARE);//类型

        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                //下载数据

                if (RECORD_TYPE == Contants.RECORD_TYPE_SHARE) {//分享记录
                    getShareRec();
                } else {//收藏记录
                    getCollectionRec();

                }

            }
        }));

    }

    @Override
    public void findviewbyid() {

        setBarItemVisible(true, false);
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                mBackStartActivity(PersonalCenterActivity.class);
                mActivity.finish();
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
                Skip = 0;

            }

            @Override
            public void pullToRefresh() {//下拉
                Skip += Contants.RECORD_LENGHT;
                dataList.clear();
                //获取分享记录
                getShareRec();

            }
        });

    }


    @Override
    public void bindData() {


    }

    @Override
    public void handerMessage(Message msg) {

        switch (msg.what) {

            case MESSAFE_TYPE_SHARE://分享信息记录

                recAdapter.setData(dataList);

                break;

            case MESSAFE_TYPE_COLLECTION://收藏信息记录


                break;
        }
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
            params.put("userID", mUser.getObjectId());
            params.put("skip", String.valueOf(Skip));
        } catch (JSONException e) {
            LogUtils.logD("add params failure 　" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_SHARE_RECROD, ShareMessageList.class, new GotoAsyncFunction() {
            @Override
            public void onSuccess(Object object) {
                ShareMessageList sharemessageList = (ShareMessageList) object;
//                格式化数据
                dataList.addAll(sharemessageList.getShareMessageHzList());

                mHandler.sendEmptyMessage(MESSAFE_TYPE_SHARE);
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.logD("get share messages failure. code = " + code + " message = " + msg);

            }
        });

    }


    /**
     * 收藏记录
     */
    public void getCollectionRec() {

    }

    /**
     * item的点击事件
     */
    private class itemClick implements AdapterView.OnItemClickListener {
        // TODO: 2015-12-04 详细信息出不来，还有返回键
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Contants.BUNDLE_TAG, dataList.get(position));
            bundle.putCharSequence(Contants.CLAZZ_NAME,Contants.CLAZZ_DISCOVER_ACTIVITY);//shareMessage
            mStartActivity(MessageDetail.class, bundle);
        }
    }
}
