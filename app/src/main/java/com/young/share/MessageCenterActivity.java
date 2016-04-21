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
import com.young.share.adapter.MessageCenterAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.model.Comment_HZ;
import com.young.share.model.Message_HZ;
import com.young.share.model.gson.CommentList;
import com.young.share.network.BmobApi;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 消息中心
 * Created by Nearby Yang on 2015-12-06.
 */
public class MessageCenterActivity extends BaseAppCompatActivity {

    @InjectView(R.id.sw_message_center_refresh)
    private SwipeRefreshLayout swipeRefresh;
    @InjectView(R.id.lv_message_center)
    private ListView listView;
    @InjectView(R.id.im_content_tips)
    private ImageView tipsIm;

    private MessageCenterAdapter messageAdapter;

    private List<Comment_HZ> commentList = new ArrayList<>();
    private List<Comment_HZ> dataList = new ArrayList<>();

    private int starIndex = 0;
    private int endIndex = 20;
    protected static final int pageSize = 20;
    private int PUSH_TIMES = 0;
    private int Skip = 0;//跳过的数量
    private boolean isGetMore = false;//从远程数据库获取更多数据

    private static final int REFRESHUI = 101;//获取数据，通知刷新UI
    private static final int UPDATE_MESSAGE = 110;//更新消息，通知刷新UI
    private static final int MESSAGE_NO_DATA = 0x001;//没有更多内容
    private static final int MESSAGE_LOAD_DATA_FAILURE = 0x002;//加载数据失败

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_center;
    }

    @Override
    public void initData() {
        initialiToolbar();
        setTitle(R.string.messages_center);

        dataList = (List<Comment_HZ>) app.getCacheInstance().getAsObject(Contants.ACAHE_KEY_MESSAGE_CENTER + cuser.getObjectId());
        SVProgressHUD.showWithStatus(mActivity, getString(R.string.tips_loading));
        //获取数据
        initDataByThread();
    }

    /**
     * 通过线程进行获取数据
     */
    private void initDataByThread() {
        getRemoteData();
    }

    @Override
    public void findviewbyid() {
        messageAdapter = new MessageCenterAdapter(mActivity);
        listView.setAdapter(messageAdapter);
        listView.setOnItemClickListener(new itemClick());
/*上拉、下拉刷新listener*/
        setPullListener();

    }

    @Override
    public void bindData() {

        if (dataList!=null&&dataList.size()>0){
            mHandler.sendEmptyMessage(REFRESHUI);
        }
    }


    /**
     * 上下来刷新 监听
     */
    private void setPullListener() {
        new ListViewRefreshListener(listView, swipeRefresh, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {
//                Skip += Contants.RECORD_LENGHT;

                //分享信息
                if (dataList.size() > pageSize * PUSH_TIMES) {

                    endIndex = dataList.size() < pageSize + pageSize * PUSH_TIMES ? dataList.size() :
                            pageSize + pageSize * PUSH_TIMES;
                    messageAdapter.setData(dataList.subList(starIndex, endIndex));

                    PUSH_TIMES++;

                } else {
                    isGetMore = true;
                    Skip = dataList.size();
                    //获取数据
                    getRemoteData();
//                        toast(R.string.no_more_messages);
                }


                swipeRefresh.setRefreshing(false);
                LogUtils.d("上拉刷新");
            }

            @Override
            public void pullToRefresh() {//下拉刷新
                Skip = 0;
                isGetMore = false;
                //获取分享记录
                getRemoteData();
            }
        });
    }

    /**
     * 初始化toolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_message_center);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back2superclazz();
            }
        });

    }


    @Override
    public void handerMessage(Message msg) {

        switch (msg.what) {

            case REFRESHUI://获取数据并且刷新UI
                //提示框处理
                CommonFunctionUtils.processDialog(mActivity);
                swipeRefresh.setRefreshing(false);

                refreshUI();

                break;

            case UPDATE_MESSAGE:
//更新状态
                messageAdapter.getData().get(msg.arg1).getMessageId().setRead(true);
                messageAdapter.notifyDataSetChanged();

                break;
            case MESSAGE_NO_DATA://没有更多数据
                toast(R.string.no_more_messages);

                break;
            case MESSAGE_LOAD_DATA_FAILURE:

                //提示框处理
                CommonFunctionUtils.processDialog(mActivity);

                toast(R.string.tips_loading_faile);
                break;

        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {

            back2superclazz();
            return true;
        }


        return super.dispatchKeyEvent(event);
    }

    /**
     * setdata &&  notification
     */
    private void refreshUI() {

        if (isGetMore) {
            endIndex = dataList.size() < (PUSH_TIMES + 1) * pageSize ? dataList.size() : (PUSH_TIMES + 1) * pageSize;
        } else {
            endIndex = dataList.size() < pageSize ? dataList.size() : pageSize;
        }
        //刷新UI
        messageAdapter.setData(dataList.subList(starIndex, endIndex));
    }

    /**
     * 返回上一级
     * 发送广播，修改图标
     */
    private void back2superclazz() {

        setResult(Contants.RESULT_MESSAGE_CENTER);
        this.finish();
    }

    /**
     * 获取数据
     */
    public void getRemoteData() {

        JSONObject params = new JSONObject();

        try {
            params.put(Contants.PARAM_USERID, cuser.getObjectId());
            params.put(Contants.PARAM_SKIP, String.valueOf(Skip));

        } catch (JSONException e) {

            LogUtils.d("add params failure 　" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_NEW_MESSAGES, CommentList.class, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                CommentList commentList = (CommentList) object;

                if (isGetMore) {
                    if (commentList.getCommentList().size() > 0) {
                        dataList.addAll(commentList.getCommentList());
                    } else {
                        mHandler.sendEmptyMessage(MESSAGE_NO_DATA);
                    }
                } else {

//                格式化数据
                    dataList = commentList.getCommentList();

                }

                //刷新机制
                if (dataList != null && dataList.size() > 0) {
                    tipsIm.setVisibility(View.GONE);
                    //缓存数据
                    app.getCacheInstance().put(Contants.ACAHE_KEY_MESSAGE_CENTER + cuser.getObjectId(), (Serializable) dataList);
                    mHandler.sendEmptyMessage(REFRESHUI);
                } else {
                    //提示框处理
                    CommonFunctionUtils.processDialog(mActivity);
                    tipsIm.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mHandler.sendEmptyMessage(MESSAGE_LOAD_DATA_FAILURE);
                LogUtils.d("get share messages failure. code = " + code + " message = " + msg);

            }
        });

    }


    /**
     * 点击事件
     */
    private class itemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//将消息标记为已读
            updateMessage(position);
            Bundle bundle = new Bundle();
            bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_MESSAGE_CENTER_ACTIVITY);
            bundle.putSerializable(Contants.INTENT_KEY_DISCOVER, dataList.get(position).getShMsgId());

            mStartActivity(MessageDetailActivity.class, bundle);


        }
    }

    /**
     * 将消息标记为已读
     *
     * @param position
     */
    private void updateMessage(final int position) {
        Message_HZ message = new Message_HZ();
        message.setObjectId(dataList.get(position).getObjectId());
        message.setRead(true);
        dataList.get(position).getMessageId().setRead(true);
        messageAdapter.notifyDataSetChanged();
        message.update(mActivity, new UpdateListener() {
            @Override
            public void onSuccess() {

                Message mes = new Message();
                mes.what = UPDATE_MESSAGE;
                mes.arg1 = position;
                mHandler.sendMessage(mes);

            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.d(" update message failure. code = " + i + " message = " + s);
            }
        });

    }


}
