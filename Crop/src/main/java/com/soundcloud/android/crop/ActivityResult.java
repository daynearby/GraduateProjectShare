package com.soundcloud.android.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * 再点击事件内调用Crop.pickImage(this, type)
 * 1.type在Crop类中随机定义了一个数字1993，如果传入的是1993，那么会使用相机拍照，再进行裁剪；
 * 2.type != 1993 打开系统相册
 * 3.在覆写onActivityResult中执行的方法
 * <p/>
 * Created by Nearby Yang on 2015-09-02.
 */
public class ActivityResult {

    private static final int RESULT_OK = -1;
    //裁剪好的照片名字
    public static final String storyFileName = "cropped.jpeg";
    public static final String STORY_DIC = "/image";
    private static File imageFilePath;
    //    private Context context;
    //定义输出的宽高
    private static final int size_nromal = 250;
    //输出照片的宽\高.默认是250*250,不会进行放大图片,如果图片最小边没有1500像素,那么就娶最小边的宽高
    private static final int size_large = 1500;

    /**
     * 在覆写protected void onActivityResult(int requestCode, int resultCode, Intent result)
     * 传入对应的变量
     *
     * @param activity    当前Activity的对象
     * @param requestCode onActivityResult的回调参数
     * @param resultCode  onActivityResult的回调参数
     * @param result      onActivityResult的回调参数
     * @param resultView  目标的imageView的实例化对象     @return 图片保存的uri
     * @param isBig       是否裁剪为使用比较大分辨率图片
     */
    public static boolean corpResult(Activity activity, int requestCode, int resultCode, Intent result, ImageView resultView, boolean isBig) {

        return config(activity, requestCode, resultCode, result, resultView, isBig);
    }

    private static boolean config(Activity activity, int requestCode, int resultCode, Intent result, ImageView resultView, boolean isBig) {

        boolean finish = false;


        imageFilePath = CropUtil.CreateImageFile(activity);

//        设置了返回值，如果返回值是1993 相机拍照 ，并且使用裁剪
        if (requestCode == Crop.PIC_FROM_CAMERA && resultCode == RESULT_OK) {

            beginCrop(activity, Uri.fromFile(new File(imageFilePath, Crop.imgFileName)), isBig);

        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {//选择了照片，开始裁剪
            beginCrop(activity, result.getData(), isBig);

        } else if (requestCode == Crop.REQUEST_CROP) {//裁剪完成

            finish = handleCrop(activity, resultCode, result, resultView);

        }
        return finish;
    }

    /**
     * 跳转到裁剪界面，开始裁剪
     *
     * @param activity 当前Activity的Activity
     * @param source   读取照片之后返回的照片uri
     */
    private static void beginCrop(Activity activity, Uri source, boolean isLarge) {

        Uri destination = Uri.fromFile(new File(imageFilePath, storyFileName));
        if (isLarge) {
            Crop.of(source, destination).asSquare().withMaxSize(size_large, size_large).start(activity);
        } else {
            Crop.of(source, destination).asSquare().withMaxSize(size_nromal, size_nromal).start(activity);
        }
    }

    /**
     * 裁剪并且上传
     *
     * @param activity   当前Activity的Activity
     * @param resultCode 返回码，由Activity的回调返回
     * @param result     intent对象，又Activity的回调返回
     * @param resultView 目标imageView的实例化对象
     */
    private static boolean handleCrop(Activity activity, int resultCode, Intent result, ImageView resultView) {
        boolean isfinish = false;
        Uri imguri;//裁剪好的照片uri

        if (resultCode == RESULT_OK) {

            imguri = Crop.getOutput(result);

            if (imguri != null) {
                isfinish = true;
            }

            if (null != resultView) {
                Bitmap bg = BitmapFactory.decodeFile(imguri.getEncodedPath());
                resultView.setImageBitmap(bg);
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            isfinish = false;
            Toast.makeText(activity, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
        return isfinish;
    }


}
