package com.young.share.utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.young.share.R;
import com.young.share.config.Contants;
import com.young.share.network.NetworkReuqest;

import java.io.File;
import java.io.IOException;

/**
 * function ：播放视频的控件，是否需要开子线程？看播放质量
 * Created by young fuJin on 2015/12/31.
 */
public class VideoPlayerUtils implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private Context context;
    private String filePath;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private boolean soundEnable;
    private boolean isPause = false;//暂停
    private boolean isPrepare;//准备播放

    /**
     * 视频播放类
     *
     * @param context
     * @param surfaceView
     * @param filePath    video path
     * @param soundEnable 声音是否有
     */
    public VideoPlayerUtils(Context context, SurfaceView surfaceView, String filePath, Boolean soundEnable) {
        this.context = context;
        this.soundEnable = soundEnable;
        this.surfaceView = surfaceView;
        this.filePath = filePath;

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(new HolderCallback());
//        surfaceHolder.setFixedSize(176, 144);//设置分辨率
//        surfaceHolder.lockCanvas();

    }

    /**
     * 另外一个构造方法，使用textureview进行播放，不是使用surfaceview
     *
     * @param textureView
     */
    public VideoPlayerUtils(TextureView textureView, String videoUrl) {

        textureView.setSurfaceTextureListener(new TextureListener(videoUrl));
        LogUtils.e("video url " + videoUrl);
    }

    /**
     * setSurfaceTextureListener
     * <p/>
     * texture + mediaplayer
     * <p/>
     * 监听器
     */
    private class TextureListener implements TextureView.SurfaceTextureListener {

        private String videoUrl;

        public TextureListener(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Surface s = new Surface(surface);


            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(videoUrl);
                mediaPlayer.setSurface(s);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        LogUtils.e("percent + " + percent);
                    }
                });
//                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        LogUtils.e("start play ");
                    }
                });
//                mediaPlayer.setOnVideoSizeChangedListener(this);
                mediaPlayer.prepare();
//                mediaPlayer.start();

            } catch (IllegalArgumentException e) {
                LogUtils.d("erro " + e.toString());
            } catch (SecurityException e) {
                LogUtils.d("erro " + e.toString());
            } catch (IllegalStateException e) {
                LogUtils.d("erro " + e.toString());
            } catch (IOException e) {
                LogUtils.d("erro " + e.toString());
            }


        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }


    /**
     * surfaceview callback
     */
    private class HolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtils.e(" suerfaceview created");

            if (mediaPlayer == null) {
                initMediaPlayer();
            }


        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtils.e(" suerfaceview changed");

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtils.e(" suerfaceview destroy");
            //停止之后会释放掉mediaplayer
            stopPlay();
        }

    }

    @Override
    public void onPrepared(MediaPlayer player) {
        LogUtils.e(" suerfaceview prepared");
        if (!soundEnable) {
            player.setVolume(0.0f, 0.0f);
        }
        mediaPlayer.start();
        isPrepare = true;
    }

    /**
     * 初始化mediaplayer
     */
    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.setDisplay(surfaceHolder);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型,surface会自动设置类型，获取
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
//        mediaPlayer.reset();
        mediaPlayer.setLooping(true);//循环播放


//mediaPlayer
        try {
            mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 视频的路径
     *
     * @param filePath
     */
    public void setFilePath(String filePath) {
        try {
            mediaPlayer.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isPrepare) {
            mediaPlayer.start();
        }
    }

    /**
     * 是否有音量
     *
     * @param soundEnable
     */
    public void setSoundEnable(boolean soundEnable) {
        this.soundEnable = soundEnable;
    }

    /**
     * 停止播放释放资源
     * 从文档说明中得到，只需要进行stop就可以了，释放资源，文档没有
     */
    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
//            mediaPlayer.reset();
//            mediaPlayer.release();
            mediaPlayer = null;
            isPause = true;
        }
    }

    public void startPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPause = true;
        }

    }


    public void stopTextureViewPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
//            mediaPlayer.reset();
//            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 视频是否正在播放
     *
     * @return
     */
    public boolean isPaly() {
        return mediaPlayer != null && mediaPlayer.isPlaying();

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.v("Play Error", "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.v("Play Error", "MEDIA_ERROR_UNKNOWN");
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 视频播放处理
     *
     * @param url
     * @param view
     */
    public static void videoPlayHelper(final Context context, String url, final VideoView view) {

        String filePath = Environment.getExternalStorageDirectory().getPath() + Contants.FILE_PAHT_DOWNLOAD + url.substring(url.lastIndexOf('/') + 1);
        File file = new File(filePath);
//        videoPlayerList.add(view);

        if (file.exists()) {//视频已经下载了
            view.setVideoPath(filePath);
            view.start();

        } else {//视频未下载，进行下载

            //下载完成之后进行播放
            NetworkReuqest.call(context, url, new NetworkReuqest.JsonRequstCallback() {

                @Override
                public void onSuccess(Object object) {

                    LogUtils.e("response Filepath = " + object);
                    view.setVideoPath((String) object);
                    view.start();
                }

                @Override
                public void onFaile(VolleyError error) {
                    Toast.makeText(context, R.string.toast_download_video_failure, Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}
