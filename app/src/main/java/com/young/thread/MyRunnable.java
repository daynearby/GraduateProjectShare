/*
 * FileName:  MyRunnable.java
 * CopyRight:  Belong to  <XiaoMaGuo Technologies > own 
 * Description:  <description>
 * Modify By :  XiaoMaGuo ^_^ 
 * Modify Date:   2013-10-21
 * Follow Order No.:  <Follow Order No.>
 * Modify Order No.:  <Modify Order No.>
 * Modify Content:  <modify content >
 */
package com.young.thread;

import android.os.Handler;
import android.util.Log;

import com.young.utils.LogUtils;

/**
 * 线程 =- 运行
 */
public class MyRunnable implements Runnable {

    private boolean cancleTask = false;

    private boolean cancleException = false;

//    private Handler mHandler = null;
    private GotoRunnable gotoRunnable;

    public MyRunnable(GotoRunnable gotoRunnable) {
        this.gotoRunnable = gotoRunnable;
    }

    @Override
    public void run() {


        if (!cancleTask) {
//            LogUtils.logD("cancle = "+!cancleTask);

            gotoRunnable.running();
            LogUtils.logD("running");
        }

//        if (!cancleTask && mHandler != null) {
//
//        }


    }


    /**
     *
     */
    private void running() {
        try {
            // 做点有可能会出异常的事情！！！
            int prog = 0;
            if (!cancleTask && !cancleException) {
//                while (prog < 101) {
////                    if ((prog > 0 || prog == 0) && prog < 70)
////                    {
////                        SystemClock.sleep(100);
////                    }
////                    else
////                    {
////                        SystemClock.sleep(300);
////                    }
//                    if (!cancleTask) {
//                        mHandler.sendEmptyMessage(prog++);
//                        Log.i("KKK", "调用 prog++ = " + (prog));
//                    }
//
//                }


            }
        } catch (Exception e) {
            cancleException = true;
        }
    }


    public void setCancleTaskUnit(boolean cancleTask) {
        this.cancleTask = cancleTask;
        // mHandler.sendEmptyMessage(0);
    }


    /**
     * 回调函数
     */
    public interface GotoRunnable {
        void running();
    }

}
