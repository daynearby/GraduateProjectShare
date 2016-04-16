package com.young.share.shareSocial;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.young.share.config.ApplicationConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 社会化分享
 * Created by Nearby Yang on 2016-04-08.
 */
public class SocialShareManager {

    /**
     * 纯文字的分享
     * @param context
     * @param content 文字
     */
    public static void shareText(Context context, String content){
        shareShow(context,content,null);
    }

    /**
     * 分享纯图片
     * @param context
     * @param imageUrl
     */
    public static void shareImage(Context context,  String imageUrl){
        shareShow(context,null,imageUrl);
    }
    /**
     * 分享 社会化，只能分享一张图片或者是一句话，不能多图
     */
    public static void shareShow(Context context, String content, String imageUrl) {


        /**
         * UMImage image = new UMImage(ShareActivity.this,
         BitmapFactory.decodeFile("/mnt/sdcard/icon.png")));

         */



    }




    /**
     * 分享多图
     *
     * @param context
     * @param filePathList 文件的地址
     */
    public static void shareImagesByIntent(Context context, List<String> filePathList) {
        ArrayList<Uri> picturesUriArrayList = new ArrayList<Uri>();

//        File pictureFile1=new File
//                (Environment.getExternalStorageDirectory()+File.separator+"test1.png");
//        File pictureFile2=new File
//                (Environment.getExternalStorageDirectory()+File.separator+"test2.png");
//
//        Uri pictureUri1=Uri.fromFile(pictureFile1);
//        Uri pictureUri2=Uri.fromFile(pictureFile2);
//
//        picturesUriArrayList.add(pictureUri1);
//        picturesUriArrayList.add(pictureUri2);

        for (String filePath : filePathList) {
            Uri pictureUri = Uri.fromFile(new File(filePath));
            picturesUriArrayList.add(pictureUri);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, picturesUriArrayList);
        intent.setType("image/jpeg");
        Intent.createChooser(intent, "分享多张图片");
        context.startActivity(intent);
    }


    /**
     * toast
     *
     * @param msg
     */
    private static void toast(String msg) {
        Toast.makeText(ApplicationConfig.getContext(), msg, Toast.LENGTH_LONG).show();

    }
}
