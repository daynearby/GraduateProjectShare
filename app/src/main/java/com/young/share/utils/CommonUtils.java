package com.young.share.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    /**
     * 检查是否有网络
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isAvailable();

    }

    /**
     * 检查是否是WIFI
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 检查是否是移动网络
     */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    private static NetworkInfo getNetworkInfo(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 检查SD卡是否存在
     */
    public static boolean checkSdCard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    //    private void beginCrop(Uri source) {
//
//        File filepath;
//
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            //外置内存卡存在
//            File share = new File(Environment.getExternalStorageDirectory(), "share/user/icon");
//            share.mkdirs();
//
//            filepath = new File(Environment.getExternalStorageDirectory(), "share/user/icon/user");
//
//        } else {
//            //外置内存卡不存在
//
//            File share = new File(this.getCacheDir(), "/user/icon");
//            share.mkdirs();
//
//            filepath = new File(this.getCacheDir(), "user/icon/user");
//
//        }

    /**
     * 存放图片对应的文件夹
     *
     * @param context 上下文对象
     * @return file对象
     */
    public static File CreateImageFile(Context context) {
        String path;
        File imageFilePath;

        if (checkSdCard()) {//存在

            path = context.getExternalCacheDir().getAbsolutePath() + "/image";
            imageFilePath = new File(path);

            if (!imageFilePath.exists()) {
                imageFilePath.mkdir();
            }

        } else {

            path = context.getCacheDir().getAbsolutePath() + "/image";

            imageFilePath = new File(path);
            if (!imageFilePath.exists()) {
                imageFilePath.mkdir();
            }
        }

        return imageFilePath;
    }


    /**
     * 判断email格式是否正确
     *
     * @param email 要验证的字符串
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 获取程序分配到的内存空间
     *
     * @return
     */
    public static int getRuntimeRAM() {
        return (int) Runtime.getRuntime().totalMemory();

    }
}
