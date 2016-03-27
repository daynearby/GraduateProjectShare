package com.young.share.config;

import android.content.Context;
import android.hardware.Camera;

import com.duanqu.qupai.engine.session.MovieExportOptions;
import com.duanqu.qupai.engine.session.VideoSessionCreateInfo;
import com.young.share.R;

/**
 * 趣拍录制视频的配置文件
 * 视频录制的配置文件配置
 * Created by Administrator on 2016/3/16.
 */
public class VideoRecorderConfig {

    /**
     * 视频压缩参数设置
     */
    private static MovieExportOptions movieOptions() {
        /**
         * 压缩参数，可以自由调节
         */
        return new MovieExportOptions.Builder()
                .setVideoProfile(Contants.VIEDO_PROFILE)
                .setVideoBitrate(Contants.VIDEO_BITRATE)
                .setVideoPreset(Contants.DEFAULT_VIDEO_Preset).setVideoRateCRF(Contants.DEFAULT_VIDEO_RATE_CRF)
                .setOutputVideoLevel(Contants.DEFAULT_VIDEO_LEVEL)
                .setOutputVideoTune(Contants.DEFAULT_VIDEO_TUNE)
                .configureMuxer(Contants.DEFAULT_VIDEO_MOV_FLAGS_KEY, Contants.DEFAULT_VIDEO_MOV_FLAGS_VALUE)
                .build();
    }

    /**
     * 设置拍摄界面的一下控件显示与隐藏
     * 还有一些功能的启动与关闭，如闪光灯，摄像头切换
     *
     * @return
     */
    public static VideoSessionCreateInfo CreateInfo(Context context) {
        /**
         * 界面参数
         */
        return new VideoSessionCreateInfo.Builder()
                .setOutputDurationLimit(Contants.DEFAULT_DURATION_MAX_LIMIT)//最长时间
                .setOutputDurationMin(Contants.DEFAULT_DURATION_LIMIT_MIN)//最短时间
                .setMovieExportOptions(movieOptions())
                .setBeautyProgress(Contants.BEAUTY_PROGRESS)//美颜程度
                .setBeautySkinOn(Contants.BEAUTY_SKIN)//是否开启美颜
                .setCameraFacing(
                        Camera.CameraInfo.CAMERA_FACING_BACK)//默认使用后置镜头
                .setVideoSize(Contants.OUTPUT_VIDEO_WIDTH, Contants.OUTPUT_VIDEO_HEIGHT)
                .setCaptureHeight(context.getResources().getDimension(R.dimen.size_capture_height))//摆设按钮布局高度
                .setBeautySkinViewOn(false)//美颜按钮不显示
                .setFlashLightOn(true)//闪光灯开关 开启，默认不开启
                .setTimelineTimeIndicator(true)//时间进度条上的时间指示器，不开启
                .build();
    }
}
