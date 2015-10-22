package com.young.network;

import android.content.Context;

import com.young.myCallback.GotoAsyncFunction;
import com.young.utils.LogUtils;

import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
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
     * @param ctx
     * @param params  JSONObject 请求参数
     * @param menum 方法名
     */
    public static void AsyncFunction(Context ctx, JSONObject params, aceName menum,final GotoAsyncFunction gotoListener) {
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
                        gotoListener.onFailure(code,msg);
                        LogUtils.logE("访问云端usertest方法失败:" + msg);
                    }
                });
    }


}
