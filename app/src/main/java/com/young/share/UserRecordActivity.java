package com.young.share;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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
import com.young.share.interfaces.ComparatorImpl;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.RemoteModel;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.gson.UserRecorderList;
import com.young.share.network.BmobApi;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分享记录查看
 * <p/>
 * Created by Nearby Yang on 2015-12-03.
 */
public class UserRecordActivity extends BaseAppCompatActivity {

    @InjectView(R.id.lv_record_comm)
    private ListView listview;
    @InjectView(R.id.sw_record_comm_refresh)
    private SwipeRefreshLayout swipeRefresh;
    @InjectView(R.id.im_record_content_empty)
    private ImageView contentEmpty;

    private RecordAdapter recAdapter;
    private List<RemoteModel> dataList = new ArrayList<>();

    private int starIndex = 0;
    private int endIndex = 20;
    private int PUSH_TIMES = 1;//上拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据
    private boolean isEmpty = false;//内容是否为空

    private int Skip = 0;//跳过的数量
    // TODO: 2015-12-05 线程池多线程并发进行
    private static final int MESSAFE_TYPE_SHARE = 0x12;
    private static final String SHARE_RECORD = "share_record";//分享信息记录
    private static final int MESSAGE_NO_MORE_DATA = 0x01;//获取不了更多数据
    private static final int MESSAGE_LOAD_DATA_FAILURE = 0x02;//获取不了更多数据,加载失败


    @Override
    public int getLayoutId() {
        return R.layout.activity_record_comm;
    }

    @Override
    public void initData() {
        initialiToolbar();
        Bundle bundle = getIntent().getExtras();

//设置标题
        setTitle(R.string.share_record);

        //下载数据
        SVProgressHUD.showWithStatus(mActivity, getString(R.string.tips_loading));
//        threadPool.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
//            @Override
//            public void running() {
//
//            }
//        }));
        initDataByThread();
        dataList = (List<RemoteModel>) app.getCacheInstance().getAsObject(SHARE_RECORD + cuser.getObjectId());
    }

    /**
     * 通过线程进行获取数据
     */
    private void initDataByThread() {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getShareRec();//获取数据
//            }
//        }).start();

        getShareRec();//获取数据
    }

    @Override
    public void findviewbyid() {

        recAdapter = new RecordAdapter(mActivity);
        listview.setAdapter(recAdapter);
        listview.setOnItemClickListener(new itemClick());

        if (dataList != null && dataList.size() > 0) {
            mHandler.sendEmptyMessage(MESSAFE_TYPE_SHARE);
        }

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
                mActivity.finish();
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
                    getShareRec();
//                        toast(R.string.no_more_messages);
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
                getShareRec();

            }
        });

    }

    @Override
    public void handerMessage(Message msg) {

        switch (msg.what) {
            case MESSAFE_TYPE_SHARE://分享信息记录，获取数据
                //提示框处理
                CommonFunctionUtils.processDialog(mActivity);
                if (!isEmpty) {
                    refreshUI();
                } else {
                    contentEmpty.setVisibility(View.VISIBLE);
                }

                break;

            case MESSAGE_NO_MORE_DATA://没有更多数据
                toast(R.string.no_more_messages);
                break;
            case MESSAGE_LOAD_DATA_FAILURE://加载数据失败
                //提示框处理
                CommonFunctionUtils.processDialog(mActivity);

                toast(R.string.tips_loading_faile);
                break;
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
mActivity.finish();
            return true;
        }


        return super.dispatchKeyEvent(event);
    }


    /**
     * 分享记录
     */
    public void getShareRec() {

        JSONObject params = new JSONObject();

        LogUtils.d("  cuser.getObjectId() = " + cuser.getObjectId());

        try {
            params.put(Contants.PARAM_USERID, cuser.getObjectId());
            params.put(Contants.PARAM_SKIP, String.valueOf(Skip));
        } catch (JSONException e) {
            LogUtils.d("add params failure 　" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_USER_RECORD, UserRecorderList.class, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                UserRecorderList userRecorderList = (UserRecorderList) object;

                if (isGetMore) {

                    if (userRecorderList.getShareMessage().size() > 0 || userRecorderList.getDiscountMessage().size() > 0) {

                        for (ShareMessage_HZ sha : userRecorderList.getShareMessage()) {
                            dataList.add(DataFormateUtils.formateDataDiscover(sha));
                        }

                        for (DiscountMessage_HZ disc : userRecorderList.getDiscountMessage()) {
                            dataList.add(DataFormateUtils.formateDataDiscount(disc));
                        }

                        /*将两个表的数据进行排序*/
                        if (dataList.size() > 0) {
                            Collections.sort(dataList, new ComparatorImpl(ComparatorImpl.COMPREHENSIVE_CREATED));
                            app.getCacheInstance().put(SHARE_RECORD + cuser.getObjectId(), (Serializable) dataList);
                        }

                    } else {
                        mHandler.sendEmptyMessage(MESSAGE_NO_MORE_DATA);
                    }

                } else {
                    if (dataList != null && dataList.size() > 0) {
                        dataList.clear();
                    } else {
                        dataList = new ArrayList<>();
                    }

                    for (ShareMessage_HZ sha : userRecorderList.getShareMessage()) {
                        dataList.add(DataFormateUtils.formateDataDiscover(sha));
                    }

                    for (DiscountMessage_HZ disc : userRecorderList.getDiscountMessage()) {
                        dataList.add(DataFormateUtils.formateDataDiscount(disc));
                    }

                        /*将两个表的数据进行排序*/
                    if (dataList.size() > 0) {
                        Collections.sort(dataList, new ComparatorImpl(ComparatorImpl.COMPREHENSIVE_CREATED));
                        app.getCacheInstance().put(SHARE_RECORD + cuser.getObjectId(), (Serializable) dataList);
                    }


                    isEmpty = dataList.size() <= 0;

                }
                //防止保存空数据，下次读取显示的时候报空指针
                if (dataList.size() > 0) {
                    app.getCacheInstance().put(SHARE_RECORD + cuser.getObjectId(), (Serializable) dataList);
                }

                mHandler.sendEmptyMessage(MESSAFE_TYPE_SHARE);
            }

            @Override
            public void onFailure(int code, String msg) {
                isEmpty = dataList.size() <= 0;

                mHandler.sendEmptyMessage(MESSAGE_LOAD_DATA_FAILURE);
                LogUtils.d("get share messages failure. code = " + code + " message = " + msg);

            }
        });

    }


    /**
     * item的点击事件
     */
    private class itemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(Contants.BUNDLE_TAG, dataList.get(position));
//            bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_PERSONAL_ACTIVITY);//shareMessage
//            mStartActivity(MessageDetailActivity.class, bundle);

            Bundle bundle = new Bundle();
            RemoteModel remoteModel = dataList.get(position);
            bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, remoteModel);
            bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_PERSONAL_ACTIVITY);//shareMessage

            if (remoteModel.getType() == Contants.DATA_MODEL_SHARE_MESSAGES) {//分享信息
                mStartActivity(MessageDetailActivity.class, bundle);
            } else {//折扣信息
                mStartActivity(DiscoutDetailActivity.class, bundle);
            }
        }
    }

}
