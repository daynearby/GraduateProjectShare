package com.young.share;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.adapter.MessageCenterAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.ItemActBarActivity;
import com.young.share.config.Contants;
import com.young.share.model.CommRemoteModel;
import com.young.share.model.CommentList;
import com.young.share.model.Comment_HZ;
import com.young.share.model.Message_HZ;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.network.BmobApi;
import com.young.share.thread.MyRunnable;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 消息中心
 * Created by Nearby Yang on 2015-12-06.
 */
public class MessageCenterActivity extends ItemActBarActivity {

    @InjectView(R.id.sw_message_center_refresh)
    private SwipeRefreshLayout swipeRefresh;
    @InjectView(R.id.lv_message_center)
    private ListView listView;

    private MessageCenterAdapter messageAdapter;

    private List<Comment_HZ> commentList = new ArrayList<>();
    private List<CommRemoteModel> dataList = new ArrayList<>();

    private int starIndex = 0;
    private int endIndex = 20;
    protected static final int pageSize = 20;
    private int PUSH_TIMES = 0;
    private int Skip = 0;//跳过的数量
    private boolean isGetMore = false;//从远程数据库获取更多数据

    private static final int REFRESHUI = 101;//获取数据，通知刷新UI
    private static final int UPDATE_MESSAGE = 110;//更新消息，通知刷新UI

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_center;
    }

    @Override
    public void initData() {
        super.initData();

        setBarItemVisible(true, false);
        setTvTitle(R.string.messages_center);
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                back2superclazz();
            }

            @Override
            public void rightClivk(View v) {

            }
        });

        SVProgressHUD.showWithStatus(mActivity, getString(R.string.tips_loading));
        //获取数据
        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                getRemoteData();

            }
        }));

    }

    @Override
    public void findviewbyid() {
        messageAdapter = new MessageCenterAdapter(mActivity);
        listView.setAdapter(messageAdapter);
        listView.setOnItemClickListener(new itemClick());


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
//                        mToast(R.string.no_more_messages);
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

    @Override
    public void bindData() {

    }

    @Override
    public void handerMessage(Message msg) {

        switch (msg.what) {

            case REFRESHUI://获取数据并且刷新UI

                refreshUI();

                break;

            case UPDATE_MESSAGE:

                List<CommRemoteModel> data = messageAdapter.getData();
                data.get(msg.arg1).setRead(true);
                messageAdapter.setData(data);

                break;
        }

    }

    @Override
    public void mBack() {
        back2superclazz();
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
        mBackStartActivity(MainActivity.class);
        intents.setAction(Contants.BORDCAST_CLEAR_MESSAGES);
        sendBroadcast(intents);
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
                        dataList.addAll(formateData(commentList.getCommentList()));
                    } else {
                        mToast(R.string.no_more_messages);
                    }
                } else {
                    dataList.clear();
//                格式化数据
                    dataList = formateData(commentList.getCommentList());

                }

                swipeRefresh.setRefreshing(false);
                //提示框处理
                LocationUtils.processDialog(mActivity);

                mHandler.sendEmptyMessage(REFRESHUI);
            }

            @Override
            public void onFailure(int code, String msg) {
//提示框处理
                LocationUtils.processDialog(mActivity);

                mToast(R.string.tips_loading_faile);
                LogUtils.d("get share messages failure. code = " + code + " message = " + msg);

            }
        });

    }

    /**
     * 将该类转换成通用的object
     *
     * @param commentList
     * @return
     */
    private List<CommRemoteModel> formateData(List<Comment_HZ> commentList) {

        List<CommRemoteModel> commList = new ArrayList<>();
        for (Comment_HZ comment : commentList) {

            CommRemoteModel comm = new CommRemoteModel();

            comm.setObjectId(comment.getMessageId().getObjectId());
            comm.setContent(comment.getMessageId().getCommContent());
            comm.setMcreatedAt(comment.getMessageId().getCreatedAt());
            comm.setShareMessage(comment.getShMsgId());
            comm.setReceiver(comment.getReveicerId());
            comm.setSender(comment.getSenderId());
            comm.setRead(comment.getMessageId().isRead());

            commList.add(comm);
        }

        return commList;
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
            bundle.putSerializable(Contants.CLAZZ_DATA_MESSAGE, dataList.get(position).getShareMessage());

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
