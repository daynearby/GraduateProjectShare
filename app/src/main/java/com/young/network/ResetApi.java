package com.young.network;

import android.content.Context;
import android.os.Message;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.young.config.Contants;
import com.young.myCallback.GoToUploadImages;
import com.young.myCallback.GotoAsyncFunction;
import com.young.utils.LogUtils;

import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.CloudCodeListener;

/**
 * 网络请求 基类
 * Created by Nearby Yang on 2015-10-17.
 */
public class ResetApi {

    // TODO: 2015-10-17 volley请求

    //找回密码
    public static final String findPwd = "FindPwd";

    public enum aceName {
        FIND_PWD

    }

    /**
     * 云端代码
     *
     * @param ctx
     * @param params JSONObject 请求参数
     * @param menum  方法名
     */
    public static void AsyncFunction(Context ctx, JSONObject params, aceName menum, final GotoAsyncFunction gotoListener) {
        String funcationName = "";
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();

        switch (menum) {
            case FIND_PWD:
                funcationName = findPwd;
                break;

        }

//第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        ace.callEndpoint(ctx, funcationName, params,
                new CloudCodeListener() {
                    @Override
                    public void onSuccess(Object object) {
                        gotoListener.onSuccess(object);

                        LogUtils.logE("云端usertest方法返回:" + object.toString());
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        gotoListener.onFailure(code, msg);
                        LogUtils.logE("访问云端usertest方法失败:" + msg);
                    }
                });
    }


    /**
     * Bmob批量上传文件
     *先修正文件地址，再进行操作
     * @param file 文件地址，带file：//的
     * @param context
     */
    public static void UploadFiles( Context context,String[] file, final GoToUploadImages listener) {

        String[] files = new String[file.length];

        for (int i = 0; i < file.length; i++) {
            files[i] = file[i].substring(Contants.FILE_HEAD.length(), file[i].length());
        }

        BmobProFile.getInstance(context).uploadBatch(files, new UploadBatchListener() {

            @Override
            public void onSuccess(boolean isFinish, String[] fileNames, String[] urls, BmobFile[] files) {
                if (listener != null) {
                    listener.Result(isFinish,urls);
                }

                // isFinish ：批量上传是否完成
                // fileNames：文件名数组
                // urls        : url：文件地址数组
                // files     : BmobFile文件数组，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
//                注：若上传的是图片，url(s)并不能直接在浏览器查看（会出现404错误），需要经过`URL签名`得到真正的可访问的URL地址,当然，`V3.4.1`版本可直接从BmobFile中获得可访问的文件地址。
                LogUtils.logI("uploadBatch isFinish = "+isFinish+" imgUrl = " + urls.toString());

            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                // curIndex    :表示当前第几个文件正在上传
                // curPercent  :表示当前上传文件的进度值（百分比）
                // total       :表示总的上传文件数
                // totalPercent:表示总的上传进度（百分比）
                LogUtils.logI("uploadBatch", "onProgress :" + curIndex + "---" + curPercent + "---" + total + "----" + totalPercent);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                if (listener != null) {
                    listener.onError(statuscode, errormsg);
                }
                LogUtils.logI("uploadBatch", "批量上传出错：" + statuscode + "--" + errormsg);
            }
        });
    }




}
