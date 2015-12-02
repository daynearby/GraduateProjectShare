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
import com.young.model.ShareMessage_HZ;
import com.young.network.BmobApi;
import com.young.thread.MyRunnable;
import com.young.utils.EmotionUtils;
import com.young.utils.ThreadUtils;

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


    private static String backTagClazz;
    private static CommRemoteModel commModel = new CommRemoteModel();
    private List<CommRemoteModel> dataList = new ArrayList<>();//数据
    private List<String> commentList;
    private CommentAdapter commAdapter;
    private InputMethodManager imm;
    private int strId;//提示文字


    private static final int MESSAGE_FORMATE_DATA = 1;//格式化数据
    private boolean SendMessageFinish = true;//消息是否已经发送


    @Override
    public int getLayoutId() {
        // TODO: 2015-11-27 用户头像。界面的设计-->控件选择
        return R.layout.activity_message_detail;

    }

    @Override
    public void initData() {
        super.initData();
        setBarItemVisible(true, false);
        setTvTitle(R.string.title_body);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        backTagClazz = getIntent().getStringExtra(Contants.CLAZZ_NAME);

        ThreadUtils threadUtils = new ThreadUtils();

        threadUtils.addTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                switch (backTagClazz) {

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
//        wantogo_txt.setOnClickListener(this);
//        hadgo_txt.setOnClickListener(this);
//        comment_txt.setOnClickListener(this);

    }

    @Override
    public void bindData() {

//标题栏的监听
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                mBackStartActivity(backTagClazz);
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
                startPrepare();
//                sendComment(uId);
            }
        });

    }

    @Override
    public void handerMessage(Message msg) {

        switch (msg.what) {
            case MESSAGE_FORMATE_DATA:


                commAdapter.setData(dataList);

                break;
        }
    }

    /**
     * 准备发送评论工作
     */
    private void startPrepare() {

        if (SendMessageFinish) {
            sendComment_edt.setFocusable(true);
            layout_comment.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 点击发送 处理工作
     *
     */
    private void finishPrepare() {
        sendComment_edt.setFocusable(false);
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
        commModel.setType(Contants.DATA_MODEL_SHARE_MESSAGES);//属于分享信息
        dataList.add(commModel);

        mHandler.sendEmptyMessage(MESSAGE_FORMATE_DATA);
    }

    /**
     * 获取评论数据
     *
     * @param serializableExtra
     */
    private void getComment(Serializable serializableExtra) {
        ShareMessage_HZ shareMessage = (ShareMessage_HZ) serializableExtra;


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
                sendComment(commModel.getUser().getObjectId());
                break;

//            case R.id.id_tx_wantogo://想去
//                v.setClickable(false);
//                wantToGo(v);
//                break;
//
//            case R.id.id_hadgo://去过
//                v.setClickable(false);
//                visit(v);
//                break;
//
//            case R.id.id_tx_comment://评论
//                layout_comment.setVisibility(View.VISIBLE);
//
//                break;

        }

    }

    /**
     * 发送评论
     */
    private void sendComment(String uId) {


        if (!TextUtils.isEmpty(sendComment_edt.getText().toString())) {

            BmobApi.sendMessage(mActivity, mUser.getObjectId(), uId, sendComment_edt.getText().toString(),
                    commModel.getObjectId(), new BmobApi.SendMessageCallback() {
                        @Override
                        public void onSuccessReflesh() {
                            // TODO: 2015-12-02 刷新ui，重新获取数据库评论数据

                            sendFinish();

                        }
                    });


        } else {//输入内容为空
            SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.comment_not_empty));
        }


    }

    /**
     * 发送完后清理
     */
    private void sendFinish() {
        SendMessageFinish = true;
        sendComment_edt.setText("");

    }


}
