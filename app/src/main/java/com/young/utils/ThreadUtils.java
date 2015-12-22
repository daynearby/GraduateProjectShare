package com.young.utils;

import com.young.thread.MyRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 先获取对象，初始化
 * 执行任务先添加任务，再启动
 * 最后在activity停止的时候应该停止该线程，释放资源
 * <p>
 * Created by Nearby Yang on 2015-11-21.
 */
public class ThreadUtils {

    private  ExecutorService mES;
    private ConcurrentLinkedQueue<MyRunnable> taskQueue = null;//任务队列
//    private static final int count = Runtime.getRuntime().availableProcessors() * 3 + 2;
    /**
     * 正在等待执行或已经完成的任务队列
     * 备注：Future类，一个用于存储异步任务执行的结果，比如：判断是否取消、是否可以取消、是否正在执行、是否已经完成等
     */
    private ConcurrentMap<Future, MyRunnable> taskMap = null;
    // 在此类中使用同步锁时使用如下lock对象即可，官方推荐的，不推荐直接使用MyRunnableActivity.this类型的,可以详细读一下/framework/app下面的随便一个项目
    private final Object lock = new Object();
    // 唤醒标志，是否唤醒线程池工作
    private boolean isNotify = true;

    //线程池是否处于运行状态(即:是否被释放!)
    private boolean isRuning = true;

    private static final int COUNT = 4;//并发线程数量

    public ThreadUtils() {

        getInstance();
        init();

    }

    /**
     * 获取线程池对象
     *
     * @return
     */
    public ExecutorService getInstance() {
        if (mES == null) {
            synchronized (ThreadUtils.class) {
                if (mES == null) {
                    mES = Executors.newFixedThreadPool(COUNT);
                }
            }
        }

        return mES;
    }

    /**
     * 初始化
     */
    public void init() {


        if (taskQueue == null) {
            taskQueue = new ConcurrentLinkedQueue<>();
        }
        if (taskMap == null) {
            taskMap = new ConcurrentHashMap<>();
        }

    }

    /**
     * 启动线程，获取线程池
     */
    public void start() {

        getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (isRuning) {

                    LogUtils.logD("thread star task");

                    MyRunnable myRunnable = null;

                    synchronized (lock) {
                        // 从线程队列中取出一个Runnable对象来执行，如果此队列为空，则调用poll()方法会返回null
                        myRunnable = taskQueue.poll();

                        if (myRunnable == null) {
                            isNotify = true;
                        }
                    }

                    if (myRunnable != null) {
                        taskMap.put(mES.submit(myRunnable), myRunnable);
                    }
                }

            }
        });


    }

    /**
     * 停止所有任务
     */
    public void stop() {

        for (MyRunnable runnable : taskMap.values()) {
            runnable.setCancleTaskUnit(true);
        }

    }

    /**
     * 添加任务到线程池中
     *
     * @param mr MyRunnable对象
     */
    public void addTask(final MyRunnable mr) {

//        mHandler.sendEmptyMessage(0);

        if (mES == null) {
            getInstance();
            notifyWork();
        }

        init();

        mES.execute(new Runnable() {

            @Override
            public void run() {
                /**
                 * 插入一个Runnable到任务队列中 这个地方解释一下,offer跟add方法,试了下,效果都一样,没区别,官方的解释如下:
                 * 1 offer : Inserts the specified
                 * element at the tail of this queue. As the queue is unbounded, this method will never return
                 * {@code false}. 2 add: Inserts the specified element at the tail of this queue. As the queue is
                 * unbounded, this method will never throw {@link IllegalStateException} or return {@code false}.
                 *
                 *
                 * */
                taskQueue.offer(mr);

                // taskQueue.add(mr);
                notifyWork();
                LogUtils.logD("thread add task ");
            }
        });

    }

    /**
     * 释放线程占用的全部资源
     */
    public void release() {

        /** 将ProgressBar进度置为0 */
//        mHandler.sendEmptyMessage(0);
        isRuning = false;

        for (Map.Entry<Future, MyRunnable> entry : taskMap.entrySet()) {
            Future result = entry.getKey();
            result.cancel(true);
            taskMap.remove(result);
        }
        if (null != mES) {
            mES.shutdown();
        }

        mES = null;
        taskMap = null;
        taskQueue = null;

    }

    private void notifyWork() {
        synchronized (lock) {
            if (isNotify) {
                lock.notifyAll();
                isNotify = !isNotify;
            }
        }
    }

    /**
     * 添加并立即启动任务
     *
     * @param mr
     */
    public void startTask(MyRunnable mr) {
        addTask(mr);
        start();
    }

    /**
     * 停止所有任务
     */
    public void stopAllTask() {
        stop();
        release();
    }

}
