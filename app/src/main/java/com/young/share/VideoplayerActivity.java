package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.network.NetworkReuqest;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.VideoPlayerUtils;

import java.io.File;

/**
 * function ：视频全屏播放，在列表中进行小视频播放，点击之后进入全屏播放，并且有声音
 * 视频的宽高 4:3
 * Created by young fuJin on 2016/1/13.
 */
public class VideoplayerActivity extends BaseAppCompatActivity implements View.OnClickListener {

    //    @InjectView(R.id.suview_video_player)
//    private SurfaceView surfaceView;
    @InjectView(R.id.rllayout_bg)
    private RelativeLayout parentView;
    @InjectView(R.id.im_video_preview)
    private ImageView videoPreView;
    @InjectView(R.id.vdv_video)
    private VideoView videoView;
    @InjectView(R.id.pb_video_loading)
    private ProgressBar progressBar;

    private String videoPath;
    private String videoPreviewPath;
    private VideoPlayerUtils videoPlayerUtils;
    //    private ShareMessage_HZ shareMessage;
    private Intent intent;
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
//        shareMessage = (ShareMessage_HZ) getIntent().getSerializableExtra(Contants.INTENT_KEY_POST_VIDEO);
        /*由主界面跳转*/
//        if (shareMessage == null) {
        videoPath = getIntent().getStringExtra(Contants.INTENT_KEY_VIDEO_PATH);
        videoPreviewPath = getIntent().getStringExtra(Contants.INTENT_KEY_VIDEO_PREVIEW_PATH);
        LogUtils.d(" videoPath = " + videoPath
                + " videoPreviewPath = " + videoPreviewPath);
//        } else {
//            videPath = shareMessage.getVideo().getFileUrl(mActivity);
//
//        }
    }

    @Override
    protected void findviewbyid() {

        parentView.setOnClickListener(this);

    }

    @Override
    protected void bindData() {

        //只有在列表中进行预览的时候才能轻触退出
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidthPixels(mActivity);
        lp.height = DisplayUtils.getScreenWidthPixels(mActivity) / 4 * 3;

        ViewGroup.LayoutParams layoutParams = videoPreView.getLayoutParams();
        layoutParams.width = DisplayUtils.getScreenWidthPixels(mActivity);
        layoutParams.height = DisplayUtils.getScreenWidthPixels(mActivity) / 4 * 3;
        ImageLoader.getInstance().displayImage(videoPreviewPath, videoPreView);

//        videoView.setOnClickListener(this);
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mActivity.finish();
                }
                return true;
            }
        });

        progressBar.setVisibility(View.VISIBLE);
//        videoPlayerUtils = new VideoPlayerUtils(mActivity, videoView, videPath, true);
//        mHandler.sendEmptyMessageDelayed(PLAY_VIDEO, 500);
        videoPlayback();

    }

    /**
     * 播放视频
     */

    private void videoPlayback() {


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
/*需要播放*/
                mp.setLooping(true);
                mp.start();
            }
        });

//        if () {
            downloadVideo();
//        } else {
//            videoView.setVideoPath(filePath);
//            progressBar.setVisibility(View.GONE);
//            videoPreView.setVisibility(View.GONE);
//            videoView.setVisibility(View.VISIBLE);
//            videoView.start();
//        }


    }

    @Override
    protected void handerMessage(Message msg) {
        switch (msg.what) {

            case PLAY_VIDEO:

//                videoPlayerUtils = new VideoPlayerUtils(mActivity, surfaceView, videoPath, true);
//                videoPreView.setVisibility(View.GONE);

                break;
        }
    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_post_video);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    /**
     * 下载视频并且播放
     */
    private void downloadVideo() {




        File file = new File(videoPath);
//        videoPlayerList.add(view);
        LogUtils.e("down load filePath = " + videoPath);

        if (file.exists()) {//视频已经下载了
            videoView.setVideoPath(videoPath);
            progressBar.setVisibility(View.GONE);
            videoPreView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();


        } else {//视频未下载，进行下载

//            String filePath = Environment.getExternalStorageDirectory().getPath()
//                    + Contants.FILE_PAHT_DOWNLOAD
//                    + StorageUtils.getFileName(videoPath);

            //下载完成之后进行播放
            NetworkReuqest.call(this, videoPath, new NetworkReuqest.JsonRequstCallback<String>() {
                @Override
                public void onSuccess(String videoPath) {
//                    LogUtils.E("response Filepath = " + object);

                    videoView.setVideoPath(videoPath);
                    progressBar.setVisibility(View.GONE);
                    videoPreView.setVisibility(View.GONE);

                    videoView.setVisibility(View.VISIBLE);
                    videoView.start();

                }

                @Override
                public void onFaile(VolleyError error) {
                    toast(R.string.toast_download_video_failure);
                }
            });


        }


    }

    @Override
    public void onClick(View v) {


        mActivity.finish();

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
