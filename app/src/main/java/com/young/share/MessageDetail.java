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
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.adapter.CommentAdapter;
import com.young.annotation.InjectView;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.model.BaseModel;
import com.young.model.Collection_HZ;
import com.young.model.CommRemoteModel;
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
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 查看详细信息
 * Created by Nearby Yang on 2015-11-26.
 */
public class MessageDetail extends ItemActBarActivity implements View.OnClickListener {

    @InjectView(R.id.listview_discover)
    private ListView listView;
    @InjectView(R.id.edt_message_detail_comment)
    private EditText sendComment_edt;
    @InjectView(R.id.btn_message_detail_tosend)
    private Button tosend_btn;
    @InjectView(R.id.im_message_detail_emotion)
    private ImageView emotion_im;
    @InjectView(R.id.vp_popupwindow_emotion_dashboard)
    private ViewPager vp_emotion_dashboard;
    @InjectView(R.id.id_tx_wantogo)
    private TextView wantogo_txt;
    @InjectView(R.id.id_hadgo)
    private TextView hadgo_txt;
    @InjectView(R.id.id_tx_comment)
    private TextView comment_txt;
    @InjectView(R.id.llayout_message_detail_input_comment)
    private LinearLayout layout_comment;


    private static String backTagClazz;
    private static CommRemoteModel commModel = new CommRemoteModel();
    private List<String> commentList;
    private CommentAdapter commAdapter;
    private InputMethodManager imm;
    private int strId;//提示文字


    private static final int MESSAGE_FORMATE_DATA = 1;//格式化数据


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
        //表情
        new EmotionUtils(mActivity, vp_emotion_dashboard, sendComment_edt);

        sendComment_edt.setOnClickListener(this);
        tosend_btn.setOnClickListener(this);
        emotion_im.setOnClickListener(this);
        wantogo_txt.setOnClickListener(this);
        hadgo_txt.setOnClickListener(this);
        comment_txt.setOnClickListener(this);

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
            public void reply(String objId) {
                sendComment(objId, true);
            }
        });

    }

    @Override
    public void handerMessage(Message msg) {

        switch (msg.what) {
            case MESSAGE_FORMATE_DATA:

                break;
        }
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
        commModel.setType(Contants.DATA_MODEL_SHARE_MESSAGES);//属于分享信息

        mHandler.sendEmptyMessage(MESSAGE_FORMATE_DATA);
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
                sendComment(commModel.getUser().getObjectId(), false);
                break;

            case R.id.id_tx_wantogo://想去
                wantToGo(v);
                break;

            case R.id.id_hadgo://去过
                visit(v);
                break;

            case R.id.id_tx_comment://评论
                layout_comment.setVisibility(View.VISIBLE);
                break;

        }

    }

    /**
     * 发送评论
     */
    private void sendComment(String objId, boolean isReply) {

        if (!TextUtils.isEmpty(sendComment_edt.getText().toString())) {


        } else {
            SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.comment_not_empty));
        }

    }

    /**
     * 想去逻辑处理
     *
     * @param v
     */
    private void wantToGo(View v) {

        boolean hadWant = false;
        ShareMessage_HZ shareMessage = new ShareMessage_HZ();


        JSONObject jsonObject = new JSONObject();//参数

        try {
            jsonObject.put("message", "1");
            jsonObject.put("userid", mUser.getObjectId());
            jsonObject.put("collectionid", commModel.getObjectId());

        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtils.logD("云端代码 添加参数失败 " + e.toString());
        }


//判断用户是否存在
        for (String userId : commModel.getWanted()) {
            hadWant = mUser.getObjectId().equals(userId);
        }

        if (hadWant) {

            strId = R.string.cancel_collect_success;
            commModel.getWanted().remove(mUser.getObjectId());
            shareMessage.setShWantedNum(commModel.getWanted());
//操作收藏表
            BmobApi.AsyncFunction(mActivity, jsonObject, BmobApi.REMOVE_COLLECTION, new GotoAsyncFunction() {
                @Override
                public void onSuccess(Object object) {

                    BaseModel baseModel = (BaseModel) object;

                    if (baseModel.getCode() == BaseModel.SUCCESS) {
//                        mToast(R.string.operation_success);
                        LogUtils.logD("删除收藏记录 成功  data = " + baseModel.getData());
                    } else {

                        LogUtils.logD("删除收藏记录 失败  data = " + baseModel.getData());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });

        } else {//没去过

            strId = R.string.collect_success;
            commModel.getWanted().add(mUser.getObjectId());

            shareMessage.setObjectId(commModel.getObjectId());
            shareMessage.setShWantedNum(commModel.getWanted());

//添加收藏
            BmobApi.saveCollectionShareMessage(mActivity,mUser,shareMessage,Contants.MESSAGE_TYPE_SHAREMESSAGE);

        }

        ((TextView) v).setText(String.valueOf(commModel.getWanted() == null ?
                0 : commModel.getWanted().size()));


        //更新云端数据库
        shareMessage.update(mActivity, commModel.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {

                LogUtils.logD(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }

    /**
     * 去过的逻辑处理
     *
     * @param v
     */
    private void visit(View v) {
        boolean hadGo = false;
        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
//判断用户是否存在
        for (String userId : commModel.getVisited()) {
            hadGo = mUser.getObjectId().equals(userId);
        }

        if (hadGo) {
            strId = R.string.not_visit;
            commModel.getVisited().remove(mUser.getObjectId());
        } else {
            strId = R.string.cancel_collect_success;
            commModel.getVisited().add(mUser.getObjectId());
        }

        ((TextView) v).setText(String.valueOf(commModel.getVisited() == null ?
                0 : commModel.getVisited().size()));

        shareMessage.setShVisitedNum(commModel.getVisited());
//更新云端数据库
        shareMessage.update(mActivity, commModel.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.logD(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }
}
