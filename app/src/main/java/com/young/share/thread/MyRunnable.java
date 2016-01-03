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

    private boolean cancleTask = false;


    private GotoRunnable gotoRunnable;

    public MyRunnable(GotoRunnable gotoRunnable) {
        this.gotoRunnable = gotoRunnable;
    }

    @Override
    public void run() {

        if (!cancleTask) {
            LogUtils.logD("running");
            gotoRunnable.running();

        }


    }


    public void setCancleTaskUnit(boolean cancleTask) {
        this.cancleTask = cancleTask;
    }


    /**
     * 回调函数
     */
    public interface GotoRunnable {
        void running();
    }

}
