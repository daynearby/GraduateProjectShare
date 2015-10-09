package com.young.utils;

import android.util.Log;
import android.widget.Toast;

import com.young.config.ApplicationConfig;

/**
 * Created by Nearby Yang on 2015-10-09.
 */
public class LogUtils {
    private final static String Tag = " ++ share ++ ";
    private final static String ms = "msg -> ";

    /**
     * logcat Error
     * @param msg 信息，可以多个参数，最多支持两个
     */
    public static void logE(String ...msg) {
        if (ApplicationConfig.isDebug) {
            if (msg.length > 1 ){
                Log.e(Tag+msg[0], msg[1]);
            }else{
                Log.e(Tag, msg[0]);
            }
        }

    }
    /**
     * logcat debug
     * @param msg 信息，可以多个参数，最多支持两个
     */
    public static void logD(String ...msg) {

        if (ApplicationConfig.isDebug) {
            if (msg.length > 1 ){
                Log.d(Tag + msg[0], msg[1]);
            }else{
                Log.d(Tag, msg[0]);
            }

        }
    }

    /**
     * logcat info
     * @param msg 信息，可以多个参数，最多支持两个
     */
    public static void logI(String ...msg) {

        if (ApplicationConfig.isDebug) {
            if (msg.length > 1 ){
                Log.i(Tag + msg[0], msg[1]);
            }else{
                Log.i(Tag, msg[0]);
            }

        }
    }

    /**
     * toast 时长：Toast.LENGTH_LONG
     *
     * @param msg 需要显示的信息
     */
    public static void ta(String msg){
        if (ApplicationConfig.isDebug){
            Toast.makeText(ApplicationConfig.getInstance(),ms+msg,Toast.LENGTH_LONG).show();
        }
    }

}
