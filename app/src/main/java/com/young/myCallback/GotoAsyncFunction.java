package com.young.myCallback;

/**
 *
 * 云端方法回调函数
 *
 *
 * Created by yangfujing on 15/10/10.
 */
public interface GotoAsyncFunction {
     void onSuccess(Object object);
     void onFailure(int code, String msg);


}
