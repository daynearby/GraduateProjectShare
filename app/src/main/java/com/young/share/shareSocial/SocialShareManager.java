package com.young.share.shareSocial;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.young.share.R;
import com.young.share.config.ApplicationConfig;
import com.young.share.config.Contants;

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

        ShareAction shareAction = new ShareAction((Activity) context);

        shareAction.setDisplayList(Contants.displaylist)
                .withText(!TextUtils.isEmpty(content) ? content : "")
                .withTitle(context.getString(R.string.app_name))
//                .withTargetUrl("http://www.baidu.com")
//                .withMedia(image)
                .setListenerList(umShareListener, umShareListener)
                .setShareboardclickCallback(new shareBoardlistener(context, content, imageUrl));

        if (!TextUtils.isEmpty(imageUrl)) {
            UMImage image = new UMImage(context, imageUrl);
            shareAction.withMedia(image);
        }

        shareAction.open();

    }


    private static class shareBoardlistener implements ShareBoardlistener {
        private Context context;
        private String content;
        private String imageUrl;

        public shareBoardlistener(Context context, String content, String imageUrl) {
            this.context = context;
            this.content = content;
            this.imageUrl = imageUrl;

        }

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {

            ShareAction shareAction = new ShareAction((Activity) context);
            shareAction.setPlatform(share_media).setCallback(umShareListener)
                    .withTitle(context.getString(R.string.app_name))
                    .withText(!TextUtils.isEmpty(content) ? content : "");

            if (!TextUtils.isEmpty(imageUrl)) {
                UMImage image = new UMImage(context, imageUrl);
                shareAction.withMedia(image);
            }

            shareAction.share();
        }


    }

    ;


    /**
     *
     */
    private static UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
//            toast(share_media + "分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            toast("分享失败 " + throwable.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
//            toast(share_media + "分享取消");
        }
    };

    /**
     * toast
     *
     * @param msg
     */
    private static void toast(String msg) {
        Toast.makeText(ApplicationConfig.getContext(), msg, Toast.LENGTH_LONG).show();

    }
}
