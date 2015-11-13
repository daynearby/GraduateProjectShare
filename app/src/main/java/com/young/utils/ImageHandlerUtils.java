package com.young.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.young.config.Contants;
import com.young.share.R;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 图片处理工具类
 * <p/>
 * Created by Nearby Yang on 2015-10-25.
 */
public class ImageHandlerUtils {


    /**
     * 启动选择照片的意图对象
     *
     * @return
     */
    public static void starSelectImages(Activity aty, ArrayList<String> data) {
        ArrayList<String> list = new ArrayList<>();
        Intent intent = new Intent(aty, MultiImageSelectorActivity.class);

// 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

// 最大图片选择数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, Contants.IMAGENUMBER);

// 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        if (data != null) {

            for (String s : data) {
                s = s.substring(Contants.FILE_HEAD.length(), s.length());
//            LogUtils.logI("转化 path = "+s);
                list.add(s);
            }
        }
        // 默认选择
        if (list != null && list.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, list);
        }
        if (list != null && list.size() == Contants.IMAGENUMBER) {
            String s = String.format(aty.getString(R.string.theImgNumberLimit), Contants.IMAGENUMBER);

            SVProgressHUD.showInfoWithStatus(aty, s, SVProgressHUD.SVProgressHUDMaskType.Black);
            return;
        }

        aty.startActivityForResult(intent, Contants.REQUEST_IMAGE);

    }


    /*
   * Drawable → Bitmap
   */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /*
     * Bitmap → Drawable
     */
    @SuppressWarnings("deprecation")
    public static Drawable bitmap2Drawable(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        return new BitmapDrawable(bm);
    }

    private static DisplayImageOptions imageloaderOption() {
        DisplayImageOptions options
                = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_avatar) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_loading_image_fail)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_loading_image_fail)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
//                .decodingOptions(android.graphics.BitmapFactory.OptionsdecodingOptions)//设置图片的解码配置
//.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
//设置图片加入缓存前，对bitmap进行设置
//.preProcessor(BitmapProcessor preProcessor)
//                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
//                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成

        return options;
    }

    public static void loadIamge(Context ctx,String imageUrl,ImageView im){
        ImageLoader.getInstance().displayImage(NetworkUtils.getRealUrl(ctx,imageUrl),im,imageloaderOption());
    }
}
