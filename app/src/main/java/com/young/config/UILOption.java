package com.young.config;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.young.share.R;

/**
 * 通用图片加载 配置
 * Created by Nearby Yang on 2015-07-02.
 */
public class UILOption {


    /**
     * 圆角 图片
     * @return
     */
    public static DisplayImageOptions setRoundOption(){
        DisplayImageOptions options=null;

        options=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loadingimg)
                .showImageOnFail(R.drawable.loadingimgfaile)
                .showImageForEmptyUri(R.drawable.loadingimaisempty)
                .considerExifParams(true)//考虑是否旋转
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();

        return options;
    }
}
