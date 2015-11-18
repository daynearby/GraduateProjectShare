package com.young.utils;

import android.content.Context;

import com.bmob.BmobProFile;
import com.young.config.Contants;

/**
 * 网络请求工具类
 * 获取网址或者截取网址
 * Created by Nearby Yang on 2015-11-13.
 */
public class NetworkUtils {

    /**
     * 获取签名url
     *
     * @param context
     * @param url
     * @param isLocation
     * @return
     */
    public static String getRealUrl(Context context, String url, boolean isLocation) {

        if (isLocation) {
            return url;
        } else {
            String filename = url.substring(url.lastIndexOf('/') + 1);
            return BmobProFile.getInstance(context).signURL(filename, url, Contants.BMOB_APP_ACCESS_KEY, 0, null);

        }
    }
}
