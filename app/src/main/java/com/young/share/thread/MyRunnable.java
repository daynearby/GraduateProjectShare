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
public abstract class MyRunnable implements Runnable {


    public boolean cancelTask = false;
    private String tag;//作为判断标识

    public MyRunnable() {

    }


    @Override
    public void run() {
        LogUtils.d(" running is cancel " + cancelTask);
        if (!cancelTask) {
            LogUtils.d("running");
            runnabele();
        }

    }

    /**
     * 停止运行
     *
     * @param cancelTask
     */
    public void setCancleTaskUnit(boolean cancelTask) {
        this.cancelTask = cancelTask;
    }


    public abstract void runnabele();

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
