package com.young.share.utils;

import android.content.Context;

import java.io.File;

/**
 * 涉及存储的操作类
 * Created by Nearby Yang on 2016-03-07.
 */
public class StorageUtils {
    /**
     * 检查SD卡是否存在
     */
    public static boolean checkSdCard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

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

            path = context.getExternalCacheDir().getAbsolutePath() + "/share/image";
            imageFilePath = new File(path);

            if (!imageFilePath.exists()) {
                imageFilePath.mkdir();
            }

        } else {

            path = context.getCacheDir().getAbsolutePath() + "/share/image";

            imageFilePath = new File(path);
            if (!imageFilePath.exists()) {
                imageFilePath.mkdir();
            }
        }

        return imageFilePath;
    }

    /**
     * 存放图片对应的文件夹
     *
     * @param context 上下文对象
     * @return file对象
     */
    public static File CreateCacheFile(Context context) {
        String path;
        File imageFilePath;

        if (checkSdCard()) {//存在

            path = context.getExternalCacheDir().getAbsolutePath() + "/share/cache";
            imageFilePath = new File(path);

            if (!imageFilePath.exists()) {
                imageFilePath.mkdir();
            }

        } else {

            path = context.getCacheDir().getAbsolutePath() + "/share/cache";

            imageFilePath = new File(path);
            if (!imageFilePath.exists()) {
                imageFilePath.mkdir();
            }
        }

        return imageFilePath;
    }
}
