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
package com.young.share.thread;

import com.young.share.utils.LogUtils;

/**
 * 线程
 * 运行
 */
public class MyRunnable implements Runnable {

    public  boolean cancelTask = false;


    private GotoRunnable gotoRunnable;

    public MyRunnable() {
    }

    public MyRunnable(GotoRunnable gotoRunnable) {
        this.gotoRunnable = gotoRunnable;
    }


    @Override
    public void run() {
        LogUtils.d(" running is cancel " +cancelTask);
        if (!cancelTask) {
            LogUtils.d("running");
            if (gotoRunnable != null)
                gotoRunnable.running();

        }

    }


    public void setCancleTaskUnit(boolean cancelTask) {
        this.cancelTask = cancelTask;
    }


    /**
     * 回调函数
     */
    public interface GotoRunnable {
        void running();
    }

}
