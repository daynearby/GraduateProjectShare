package com.young.share;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.SlideInEffect;
import com.young.adapter.CommentAdapter;
import com.young.annotation.InjectView;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.model.CommRemoteModel;
import com.young.model.CommentList;
import com.young.model.Comment_HZ;
import com.young.model.ShareMessage_HZ;
import com.young.myInterface.GotoAsyncFunction;
import com.young.network.BmobApi;
import com.young.thread.MyRunnable;
import com.young.utils.DataFormateUtils;
import com.young.utils.EmotionUtils;
import com.young.utils.LocationUtils;
import com.young.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 查看详细信息
 * Created by Nearby Yang on 2015-11-26.
 */
public class MessageDetail extends ItemActBarActivity implements View.OnClickListener {

    @InjectView(R.id.listview_discover)
    private JazzyListView listView;
    @InjectView(R.id.edt_message_detail_comment)
    private EditText sendComment_edt;
    @InjectView(R.id.btn_message_detail_tosend)
    private Button tosend_btn;
    @InjectView(R.id.im_message_detail_emotion)
    private ImageView emotion_im;
    @InjectView(R.id.vp_popupwindow_emotion_dashboard)
    private ViewPager vp_emotion_dashboard;
    @InjectView(R.id.llayout_message_detail_input_comment)
    private LinearLayout layout_comment;


    private static String superTagClazz;
    private static CommRemoteModel commModel = new CommRemoteModel();
    private List<CommRemoteModel> dataList = new ArrayList<>();//数据
    private CommentAdapter commAdapter;
    private InputMethodManager imm;
    private String receiverId;//接收消息者id
    private int commentClick;//是否是点击评论进去

    private static final int GET_MESSAGE = 1;//格式化数据
    private static final int COMMENT_CLICK = 0;//点击事件，是评论


    private boolean SendMessageFinish = true;//消息是否已经发送


    @Override
    public int getLayoutId() {
        return R.layout.activity_message_detail;

    }

    @Override
    public void initData() {
        super.initData();
        setBarItemVisible(true, false);
        setTvTitle(R.string.title_body);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final Bundle bundle = getIntent().getExtras();

        superTagClazz = bundle.getString(Contants.CLAZZ_NAME);
        commentClick = bundle.getInt(Contants.EXPEND_OPTION_ONE, 0);


        //提示
        SVProgressHUD.showWithStatus(mActivity, getString(R.string.tips_loading));

        //线程
        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {

            @Override
            public void running() {


                switch (superTagClazz) {

                    case Contants.CLAZZ_DISCOVER_ACTIVITY://shareMessage详细信息


                        ShareMessage_HZ shareMessage = (ShareMessage_HZ) bundle.getSerializable(Contants.CLAZZ_DATA_MODEL);
                        formateDataDiscover(shareMessage);
                        //获取最新的评论
                        if (shareMessage != null) {
                            getComment(shareMessage.getObjectId());
                        } else {
                            LocationUtils.processDialog(mActivity);

                        }

                        break;

                    case Contants.CLAZZ_PERSONAL_ACTIVITY://分析消息记录


                        ShareMessage_HZ shareMessageRec = (ShareMessage_HZ) bundle.getSerializable(Contants.BUNDLE_TAG);
                        formateDataDiscover(shareMessageRec);
                        //获取最新的评论
                        if (shareMessageRec != null) {
                            getComment(shareMessageRec.getObjectId());
                        } else {
                            LocationUtils.processDialog(mActivity);
                        }

                        break;

                    case  Contants.CLAZZ_MESSAGE_CENTER_ACTIVITY://消息列表
                        ShareMessage_HZ shareMessageMessage = (ShareMessage_HZ) bundle.getSerializable(Contants.CLAZZ_DATA_MESSAGE);
                        formateDataDiscover(shareMessageMessage);
                        //获取最新的评论
                        if (shareMessageMessage != null) {
                            getComment(shareMessageMessage.getObjectId() );
                        } else {
                            LocationUtils.processDialog(mActivity);
                        }
                        break;

                }


            }
        }));


    }


    @Override
    public void findviewbyid() {

        commAdapter = new CommentAdapter(mActivity);
//回复评论
        commAdapter.setToReply(new CommentAdapter.ToReply() {
            @Override
            public void reply(String uId) {
                LogUtils.logD("callback  messagedetail");
                receiverId = uId;
                startPrepare();
            }
        });

        listView.setAdapter(commAdapter);
        listView.setTransitionEffect(new SlideInEffect());

        //表情
        new EmotionUtils(mActivity, vp_emotion_dashboard, sendComment_edt);

        sendComment_edt.setOnClickListener(this);
        tosend_btn.setOnClickListener(this);
        emotion_im.setOnClickListener(this);

    }

    @Override
    public void bindData() {

//标题栏的监听
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                back2superClazz();
            }

            @Override
            public void rightClivk(View v) {

            }
        });


    }

    @Override
    public void handerMessage(Message msg) {
        if (commentClick == Contants.EXPEND_START_INPUT) {
            startPrepare();
        }
        switch (msg.what) {
            case GET_MESSAGE:

                commAdapter.setData(dataList);

        }
    }

    @Override
    public void mBack() {
        back2superClazz();
    }

    /**
     * 返回上一级
     */
    private void back2superClazz() {
//LogUtils.logD("tagclazz = "+superTagClazz);
        switch (superTagClazz) {

            case Contants.CLAZZ_DISCOVER_ACTIVITY://shareMessage详细信息

                mBackStartActivity(superTagClazz);
                this.finish();
                break;
            case Contants.CLAZZ_PERSONAL_ACTIVITY://分析消息记录
                this.finish();
                break;
            case Contants.CLAZZ_MESSAGE_CENTER_ACTIVITY://消息列表

                mBackStartActivity(superTagClazz);
                this.finish();
                break;
        }
    }

    /**
     * 准备发送评论工作
     */
    private void startPrepare() {

        if (SendMessageFinish) {
            sendComment_edt.requestFocus();
            layout_comment.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 点击发送 处理工作
     */
    private void finishPrepare() {
        sendComment_edt.clearFocus();
        vp_emotion_dashboard.setVisibility(View.GONE);
        layout_comment.setVisibility(View.GONE);

    }

    /**
     * sharemessage
     * <p/>
     * 处理数据
     *
     * @param serializableExtra
     */
    private void formateDataDiscover(Serializable serializableExtra) {
        commModel = DataFormateUtils.formateDataDiscover(serializableExtra);

        dataList.add(0, commModel);

        mHandler.sendEmptyMessage(GET_MESSAGE);
    }

    /**
     * 获取评论数据
     *
     * @param messageId
     */
    private void getComment(String messageId) {


        JSONObject params = new JSONObject();

        try {
            params.put("messageID", messageId);
        } catch (JSONException e) {
            LogUtils.logD("get comment add params failure" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_MESSAGE_COMMENTS, CommentList.class, new GotoAsyncFunction() {
            @Override
            public void onSuccess(Object object) {
                CommentList commentList = (CommentList) object;

                if (commentList.getCommentList().size() > 0) {

                    CommRemoteModel commRemoteModle = dataList.get(0);
                    dataList.clear();
                    dataList.add(commRemoteModle);

                    for (Comment_HZ comm : commentList.getCommentList()) {
                        //格式化数据
                        dataList.add(formateComments(comm));
                    }

                }

                processDialogDismisson();

//刷新界面
                mHandler.sendEmptyMessage(GET_MESSAGE);


            }

            @Override
            public void onFailure(int code, String msg) {
                processDialogDismisson();

                mToast(R.string.tips_loading_faile);
                LogUtils.logD("get comment add params failure.  code = " + code + " message =  " + msg);
            }
        });
    }

    /**
     * 格式化数据
     * <p/>
     * 将Comment_HZ中的数据转换成CommRemoteModel
     *
     * @param comm
     */
    private CommRemoteModel formateComments(Comment_HZ comm) {

        CommRemoteModel commRemoteModel = new CommRemoteModel();

        commRemoteModel.setContent(comm.getMessageId().getCommContent());
        commRemoteModel.setObjectId(comm.getMessageId().getObjectId());
        commRemoteModel.setMcreatedAt(comm.getMessageId().getCreatedAt());
        commRemoteModel.setSender(comm.getSenderId());
        commRemoteModel.setReceiver(comm.getReveicerId());

        commRemoteModel.setType(Contants.DATA_MODEL_BODY);//评论内容

        return commRemoteModel;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.im_message_detail_emotion://表情输入
                imm.hideSoftInputFromWindow(sendComment_edt.getWindowToken(), 0);
                vp_emotion_dashboard.setVisibility(vp_emotion_dashboard.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

                break;

            case R.id.edt_message_detail_comment://输入框监听输入
                vp_emotion_dashboard.setVisibility(vp_emotion_dashboard.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);

                break;

            case R.id.btn_message_detail_tosend://发送消息
                finishPrepare();
                receiverId = commModel.getUser().getObjectId();
                sendComment();
                break;


        }

    }

    /**
     * 发送评论
     * 使用云端代码发送评论，推送消息
     */
    private void sendComment() {


        if (!TextUtils.isEmpty(sendComment_edt.getText().toString())) {

            BmobApi.sendMessage(mActivity, mUser.getObjectId(), receiverId, sendComment_edt.getText().toString(),
                    commModel.getObjectId(), new BmobApi.SendMessageCallback() {
                        @Override
                        public void onSuccessReflesh() {

                            //评论数量加1
                            commentIncrement();
                            //清理工作
                            sendFinish();

                        }
                    });


        } else {//输入内容为空
            SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.comment_not_empty));
        }


    }

    private void commentIncrement() {
        ShareMessage_HZ share = new ShareMessage_HZ();
        share.setObjectId(commModel.getObjectId());
        share.increment("shCommNum");
        share.update(mActivity);
    }

    /**
     * 发送完后清理
     */
    private void sendFinish() {
        SendMessageFinish = true;
        sendComment_edt.setText("");

        //获取最新的评论数据
        getComment(commModel.getObjectId());
    }

    /**
     * 关闭进度条提示
     */
    private void processDialogDismisson() {
        LogUtils.logD(" isshow" + SVProgressHUD.isShowing(mActivity));
        if (SVProgressHUD.isShowing(mActivity)) {

            //提示
            SVProgressHUD.dismiss(mActivity);
        }
    }

}
