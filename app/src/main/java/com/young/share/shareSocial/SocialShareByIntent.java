package com.young.share.shareSocial;

import android.content.Context;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.network.NetworkReuqest;
import com.young.share.utils.LogUtils;

import java.util.List;

/**
 * 下载图片，进行分享
 * Created by Nearby Yang on 2016-04-18.
 */
public class SocialShareByIntent {


    /**
     * 下载图片并且分享
     */
    public static void downloadImagesAndShare(final Context context, List<String> list) {
        for (String url : list) {
            LogUtils.e("share images = " + url);
        }


        SVProgressHUD.showWithStatus(context, "正在下载图片");

        NetworkReuqest.call(context, list, new NetworkReuqest.JSONRespond() {
            @Override
            public void onSuccess(List<String> response) {
                SVProgressHUD.dismiss(context);
                SocialShareManager.shareImagesByIntent(context, response);

            }

            @Override
            public void onFailure(String erroMsg) {
                SVProgressHUD.showErrorWithStatus(context, "下载图片失败，请稍后再试");
            }
        });


    }

    /**
     * 下载单张图片
     * 通过意图进行分享
     */
    public static void downloadImageAndShare(final Context context, String imageUrl) {
        LogUtils.e("share image = " + imageUrl);
        SVProgressHUD.showWithStatus(context, "正在下载图片");

        NetworkReuqest.call(context, imageUrl, new NetworkReuqest.JSONRespond() {
            @Override
            public void onSuccess(List<String> response) {
                SVProgressHUD.dismiss(context);
                SocialShareManager.shareImagesByIntent(context, response);

            }

            @Override
            public void onFailure(String erroMsg) {
                SVProgressHUD.showErrorWithStatus(context, "下载图片失败，请稍后再试");
            }
        });


    }


}
