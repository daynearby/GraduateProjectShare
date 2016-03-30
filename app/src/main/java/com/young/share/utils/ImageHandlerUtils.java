package com.young.share.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.young.share.config.Contants;
import com.young.share.R;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 图片处理工具类
 * <p>
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
        if (list.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, list);
        }
        if (list.size() == Contants.IMAGENUMBER) {
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

    /**
     * 默认显示的图片为灰色
     *
     * @return
     */
    public static DisplayImageOptions imageloaderOption() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.gray_lighter) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_iamge_uri_empty)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_loading_image_fail)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc()//设置下载的图片是否缓存在SD卡中
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
    }



    /**
     * @param ctx
     * @param imageUri
     * @param im
     * @param isLocation 是否是本地资源，true --> 本地资源
     */
    public static void loadIamge(Context ctx, String imageUri, ImageView im, boolean isLocation) {

        ImageLoader.getInstance().displayImage(NetworkUtils.getRealUrl(ctx, imageUri, isLocation), im);
    }
    /**
     * 加载在线资源
     * @param ctx
     * @param imageUri
     * @param im
     */
    public static void loadIamge(Context ctx, String imageUri, ImageView im) {

        ImageLoader.getInstance().displayImage(NetworkUtils.getRealUrl(ctx, imageUri, false), im);
    }

    /**
     * 缩略图
     * @param ctx
     * @param imageUri
     * @param im
     */
    public static void loadIamgeThumbnail(Context ctx, String imageUri, ImageView im) {
        ImageLoader.getInstance().displayImage(NetworkUtils.getRealUrl(ctx, imageUri), im);
    }

    /**
     * @param ctx
     * @param imageUri
     * @param im
     * @param isLocation 是否是本地资源，true --> 本地资源
     */
    public static void loadIamge2(Context ctx, String imageUri, ImageView im, boolean isLocation) {

        ImageLoader.getInstance().displayImage(imageUri, im);
    }
    /**
     * 缩略图
     * @param ctx
     * @param imageUri
     * @param im
     */
    public static void loadIamgeThumbnail2(Context ctx, String imageUri, ImageView im) {
        ImageLoader.getInstance().displayImage(imageUri, im);
    }

    /**
     * image loader 加载图片
     *
     * @param uri
     * @param imageView
     */
    public static void loadImage2(String uri,ImageView imageView){

        ImageLoader.getInstance().displayImage(uri,imageView);
    }


    /**
     * 具有回调，显示状态
     * @param ctx
     * @param imageUri
     * @param im
     */
    public static void loadIamgeWithState(final Context ctx, String imageUri, ImageView im) {


        ImageLoader.getInstance().displayImage(NetworkUtils.getRealUrl(ctx, imageUri, false), im,  new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                SVProgressHUD.showWithStatus(ctx, ctx.getString(R.string.tips_loading));
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                SVProgressHUD.showErrorWithStatus(ctx, ctx.getString(R.string.tips_loading_faile));
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                ((ImageView) view).setImageBitmap(bitmap);
                SVProgressHUD.dismiss(ctx);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                SVProgressHUD.showErrorWithStatus(ctx, ctx.getString(R.string.tips_loading_cancel));

            }

        });
    }


    /**
     * 使用此加载框架的imageloader加载的图片，设置了缓存后，下次使用，手动从缓存取出来用，
     * 这时特别要注意，不能直接使用：imageLoader.getMemoryCache().get(uri)来获取，因为在加载过程中，
     * key是经过运算的，而不单单是uri,而是：
     * String memoryCacheKey = MemoryCacheUtil.generateKey(uri, targetSize);
     *
     * @return
     */
    public static Bitmap getBitmapFromCache(String uri,ImageLoader imageLoader){//这里的uri一般就是图片网址
        List<String> memCacheKeyNameList = MemoryCacheUtils.findCacheKeysForImageUri(uri, imageLoader.getMemoryCache());
        if(memCacheKeyNameList != null && memCacheKeyNameList.size() > 0){
            for(String each:memCacheKeyNameList){
            }
            return imageLoader.getMemoryCache().get(memCacheKeyNameList.get(0));
        }

        return null;
    }

}
