package com.young.share;

import android.content.Context;
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
import com.young.utils.EmotionUtils;
import com.young.utils.LogUtils;
import com.young.utils.ThreadUtils;

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
    //    @InjectView(R.id.id_tx_wantogo)
//    private TextView wantogo_txt;
//    @InjectView(R.id.id_hadgo)
//    private TextView hadgo_txt;
//    @InjectView(R.id.id_tx_comment)
//    private TextView comment_txt;
    @InjectView(R.id.llayout_message_detail_input_comment)
    private LinearLayout layout_comment;


    private static String superTagClazz;
    private static CommRemoteModel commModel = new CommRemoteModel();
    private List<CommRemoteModel> dataList = new ArrayList<>();//数据
    private List<String> commentList;
    private CommentAdapter commAdapter;
    private InputMethodManager imm;
    private String receiverId;//接收消息者id


    private static final int MESSAGE_FORMATE_DATA = 1;//格式化数据
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

        superTagClazz = getIntent().getStringExtra(Contants.CLAZZ_NAME);

        ThreadUtils threadUtils = new ThreadUtils();
        //提示
        SVProgressHUD.showWithStatus(mActivity, getString(R.string.tips_loading));

        //线程
        threadUtils.addTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                switch (superTagClazz) {

                    case Contants.CLAZZ_DISCOVER_ACTIVITY://shareMessage

                        formateDataDiscover(getIntent().getSerializableExtra(Contants.CLAZZ_DATA_MODEL));
                        //获取最新的评论
                        getComment(getIntent().getSerializableExtra(Contants.CLAZZ_DATA_MODEL));

                        break;

                }


            }
        }));

        threadUtils.start();

    }


    @Override
    public void findviewbyid() {

        commAdapter = new CommentAdapter(mActivity);

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
                mBackStartActivity(superTagClazz);
                mActivity.finish();
            }

            @Override
            public void rightClivk(View v) {

            }
        });


//回复评论
        commAdapter.setToReply(new CommentAdapter.ToReply() {
            @Override
            public void reply(String uId) {

                receiverId = uId;
                startPrepare();
            }
        });

    }

    @Override
    public void handerMessage(Message msg) {

        switch (msg.what) {
            case MESSAGE_FORMATE_DATA:

                commAdapter.setData(dataList);

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

        ShareMessage_HZ shareMessage = (ShareMessage_HZ) serializableExtra;

        commModel.setContent(shareMessage.getShContent());
        commModel.setImages(shareMessage.getShImgs());
        commModel.setLocationInfo(shareMessage.getShLocation());
        commModel.setTag(shareMessage.getShTag());
        commModel.setUser(shareMessage.getUserId());
        commModel.setVisited(shareMessage.getShVisitedNum());
        commModel.setWanted(shareMessage.getShWantedNum());
        commModel.setObjectId(shareMessage.getObjectId());
        commModel.setComment(shareMessage.getShCommNum());
        commModel.setMcreatedAt(shareMessage.getCreatedAt());
        commModel.setType(Contants.DATA_MODEL_HEAD);//属于分享信息

        dataList.add(0, commModel);

        mHandler.sendEmptyMessage(MESSAGE_FORMATE_DATA);
    }

    /**
     * 获取评论数据
     *
     * @param serializableExtra
     */
    private void getComment(Serializable serializableExtra) {

        ShareMessage_HZ shareMessage = (ShareMessage_HZ) serializableExtra;
        JSONObject params = new JSONObject();
        try {
            params.put("messageID", shareMessage.getObjectId());
        } catch (JSONException e) {
            LogUtils.logD("get comment add params failure" + e.toString());
        }


        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_MESSAGE_COMMENTS, CommentList.class, new GotoAsyncFunction() {
            @Override
            public void onSuccess(Object object) {
                CommentList commentList = (CommentList) object;
                List<Comment_HZ> comList = commentList.getCommentList();

                for (Comment_HZ comm : comList) {
                    //格式化数据
                    dataList.add(formateComments(comm));

                }

                if (SVProgressHUD.isShowing(mActivity)) {
                    //提示
                    SVProgressHUD.dismiss(mActivity);
                }

//刷新界面
                mHandler.sendEmptyMessage(MESSAGE_FORMATE_DATA);


            }

            @Override
            public void onFailure(int code, String msg) {

                SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.tips_loading_faile));

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


}
