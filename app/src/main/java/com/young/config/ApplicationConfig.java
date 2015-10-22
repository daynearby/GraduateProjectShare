package com.young.config;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.litepal.LitePalApplication;

import java.io.File;

import cn.smssdk.SMSSDK;

/**
 * Created by Nearby Yang on 2015-07-02.
 */
public class ApplicationConfig extends LitePalApplication {

    //是否使用调试
    public final static boolean isDebug = true;

    //单例模式
    private volatile static ApplicationConfig instance;


    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
//初始化imageloader
        initImageLoader(getApplicationContext());

    }

    /**
     * 应用的基本配置
     */
    private void initConfig() {
        LitePalApplication.initialize(this);
        SMSSDK.initSDK(this, Contants.SMS_APP_KEY, Contants.SMS_APP_SECRET);
    }

    private void initImageLoader(Context ctx) {

//        File cacheFile = new File(Environment.getExternalStorageState(), "uilCache");
        File cacheFile = StorageUtils.getOwnCacheDirectory(ctx, "share/UILCache/");

        ImageLoaderConfiguration imConfig = new ImageLoaderConfiguration.Builder(ctx)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3) //线程池内线程的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)  // SD卡缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // 由原先的discCache -> diskCache
                .diskCache(new UnlimitedDiskCache(cacheFile))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(ctx, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(imConfig);
    }


    /**
     * 单例的Application
     *
     * @return ApplicationConfig.this
     */
    public static ApplicationConfig getInstance() {
        if (instance == null) {
            synchronized (ApplicationConfig.class) {
                if (instance == null) {
                    instance = (ApplicationConfig) getContext();
                }
            }
        }
        return instance;
    }
}
