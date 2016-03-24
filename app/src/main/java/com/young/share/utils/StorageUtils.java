package com.young.share.utils;

import android.content.Context;
import android.os.Environment;

import com.young.share.config.Contants;

import java.io.File;

/**
 * 涉及存储的操作类
 * Created by Nearby Yang on 2016-03-07.
 */
public class StorageUtils {


    /**
     * 存放图片对应的文件夹
     *
     * @param context 上下文对象
     * @return file对象
     */
    public static File CreateImageFile(Context context) {

        return new File(createdDir(context, Contants.FILE_IMAGE_PATH));
    }

    /**
     * 存放图片对应的文件夹
     *
     * @param context 上下文对象
     * @return file对象
     */
    public static File CreateCacheFile(Context context) {

        return new File(createdDir(context,Contants.FILE_CACHE_PATH));
    }

    /**
     * 创建或者是获取文件夹
     *
     * @param context
     * @param dirName
     * @return
     */
    private static String createdDir(Context context, String dirName) {

        String path =Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() + dirName :
                context.getCacheDir().getAbsolutePath() + dirName;
        File filePath = new File(path);

        if (!filePath.exists()) {
            filePath.mkdirs();
        }


        return path;
    }
}
