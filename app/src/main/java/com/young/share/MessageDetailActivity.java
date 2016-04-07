package com.young.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.adapter.ButtombarPageAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.fragment.CommentFragment;
import com.young.share.fragment.WantToGoFragment;
import com.young.share.fragment.HadGoFragment;
import com.young.share.interfaces.AsyncListener;
import com.young.share.model.BaseModel;
import com.young.share.model.RemoteModel;
import com.young.share.model.Comment_HZ;
import com.young.share.model.MyUser;
import com.young.share.model.PictureInfo;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.network.BmobApi;
import com.young.share.network.NetworkReuqest;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.EmotionUtils;
import com.young.share.utils.EvaluateUtil;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.NetworkUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.CommentListView.CommentListView;
import com.young.share.views.CustomViewPager;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.MultiImageView.MultiImageView;
import com.young.share.views.PopupWinUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

    /**
     * header
     */
    @InjectView(R.id.id_im_userH)
    private ImageView avatar;//用户头像
    @InjectView(R.id.id_userName)
    private TextView nickname_tv;//昵称
    @InjectView(R.id.id_tx_tab)
    private TextView tag_tv;//标签
    @InjectView(R.id.im_tx_tab_icon)
    private ImageView tagIconIm;//标签的图标
    @InjectView(R.id.tx_message_detail_content)
    private TextView content_tv;//分享的文本内容
    @InjectView(R.id.miv_message_detail)
    private MultiImageView multiImageView;
    @InjectView(R.id.vp_message_detail)
    private CustomViewPager viewPager;
    @InjectView(R.id.tv_item_message_detail_createdat)
    private TextView ceatedAt_tv;
    @InjectView(R.id.tv_item_message_detail_location_info)
    private TextView loactioInfoTx;
    /*下面三个bar*/
    @InjectView(R.id.ll_bottom_option_bar)
    private LinearLayout bottomOptionBar;//下面三个三个选项的布局
    @InjectView(R.id.id_tx_wantogo)
    private TextView wanto_tv;//想去数量
    @InjectView(R.id.id_hadgo)
    private TextView hadgo_tv;//去过数量
    @InjectView(R.id.id_tx_comment)
    private TextView comment_tv;//评论数量
    /*发送评论布局*/
    @InjectView(R.id.edt_message_detail_comment)
    private EditText sendComment_edt;
    @InjectView(R.id.txt_message_detail_tosend)
    private TextView tosendTxt;
    @InjectView(R.id.im_message_detail_emotion)
    private ImageView emotion_im;
    @InjectView(R.id.vp_popupwindow_emotion_dashboard)
    private ViewPager vp_emotion_dashboard;
    @InjectView(R.id.ll_message_detail_input_comment)
    private LinearLayout layout_comment;//发送评论的布局

    /*三个radioButton 显示标题*/
    @InjectView(R.id.rb_message_detail_comment)
    private RadioButton commentRbtn;
    @InjectView(R.id.rg_msg_detail_gd)
    private RadioGroup indexRadiog;
    @InjectView(R.id.rb_message_detail_had_go)
    private RadioButton hadGoRbtn;
    @InjectView(R.id.rb_message_detail_want_to)
    private RadioButton wantToRbtn;
    @InjectView(R.id.im_msg_detail_index)
    private ImageView indexIm;// 页面指示器
    /*视频布局*/
    @InjectView(R.id.rl_share_video_layout)
    RelativeLayout videoLayout;
    @InjectView(R.id.vv_share_preview_video)
    private VideoView videoView;
    @InjectView(R.id.im_share_video_priview)
    private ImageView videoPrevideo;
    @InjectView(R.id.im_share_start_btn)
    private ImageView playvideo;

    @InjectView(R.id.pb_share_loading)
    private ProgressBar videoDownloadPb;

    private String superTagClazz;
    //    private RemoteModel commModel = new RemoteModel();//存放分享信息的具体内容
    private ShareMessage_HZ shareMessage;//直接显示的分享信息
    private ButtombarPageAdapter pageAdapter;
    private InputMethodManager imm;
    private String receiverId;//接收消息者id
    private int commentClick;//是否是点击评论进去

    private CommentFragment commentFragment;//评论的界面的对象
    private WantToGoFragment wantToGoFragment;//想去或者说是收藏
    private HadGoFragment hadGoFragment;//去过
    private static final int MESSAGE_SHARE_MESSAGE = 0x01;//显示传过来的sharemessage
    private static final int MESSAGE_BING_MESSAGE = 0x02;//显示传过来的sharemessage
    private static final int MESSAGE_REFRESH_COMMENT = 0x03;//发送评论之后进行刷新评论列表

    private static final int COMMENT_CLICK = 0;//点击事件，是评论
    private boolean SendMessageFinish = true;//消息是否已经发送
    private int tabWidth = 0;
    private OvershootInterpolator overshootInterpolator;
    private int DURATION = 500;
    private int totalOffset = 0;//动画总的偏移量
    private int lastIndex = 0;//上一个页面的position
    private boolean initVideo = false;//是否已经初始化videoView

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
        indexRadiog.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tabWidth = indexRadiog.getMeasuredWidth() / 3;
        ViewGroup.LayoutParams params = indexIm.getLayoutParams();
        params.width = tabWidth;

        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        indexIm.setImageMatrix(matrix);// 设置动画初始位置
        overshootInterpolator = new OvershootInterpolator();
        wantToRbtn.setTextColor(getResources().getColor(R.color.theme_puple));
        wantToRbtn.setOnClickListener(this);
        hadGoRbtn.setOnClickListener(this);
        commentRbtn.setOnClickListener(this);
    }

    @Override
    public void bindData() {
////回复评论
        bottomOptionBar.setVisibility(View.VISIBLE);
        //表情
        new EmotionUtils(mActivity, vp_emotion_dashboard, sendComment_edt);

        sendComment_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {

                if (focus) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    imm.hideSoftInputFromWindow(sendComment_edt.getWindowToken(), 0);

                }
            }
        });

        sendComment_edt.setOnClickListener(this);
        tosendTxt.setOnClickListener(this);
        emotion_im.setOnClickListener(this);

    }


    /**
     * 获取fragment的实例
     *
     * @return
     */
    private void createdFragments() {

        List<Fragment> fragmentList = new ArrayList<>();
        Bundle bundle = new Bundle();
/*评论*/
        commentFragment = new CommentFragment();
//        commentFragment.initizliza(this);
        bundle.putString(CommentFragment.BUNLDE_SHARE_MESSAGE_ID, shareMessage.getObjectId());
        commentFragment.setArguments(bundle);
        commentFragment.setOnItemClickListener(onItemClick);
/*想去*/
        wantToGoFragment = new WantToGoFragment();
//        wantToGoFragment.initizliza(this);
        bundle.putStringArrayList(WantToGoFragment.BUNDLE_USERID_LIST, (ArrayList<String>) shareMessage.getShWantedNum());
        wantToGoFragment.setArguments(bundle);
/*去过*/
        hadGoFragment = new HadGoFragment();
//        hadGoFragment.initizliza(this);
        bundle.putStringArrayList(HadGoFragment.BUNDLE_USER_ID_LIST, (ArrayList<String>) shareMessage.getShVisitedNum());
        hadGoFragment.setArguments(bundle);

        fragmentList.add(wantToGoFragment);
        fragmentList.add(hadGoFragment);
        fragmentList.add(commentFragment);

        pageAdapter = new ButtombarPageAdapter(fragmentList, getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);

    }


    /**
     * 设置数据
     */
    private void setupData() {


        MyUser myUser = shareMessage.getMyUserId();

        if (TextUtils.isEmpty(shareMessage.getShContent())) {
            content_tv.setVisibility(View.GONE);
        } else {
            content_tv.setVisibility(View.VISIBLE);
            content_tv.setText(StringUtils.getEmotionContent(
                    this, content_tv, shareMessage.getShContent()));
        }


        nickname_tv.setText(TextUtils.isEmpty(myUser.getNickName()) ? "" : myUser.getNickName());
/*用户头像*/
        ImageLoader.getInstance().displayImage(
                TextUtils.isEmpty(myUser.getAvatar()) ?
                        Contants.DEFAULT_AVATAR :
                        NetworkUtils.getRealUrl(this, myUser.getAvatar()),
                avatar);


        if (TextUtils.isEmpty(shareMessage.getShTag())) {
            tag_tv.setVisibility(View.GONE);
            tagIconIm.setVisibility(View.GONE);
        } else {
            tag_tv.setText(shareMessage.getShTag());
        }

        wanto_tv.setText(shareMessage.getShWantedNum() != null && shareMessage.getShWantedNum().size() > 0 ?
                String.valueOf(shareMessage.getShWantedNum().size()) : getString(R.string.tx_wantogo));


        hadgo_tv.setText(shareMessage.getShVisitedNum() != null && shareMessage.getShVisitedNum().size() > 0 ?
                String.valueOf(shareMessage.getShVisitedNum().size()) : getString(R.string.hadgo));
        //判断当前用户是否点赞
        if (cuser != null) {
            CommonFunctionUtils.leftDrawableWantoGO(wanto_tv, shareMessage.getShWantedNum(), cuser.getObjectId());//设置图标
            CommonFunctionUtils.leftDrawableVisited(hadgo_tv, shareMessage.getShVisitedNum(), cuser.getObjectId());
        }

        ceatedAt_tv.setText(shareMessage.getCreatedAt());

        comment_tv.setText(shareMessage.getShCommNum() > 0 ?
                String.valueOf(shareMessage.getShCommNum()) : getString(R.string.tx_comment));

        //位置信息
        if (!TextUtils.isEmpty(shareMessage.getShLocation())) {
            loactioInfoTx.setVisibility(View.VISIBLE);
            loactioInfoTx.setText(shareMessage.getShLocation());
            loactioInfoTx.setOnClickListener(this);
        }

/**
 * 设置图片
 */
        if (shareMessage.getShImgs() != null && shareMessage.getShImgs().size() > 0) {
            setImage();
        }
        /**
         * 分享才需要判断是否有视频信息
         */
        setVideo();


        nickname_tv.setOnClickListener(this);
        avatar.setOnClickListener(this);
        wanto_tv.setOnClickListener(this);
        hadgo_tv.setOnClickListener(this);
        comment_tv.setOnClickListener(this);
        tag_tv.setOnClickListener(this);

        //创建下方的viewpager，显示点赞用户、评论内容
        createdFragments();

    }

    /**
     * 设置图片
     */
    private void setImage() {
        multiImageView.setList(DataFormateUtils.thumbnailList(this, shareMessage.getShImgs()));
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
//
//
//            case MESSAGE_SHARE_MESSAGE:
//
//
//                break;

            case MESSAGE_BING_MESSAGE://将数据绑定到控件中
/*
                这里使用了统一的数据格式，shareMessage
*/
                setupData();


                break;
            case MESSAGE_REFRESH_COMMENT:

                //评论数量加1
                commentIncrement();
                //清理工作
                sendFinish();
                //刷新数据
                commentFragment.refreshData();
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
     * 设置视频
     */
    private void setVideo() {

        if (shareMessage.getVideo() != null
                && !TextUtils.isEmpty(shareMessage.getVideo().getFileUrl(this))) {

            final String videoUrl = shareMessage.getVideo().getFileUrl(this);
            initVideo = true;
            videoLayout.setVisibility(View.VISIBLE);
            videoPrevideo.setVisibility(View.VISIBLE);
            videoDownloadPb.setVisibility(View.VISIBLE);

/*判断视频预览图有没有*/
            if (shareMessage.getVideoPreview() != null) {
                ImageHandlerUtils.loadIamge(this, shareMessage.getVideoPreview().getFileUrl(this), videoPrevideo);
            } else {
                videoPrevideo.setBackgroundColor(this.getResources().getColor(R.color.gray_lighter));
            }

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0.0f, 0.0f);
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
/*需要播放*/
                    mp.setLooping(true);
                    mp.start();
                }
            });


            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(mActivity, VideoplayerActivity.class);
                        intent.putExtra(Contants.INTENT_KEY_VIDEO_PATH, shareMessage.getVideo().getFileUrl(mActivity));
                        startActivity(intent);
                        overridePendingTransition(0, 0);

                    }

                    return false;
                }
            });

            //下载并且播放视频
            downloadVideo(videoUrl, videoView, videoPrevideo, videoDownloadPb);


        }

    }

    /**
     * 下载视频并且播放
     *
     * @param url           getfileurl
     * @param videoView     播放器
     * @param videoPrevideo 视频预览图片
     * @param pb            进度条
     */
    private void downloadVideo(final String url, final VideoView videoView, final ImageView videoPrevideo, final ProgressBar pb) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + Contants.FILE_PAHT_DOWNLOAD + url.substring(url.lastIndexOf('/') + 1);
        File file = new File(filePath);
//        videoPlayerList.add(view);

        if (file.exists()) {//视频已经下载了
            videoView.setVideoPath(filePath);
            pb.setVisibility(View.GONE);
            videoPrevideo.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();


        } else {//视频未下载，进行下载

            //下载完成之后进行播放
            NetworkReuqest.call(this, url, new NetworkReuqest.JsonRequstCallback<String>() {
                @Override
                public void onSuccess(String videoPath) {
//                    LogUtils.E("response Filepath = " + object);
                    videoView.setVideoPath(videoPath);
                    pb.setVisibility(View.GONE);
                    videoPrevideo.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.start();
                }

                @Override
                public void onFaile(VolleyError error) {
                    Toast.makeText(mActivity, R.string.toast_download_video_failure, Toast.LENGTH_LONG).show();
                }
            });


        }


    }

    /**
     * 查看用户资料
     *
     * @param v
     */
    private void showUserInfo(View v) {

        PopupWinUserInfo userInfo = new PopupWinUserInfo(this, shareMessage.getMyUserId());
        userInfo.onShow(v);
    }


    /**
     * 根据启动该类的的activity进行分辨信息格式
     * 分成两种情况：直接是share格式、另外一个种是remodelModel格式
     *
     * @param bundle intent中数据
     */
    private void getData(final Bundle bundle) {

        if (Contants.CLAZZ_DISCOVER_ACTIVITY.equals(superTagClazz)
                || Contants.CLAZZ_MESSAGE_CENTER_ACTIVITY.equals(superTagClazz)) {
            shareMessage = (ShareMessage_HZ) bundle
                    .getSerializable(Contants.INTENT_KEY_DISCOVER);
        } else {
    /*将通用格式格式化成discover列表的数据结构*/
            shareMessage = DataFormateUtils.formateDataRemoteModel((RemoteModel) bundle
                    .getSerializable(Contants.CLAZZ_DATA_MODEL));
        }

        mHandler.sendEmptyMessage(MESSAGE_BING_MESSAGE);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {

            if (layout_comment.getVisibility() == View.VISIBLE) {
                layout_comment.setVisibility(View.GONE);
                sendComment_edt.clearFocus();
                bottomOptionBar.setVisibility(View.VISIBLE);
                return true;
            } else {
                back2superClazz();
            }
        }


        return super.dispatchKeyEvent(event);
    }

    @Override
    public void mBack() {
        back2superClazz();

    }

    /**
     * 返回上一级
     */
    private void back2superClazz() {
//        // TODO: 2016-04-03 使用startActivityForResult实现消息更新，也可以使用finish结束当前activity
//        if (Contants.CLAZZ_PERSONAL_ACTIVITY.equals(superTagClazz)
//                || Contants.CLAZZ_WANT_TO_GO_ACTIVITY.equals(superTagClazz)
//                || Contants.CLAZZ_USER_RECORD_ACTIVITY.equals(superTagClazz)) {//结束当前的Activity就好
//            finish();
//        } else {//其他需要跳转？好像也不用，先保持吧
//            backAFinsish();
//        }
        finish();
    }

    /**
     * 评论点击事件
     */
    private CommentListView.OnItemClickListener onItemClick = new CommentListView.OnItemClickListener() {
        @Override
        public void onItemClick(Comment_HZ comment, int position) {
            prepareSend();
        }
    };

    /**
     * 准备发送
     */
    private void prepareSend() {
        bottomOptionBar.setVisibility(View.GONE);
        layout_comment.setVisibility(View.VISIBLE);
        sendComment_edt.requestFocus();

    }

    /**
     * 结束退出
     */
    private void backAFinsish() {

        mBackStartActivity(superTagClazz);
        this.finish();
    }

    /**
     * 准备发送评论工作
     */
    private void startPrepare() {
        //滑动到评论页面
        viewPager.setCurrentItem(2, true);

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

            case R.id.txt_message_detail_tosend://发送消息
                finishPrepare();
                receiverId = shareMessage.getMyUserId().getObjectId();
                sendComment();

                break;
            case R.id.id_im_userH://用户资料
                showUserInfo(v);
                break;
            case R.id.id_userName:
                showUserInfo(v);
                break;

            case R.id.id_tx_wantogo://想去--数量
                viewPager.setCurrentItem(0, true);
                v.setClickable(false);
                getUser();

                if (cuser != null) {

                    List<String> shWantedNum = shareMessage.getShWantedNum();

                    wantToGo(UserUtils.isHadCurrentUser(shWantedNum, cuser.getObjectId()), v);
                } else {
                    v.setClickable(true);
                    Dialog4Tips.loginFunction(mActivity);
                }

                break;

            case R.id.id_hadgo://去过--数量
                viewPager.setCurrentItem(1, true);
                v.setClickable(false);
                getUser();

                if (cuser != null) {

                    List<String> shVisitedNum = shareMessage.getShVisitedNum();
                    visit(UserUtils.isHadCurrentUser(shVisitedNum, cuser.getObjectId()), v);

                } else {
                    v.setClickable(true);
                    Dialog4Tips.loginFunction(mActivity);
                }
                break;

            case R.id.id_tx_comment://评论数量
                //滑动到评论界面
                viewPager.setCurrentItem(2, true);
                getUser();
                /*显示评论输入面板*/
                editComment();
                break;

            case R.id.id_tx_tab://标签

                LogUtils.ts("标签的点击事件");
                break;

            case R.id.rb_message_detail_want_to://想去，也就是收藏
                viewPager.setCurrentItem(0);
                break;
            case R.id.rb_message_detail_had_go://去过
                viewPager.setCurrentItem(1);
                break;
            case R.id.rb_message_detail_comment://评论
                viewPager.setCurrentItem(2);
                break;

            case R.id.tv_item_message_detail_location_info://位置信息
                showOnMap();
                break;
        }

    }

    /**
     * 在地图上面显示位置
     * 或者导航
     */
    private void showOnMap() {

        if (shareMessage.getGeographic() != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Contants.INTENT_BMOB_GEOPONIT, shareMessage.getGeographic());
            mStartActivity(BaiduMapActivity.class, bundle);

        } else {
            toast(R.string.toastP_location_info_empty);
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

            shareMessage.getShVisitedNum().remove(cuser.getObjectId());
        } else {
//            strId = R.string.cancel_collect_success;
            leftDrawID = R.drawable.icon_hadgo;
            shareMessage.getShVisitedNum().add(cuser.getObjectId());
        }


        ((TextView) v).setText(shareMessage.getShVisitedNum() == null ?
                getString(R.string.hadgo) : String.valueOf(shareMessage.getShVisitedNum().size()));
        ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(leftDrawID, 0, 0, 0);


        shareMessage.update(this, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
//                mToast(strId);
                v.setClickable(true);
                updateHadGo();

            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
                toast(getString(R.string.txt_erro_message) + i);

                LogUtils.d(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }

    /**
     * 更新想去的用户列表
     */
    private void updateHadGo() {


        hadgo_tv.setText(shareMessage.getShVisitedNum() != null && shareMessage.getShVisitedNum().size() > 0 ?
                String.valueOf(shareMessage.getShVisitedNum().size()) : getString(R.string.hadgo));
        hadGoFragment.setWantUserId(shareMessage.getShVisitedNum());
        hadGoFragment.initData();
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
        try {
            jsonObject.put("message", "1");
            jsonObject.put("userid", cuser.getObjectId());
            jsonObject.put("collectionid", shareMessage.getObjectId());

        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtils.d("云端代码 添加参数失败 " + e.toString());
        }

        if (hadWant) {
//            strId = R.string.cancel_collect_success;
            leftDrawID = R.drawable.icon_wantogo;
//操作收藏表
            BmobApi.AsyncFunction(this, jsonObject, BmobApi.REMOVE_COLLECTION, new AsyncListener() {
                @Override
                public void onSuccess(Object object) {

                    BaseModel baseModel = (BaseModel) object;

                    if (baseModel.getCode() == BaseModel.SUCCESS) {
//                        mToast(R.string.operation_success);
                        shareMessage.getShWantedNum().remove(cuser.getObjectId());
                        updateWantTo();
                        LogUtils.d("删除收藏记录 成功  data = " + baseModel.getData());
                    } else {

                        LogUtils.d("删除收藏记录 失败  data = " + baseModel.getData());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    toast(getString(R.string.txt_erro_message) + code);
                }
            });
        } else {
//            strId = R.string.collect_success;
            leftDrawID = R.drawable.icon_wantogo_light;

            shareMessage.getShWantedNum().add(cuser.getObjectId());

//MESSAGE_TYPE_SHAREMESSAGE
            BmobApi.saveCollectionShareMessage(this, cuser, shareMessage, Contants.MESSAGE_TYPE_SHAREMESSAGE);

        }

        ((TextView) v).setText(shareMessage.getShWantedNum() == null ?
                getString(R.string.tx_wantogo) : String.valueOf(shareMessage.getShWantedNum().size()));
        ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(leftDrawID, 0, 0, 0);

        shareMessage.update(this, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
//                mToast(strId);
                v.setClickable(true);
                updateWantTo();
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
                toast(getString(R.string.txt_erro_message) + i);
                LogUtils.d(" wantToGo failure  erro code = " + i + " erro message = " + s);
            }
        });

    }

    /**
     * 更新喜欢的用户
     */
    private void updateWantTo() {

        wanto_tv.setText(shareMessage.getShWantedNum() != null && shareMessage.getShWantedNum().size() > 0 ?
                String.valueOf(shareMessage.getShWantedNum().size()) : getString(R.string.tx_wantogo));

        wantToGoFragment.setUserIdList(shareMessage.getShWantedNum());
        wantToGoFragment.initData();

    }

    /**
     * 评论的布局
     */
    private void editComment() {

        layout_comment.setVisibility(View.VISIBLE);
        bottomOptionBar.setVisibility(View.GONE);
        sendComment_edt.requestFocus();

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

            bottomOptionBar.setVisibility(View.VISIBLE);
            sendComment_edt.clearFocus();

            BmobApi.sendMessage(mActivity, shareMessage.getMyUserId().getObjectId(), receiverId, sendComment_edt.getText().toString(),
                    shareMessage.getObjectId(), new BmobApi.SendMessageCallback() {
                        @Override
                        public void onSuccessReflesh() {

                            //刷新评论数据
                            mHandler.sendEmptyMessage(MESSAGE_REFRESH_COMMENT);
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

        shareMessage.increment(Contants.PARAMS_SHCOMM_NUM);
        shareMessage.update(mActivity);
        comment_tv.setText(shareMessage.getShCommNum() > 0 ?
                String.valueOf(shareMessage.getShCommNum()) : getString(R.string.tx_comment));
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
                wantToRbtn.setTextColor(getResources().getColor(R.color.theme_puple));
                hadGoRbtn.setTextColor(Color.BLACK);
                commentRbtn.setTextColor(Color.BLACK);

            } else if (position == 1) {
                wantToRbtn.setTextColor(Color.BLACK);
                hadGoRbtn.setTextColor(getResources().getColor(R.color.theme_puple));
                commentRbtn.setTextColor(Color.BLACK);
            } else {
                wantToRbtn.setTextColor(Color.BLACK);
                hadGoRbtn.setTextColor(Color.BLACK);
                commentRbtn.setTextColor(getResources().getColor(R.color.theme_puple));
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

    /**
     * 动画效果
     *
     * @param index
     */
    private void changeTab(int index) {
//        int position = tabWidth*index - tabWidth / 3 - bmpW / 3 - offset;
        totalOffset = totalOffset + (index - lastIndex) * tabWidth;
        lastIndex = index;
//        LogUtils.e(" translationX = "+totalOffset);
        ViewPropertyAnimator.animate(indexIm).translationX(totalOffset)
                .setInterpolator(overshootInterpolator).setDuration(DURATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (initVideo) {
            if (videoView != null) {
                videoView.start();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (initVideo) {
            if (videoView != null && videoView.isPlaying()) {
                videoView.pause();
            }
        }
    }
}
