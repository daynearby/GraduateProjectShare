package com.young.share;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.VideoPlayerUtils;

/**
 * function ：视频全屏播放，在列表中进行小视频播放，点击之后进入全屏播放，并且有声音
 * 视频的宽高 4:3
 * Created by young fuJin on 2016/1/13.
 */
public class VideoplayerActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private String videPath;
    private VideoPlayerUtils videoPlayerUtils;
    private ShareMessage_HZ shareMessage;
    private boolean isAlbum = false;
    private Intent intent;

    private SurfaceView videoView;
    private ActionBar actionBar;
    private boolean isLike = false;

    private static final int PLAY_VIDEO = 0x01;//延迟加载视频
    private static final long DELAY_PLAY_VIDEO = 200;//延迟时间


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initData() {
        initializeToolbar();
        setTitle(getString(R.string.txt_video_review));
        shareMessage = (ShareMessage_HZ) getIntent().getSerializableExtra(Contants.INTENT_KEY_POST_VIDEO);
        /*由主界面跳转*/
        if (shareMessage == null) {
            videPath = getIntent().getStringExtra(Contants.INTENT_KEY_VIDEO_PATH);
            isAlbum = false;
        } else {
            videPath = shareMessage.getVideo().getFileUrl(mActivity);
            isAlbum = true;

        }
//        mHandler.sendEmptyMessage(PLAY_VIDEO);
    }

    @Override
    protected void findviewbyid() {

        findViewById(R.id.rllayout_bg).setOnClickListener(this);
        videoView = $(R.id.suview_video_player);


    }

    @Override
    protected void bindData() {

        //只有在列表中进行预览的时候才能轻触退出
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidthPixels(mActivity);
        lp.height = DisplayUtils.getScreenWidthPixels(mActivity) / 4 * 3;

        videoView.setOnClickListener(this);

        videoPlayerUtils = new VideoPlayerUtils(mActivity, videoView, videPath, true);

    }

    @Override
    protected void handerMessage(Message msg) {
        switch (msg.what) {

            case PLAY_VIDEO:

                videoPlayerUtils = new VideoPlayerUtils(mActivity, videoView, videPath, true);

                break;
        }
    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_post_video);
//        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        actionBar = getSupportActionBar();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.suview_video_player:
                if (!isAlbum) {
                    mActivity.finish();
                } else {
                    barShowOrHide();
                }
                break;


        }
    }

    /**
     * actionbar 与下方的bar同时出现和隐藏
     */
    private void barShowOrHide() {
        if (actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    /**
     * 返回键监听
     */
    protected void mBack() {
        mActivity.finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("onResume ");
        //
        if (videoPlayerUtils != null && !videoPlayerUtils.isPaly()) {
            videoPlayerUtils.startPlay();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //
        if (videoPlayerUtils != null && videoPlayerUtils.isPaly()) {
            videoPlayerUtils.pausePlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        if (videoPlayerUtils != null) {
            videoPlayerUtils.stopPlay();
        }
    }
}
