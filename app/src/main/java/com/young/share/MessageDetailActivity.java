package com.young.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.young.share.adapter.ButtombarPageAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.fragment.CommentFragment;
import com.young.share.fragment.LikeFragment;
import com.young.share.fragment.WantToGoFragment;
import com.young.share.interfaces.AsyncListener;
import com.young.share.model.BaseModel;
import com.young.share.model.CommRemoteModel;
import com.young.share.model.MyUser;
import com.young.share.model.PictureInfo;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.network.BmobApi;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.EmotionUtils;
import com.young.share.utils.EvaluateUtil;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.CustomViewPager;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.MultiImageView.MultiImageView;
import com.young.share.views.PopupWinUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 查看分享信息的详细信息
 * 修改成scrollView
 * Created by Nearby Yang on 2015-11-26.
 */
public class MessageDetailActivity extends BaseAppCompatActivity implements View.OnClickListener {

    //    @InjectView(R.id.listview_discover)
//    private ListView listView;
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
    /**
     * header
     */
    @InjectView(R.id.id_im_userH)
    private ImageView avatar;//用户头像
    @InjectView(R.id.id_userName)
    private TextView nickname_tv;//昵称
    @InjectView(R.id.id_tx_tab)
    private TextView tag_tv;//标签
    @InjectView(R.id.tx_message_detail_content)
    private TextView content_tv;//分享的文本内容
    @InjectView(R.id.miv_message_detail)
    private MultiImageView multiImageView;
    //    @InjectView(R.id.gv_message_detailshareimg)
//    private GridView myGridview ;
    @InjectView(R.id.vp_message_detail)
    private CustomViewPager viewPager;
    @InjectView(R.id.id_tx_wantogo)
    private TextView wanto_tv;//想去数量
    @InjectView(R.id.id_hadgo)
    private TextView hadgo_tv;//去过数量
    @InjectView(R.id.id_tx_comment)
    private TextView comment_tv;//评论数量
    @InjectView(R.id.tv_item_message_detail_createdat)
    private TextView ceatedAt_tv;
    @InjectView(R.id.rg_msg_detail_gd)
    private RadioGroup indexRadiog;


    private String superTagClazz;
    private CommRemoteModel commModel = new CommRemoteModel();//存放分享信息的具体内容
    private ShareMessage_HZ shareMessage;//直接显示的分享信息
    private ButtombarPageAdapter pageAdapter;
    private InputMethodManager imm;
    private String receiverId;//接收消息者id
    private int commentClick;//是否是点击评论进去

    private static final int MESSAGE_SHARE_MESSAGE = 0x01;//显示传过来的sharemessage
    private static final int MESSAGE_BING_MESSAGE = 0x02;//显示传过来的sharemessage
    private static final int COMMENT_CLICK = 0;//点击事件，是评论

    private boolean SendMessageFinish = true;//消息是否已经发送
    /*指示器*/
    private ImageView indexIm;// 页面指示器
    private int offset = 0;// 动画图片偏移量
    private int tabWidth = 0;
    private int bmpW;// 动画图片宽度
    private OvershootInterpolator overshootInterpolator;
    private int DURATION = 500;

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_detail;

    }

    @Override
    public void initData() {
        initialiToolbar();
        setTitle(R.string.title_body);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Bundle bundle = getIntent().getExtras();

        superTagClazz = bundle.getString(Contants.CLAZZ_NAME);
        commentClick = bundle.getInt(Contants.EXPEND_OPTION_ONE, 0);


/*获取数据*/
        getData(bundle);

    }


    @Override
    public void findviewbyid() {
//        commAdapter = new CommentAdapter(mActivity);
/*初始化指示器*/
        initialiIndicator();
    }

    /**
     * 初始化指示器
     */
    private void initialiIndicator() {

        /**
         * 初始化动画滚动条
         */
        indexIm = (ImageView) findViewById(R.id.im_msg_detail_index);
//        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.icon_cursor)
//                .getWidth();// 获取图片宽度

        indexRadiog.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tabWidth =indexRadiog.getMeasuredWidth() / 3;

        Matrix matrix = new Matrix();
//        offset = (indexRadiog.getMeasuredWidth()/ 3 - tabWidth) / 3;
        matrix.postTranslate(0, 0);
        indexIm.setImageMatrix(matrix);// 设置动画初始位置
        overshootInterpolator = new OvershootInterpolator();
    }

    @Override
    public void bindData() {
////回复评论

        //表情
        new EmotionUtils(mActivity, vp_emotion_dashboard, sendComment_edt);

        sendComment_edt.setOnClickListener(this);
        tosend_btn.setOnClickListener(this);
        emotion_im.setOnClickListener(this);

    }


    /**
     * 获取fragment的实例
     *
     * @return
     */
    private void createdFragments() {

        List<Fragment> fragmentList = new ArrayList<>();

        fragmentList.add(new LikeFragment(this, commModel.getWanted()));
        fragmentList.add(new WantToGoFragment(this, commModel.getVisited()));
        fragmentList.add(new CommentFragment(this, commModel.getObjectId()));

        pageAdapter = new ButtombarPageAdapter(fragmentList, getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);

    }


    /**
     * 设置数据
     */
    private void setupData() {


        MyUser myUser = commModel.getMyUser();

        if (TextUtils.isEmpty(commModel.getContent())) {
            content_tv.setVisibility(View.GONE);
        } else {
            content_tv.setVisibility(View.VISIBLE);
            content_tv.setText(StringUtils.getEmotionContent(
                    this, content_tv, commModel.getContent()));
        }


        nickname_tv.setText(TextUtils.isEmpty(myUser.getNickName()) ? "" : myUser.getNickName());
/*用户头像*/
        ImageHandlerUtils.loadIamgeThumbnail(this,
                TextUtils.isEmpty(myUser.getAvatar()) ? Contants.DEFAULT_AVATAR : myUser.getAvatar(), avatar);


        if (TextUtils.isEmpty(commModel.getTag())) {
            tag_tv.setVisibility(View.GONE);
        } else {
            tag_tv.setVisibility(View.VISIBLE);
            tag_tv.setText(commModel.getTag());
        }

        wanto_tv.setText(commModel.getWanted() != null && commModel.getWanted().size() > 0 ?
                String.valueOf(commModel.getWanted().size()) : getString(R.string.tx_wantogo));


        hadgo_tv.setText(commModel.getVisited() != null && commModel.getVisited().size() > 0 ?
                String.valueOf(commModel.getVisited().size()) : getString(R.string.hadgo));
        //判断当前用户是否点赞
        if (cuser != null) {
            LocationUtils.leftDrawableWantoGO(wanto_tv, commModel.getWanted(), cuser.getObjectId());//设置图标
            LocationUtils.leftDrawableVisited(hadgo_tv, commModel.getVisited(), cuser.getObjectId());
        }

        ceatedAt_tv.setText(commModel.getMcreatedAt());

        comment_tv.setText(commModel.getComment() > 0 ? String.valueOf(commModel.getComment()) : getString(R.string.tx_comment));

        multiImageView.setList(DataFormateUtils.thumbnailList(this, commModel.getImages()));
        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(mActivity, shareMessage.getShImgs());

                EvaluateUtil.setupCoords(mActivity, (ImageView) view, pictureInfoList, position);
                Intent intent = new Intent(mActivity, BigPicActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable(Contants.INTENT_IMAGE_INFO_LIST, (Serializable) pictureInfoList);
                intent.putExtras(bundle);
                intent.putExtra(Contants.INTENT_CURRENT_ITEM, position);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        nickname_tv.setOnClickListener(this);
        avatar.setOnClickListener(this);
        wanto_tv.setOnClickListener(this);
        hadgo_tv.setOnClickListener(this);
        comment_tv.setOnClickListener(this);
        tag_tv.setOnClickListener(this);

        //创建下方的viewpager，显示点赞用户、评论内容
        createdFragments();

    }


    @Override
    public void handerMessage(Message msg) {

        if (commentClick == Contants.EXPEND_START_INPUT) {
            startPrepare();
        }

        switch (msg.what) {

//            case GET_MESSAGE:
/*更新信息*/
//                commAdapter.setData(dataList);
//
//                break;


            case MESSAGE_SHARE_MESSAGE:


                break;

            case MESSAGE_BING_MESSAGE://将数据绑定到控件中
/*
                这里使用了统一的数据格式，commMedel
*/
                setupData();


                break;

        }
    }

    /**
     * 初始化toolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_message_detail);
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


    /**
     * 查看用户资料
     *
     * @param v
     */
    private void showUserInfo(View v) {

        PopupWinUserInfo userInfo = new PopupWinUserInfo(this, commModel.getMyUser());
        userInfo.onShow(v);
    }


    /**
     * 获取信息
     *
     * @param bundle
     */
    private void getData(final Bundle bundle) {


        switch (superTagClazz) {

            case Contants.CLAZZ_DISCOVER_ACTIVITY://shareMessage详细信息

                shareMessage = (ShareMessage_HZ) bundle
                        .getSerializable(Contants.CLAZZ_DATA_MODEL);

                break;

            case Contants.CLAZZ_PERSONAL_ACTIVITY://分享消息记录

                shareMessage = (ShareMessage_HZ) bundle
                        .getSerializable(Contants.BUNDLE_TAG);


                break;

            case Contants.CLAZZ_MESSAGE_CENTER_ACTIVITY://消息列表
                shareMessage = (ShareMessage_HZ) bundle
                        .getSerializable(Contants.CLAZZ_DATA_MESSAGE);

                break;

            case Contants.CLAZZ_RANK_LIST_ACTIVITY://排行榜

                CommRemoteModel commModel = (CommRemoteModel) bundle
                        .getSerializable(Contants.CLAZZ_DATA_MODEL);

                if (commModel != null) {

                    formateDataDiscover(commModel);

                }
                break;

        }

                /*获取最新的评论*/
        if (superTagClazz.equals(Contants.CLAZZ_DISCOVER_ACTIVITY) ||
                superTagClazz.equals(Contants.CLAZZ_PERSONAL_ACTIVITY) ||
                superTagClazz.equals(Contants.CLAZZ_MESSAGE_CENTER_ACTIVITY)
                ) {

            formateDataDiscover(shareMessage);

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
// TODO: 2016-03-18 更新信息
            /*    if (isClick || commAdapter.isClick()) {//点击过则刷新界面
                    LocationUtils.sendBordCast(mActivity, Contants.REFRESH_TYPE_DISCOVER);
                }*/
                backAFinsish();
                break;

            case Contants.CLAZZ_PERSONAL_ACTIVITY://分析消息记录
                this.finish();
                break;

            case Contants.CLAZZ_MESSAGE_CENTER_ACTIVITY://消息列表
                backAFinsish();
                break;

            case Contants.CLAZZ_RANK_LIST_ACTIVITY://排行榜
                backAFinsish();
                break;
        }
    }

    private void backAFinsish() {

        mBackStartActivity(superTagClazz);
        this.finish();
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

        commModel = !superTagClazz.equals(Contants.CLAZZ_RANK_LIST_ACTIVITY) ?
                DataFormateUtils.formateDataDiscover(serializableExtra, Contants.DATA_MODEL_HEAD) :
                (CommRemoteModel) serializableExtra;


        mHandler.sendEmptyMessage(MESSAGE_BING_MESSAGE);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.im_message_detail_emotion://表情输入
                imm.hideSoftInputFromWindow(sendComment_edt.getWindowToken(), 0);
                vp_emotion_dashboard.setVisibility(vp_emotion_dashboard.getVisibility() == View.GONE ?
                        View.VISIBLE : View.GONE);

                break;

            case R.id.edt_message_detail_comment://输入框监听输入
                vp_emotion_dashboard.setVisibility(vp_emotion_dashboard.getVisibility() == View.VISIBLE ?
                        View.GONE : View.GONE);

                break;

            case R.id.btn_message_detail_tosend://发送消息
                finishPrepare();
                receiverId = commModel.getMyUser().getObjectId();
                sendComment();
                break;
            case R.id.id_im_userH://用户资料
                showUserInfo(v);
                break;
            case R.id.id_userName:
                showUserInfo(v);
                break;

            case R.id.id_tx_wantogo://想去--数量
                v.setClickable(false);
                getUser();

                if (cuser != null) {

                    List<String> shWantedNum = commModel.getWanted();

                    wantToGo(UserUtils.isHadCurrentUser(shWantedNum, cuser.getObjectId()), v);
                } else {
                    v.setClickable(true);
                    Dialog4Tips.loginFunction(mActivity);
                }

                break;

            case R.id.id_hadgo://去过--数量
                v.setClickable(false);
                getUser();

                if (cuser != null) {

                    List<String> shVisitedNum = commModel.getVisited();
                    visit(UserUtils.isHadCurrentUser(shVisitedNum, cuser.getObjectId()), v);

                } else {
                    v.setClickable(true);
                    Dialog4Tips.loginFunction(mActivity);
                }
                break;

            case R.id.id_tx_comment://评论数量
                getUser();

                break;

            case R.id.id_tx_tab://标签

                LogUtils.ts("标签的点击事件");
                break;

        }

    }


    /**
     * 去过的逻辑处理
     *
     * @param hadGo
     * @param v
     */
    private void visit(boolean hadGo, final View v) {

        int leftDrawID;//提示图片资源id
        if (hadGo) {
//            strId = R.string.not_visit;
            leftDrawID = R.drawable.icon_bottombar_hadgo;

            commModel.getVisited().remove(cuser.getObjectId());
        } else {
//            strId = R.string.cancel_collect_success;
            leftDrawID = R.drawable.icon_hadgo;
            commModel.getVisited().add(cuser.getObjectId());
        }


        ((TextView) v).setText(String.valueOf(commModel.getVisited() == null ?
                0 : commModel.getVisited().size()));
        ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(leftDrawID, 0, 0, 0);

        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
        shareMessage.setShVisitedNum(commModel.getVisited());
        shareMessage.setMyUserId(commModel.getMyUser());

        shareMessage.update(this, commModel.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
//                mToast(strId);
                v.setClickable(true);
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
                LogUtils.d(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }


    /**
     * 想去逻辑处理
     *
     * @param hadWant
     * @param v
     */
    private void wantToGo(boolean hadWant, final View v) {

        int leftDrawID;//提示图片资源id
        JSONObject jsonObject = new JSONObject();//参数
        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
        try {
            jsonObject.put("message", "1");
            jsonObject.put("userid", cuser.getObjectId());
            jsonObject.put("collectionid", commModel.getObjectId());

        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtils.d("云端代码 添加参数失败 " + e.toString());
        }

        if (hadWant) {
//            strId = R.string.cancel_collect_success;
            leftDrawID = R.drawable.icon_wantogo;

            commModel.getWanted().remove(cuser.getObjectId());

//操作收藏表
            BmobApi.AsyncFunction(this, jsonObject, BmobApi.REMOVE_COLLECTION, new AsyncListener() {
                @Override
                public void onSuccess(Object object) {

                    BaseModel baseModel = (BaseModel) object;

                    if (baseModel.getCode() == BaseModel.SUCCESS) {
//                        mToast(R.string.operation_success);
                        LogUtils.d("删除收藏记录 成功  data = " + baseModel.getData());
                    } else {

                        LogUtils.d("删除收藏记录 失败  data = " + baseModel.getData());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });
        } else {
//            strId = R.string.collect_success;
            leftDrawID = R.drawable.icon_wantogo_light;

            commModel.getWanted().add(cuser.getObjectId());
            shareMessage.setObjectId(commModel.getObjectId());
            shareMessage.setMyUserId(commModel.getMyUser());

            BmobApi.saveCollectionShareMessage(this, cuser, shareMessage, Contants.MESSAGE_TYPE_SHAREMESSAGE);

        }

        ((TextView) v).setText(String.valueOf(commModel.getWanted() == null ?
                0 : commModel.getWanted().size()));
        ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(leftDrawID, 0, 0, 0);

        shareMessage.setShVisitedNum(commModel.getVisited());
        shareMessage.update(this, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
//                mToast(strId);
                v.setClickable(true);
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
                LogUtils.d(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }

    /**
     * 再获取当前用户是否存在
     */
    public void getUser() {
        if (cuser == null) {
            cuser = BmobUser.getCurrentUser(this, MyUser.class);
        }
    }

    /**
     * 发送评论
     * 使用云端代码发送评论，推送消息
     */
    private void sendComment() {


        if (!TextUtils.isEmpty(sendComment_edt.getText().toString())) {

            BmobApi.sendMessage(mActivity, commModel.getMyUser().getObjectId(), receiverId, sendComment_edt.getText().toString(),
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

    /**
     * 增加评论数量
     */
    private void commentIncrement() {
        ShareMessage_HZ share = new ShareMessage_HZ();
        share.setObjectId(commModel.getObjectId());
        share.increment(Contants.PARAMS_SHCOMM_NUM);
        share.update(mActivity);
    }

    /**
     * 发送完后清理
     */
    private void sendFinish() {
        SendMessageFinish = true;
        sendComment_edt.setText("");

        //获取最新的评论数据
//        getComment(commModel.getObjectId());
    }

    /**
     * pager change listener
     */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == 0) {
                wanto_tv.setBackgroundColor(getResources().getColor(R.color.gray_lighter));
                hadgo_tv.setBackgroundColor(Color.WHITE);
                comment_tv.setBackgroundColor(Color.WHITE);

            } else if (position == 1) {
                wanto_tv.setBackgroundColor(Color.WHITE);
                hadgo_tv.setBackgroundColor(getResources().getColor(R.color.gray_lighter));
                comment_tv.setBackgroundColor(Color.WHITE);
            } else {
                wanto_tv.setBackgroundColor(Color.WHITE);
                hadgo_tv.setBackgroundColor(Color.WHITE);
                comment_tv.setBackgroundColor(getResources().getColor(R.color.gray_lighter));
            }

        }

        @Override
        public void onPageSelected(int position) {
            changeTab(position);// 页面变化时指示器的动画效果
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
// TODO: 2016-03-22 计算偏移值

    /**
     * 动画效果
     * @param index
     */
    private void changeTab(int index) {
        int position = tabWidth * (index + 1);
        ViewPropertyAnimator.animate(indexIm).translationX(position)
                .setInterpolator(overshootInterpolator).setDuration(DURATION);
    }
}
