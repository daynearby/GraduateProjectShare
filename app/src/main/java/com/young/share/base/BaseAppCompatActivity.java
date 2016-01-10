package com.young.share.base;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.young.share.annotation.Injector;
import com.young.share.config.ApplicationConfig;
import com.young.share.config.Contants;
import com.young.share.model.User;
import com.young.share.MainActivity;
import com.young.share.MessageCenterActivity;
import com.young.share.R;
import com.young.share.RankListActivity;
import com.young.share.RecordCommActivity;
import com.young.share.utils.ThreadUtils;

import cn.bmob.v3.BmobUser;

/**
 * 基类 v7 兼容activity
 * 有actionBar，并且有点击事件
 * 使用注释
 * <p/>
 * Created by Nearby Yang on 2015-10-08.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {


    public Activity mActivity;
    public Intent intents = new Intent();
    public IntentFilter myIntentFilter = new IntentFilter();
    public ApplicationConfig app;
    public User mUser;
    public ThreadUtils threadUtils;


    public final static String BUNDLE_BROADCAST = "sendBroadcast";

    private final static int TYPE_BACK = 0x1;
    private final static int TYPE_DEFUAL = 0x0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());
        Injector.inject(this);
        mUser = BmobUser.getCurrentUser(this, User.class);
        mActivity = this;
        app = ApplicationConfig.getInstance();
        threadUtils = app.getThreadInstance();
//        initActionBar();
        initData();
        findviewbyid();
        bindData();
    }

    /**
     * 获取根目录的布局
     */
    public View getRootView() {
        return LayoutInflater.from(this).inflate(getLayoutId(), null);
    }


    /**
     * 跳转界面，不带参数的
     *
     * @param clazz 要跳转的类，也就是要传递参数的类
     */
    public void mStartActivity(Class clazz) {


        mStartActivity(clazz, null, TYPE_DEFUAL, null);
    }

    /**
     * 跳转界面，带参数的
     *
     * @param clazz
     * @param bundle
     */
    public void mStartActivity(Class clazz, Bundle bundle) {


        mStartActivity(clazz, bundle, TYPE_DEFUAL, null);
    }

    /**
     * 跳转界面，带参数的,参数标识tag
     *
     * @param clazz
     * @param bundle
     * @param bundleTag
     */
    public void mStartActivity(Class clazz, Bundle bundle, String bundleTag) {

        mStartActivity(clazz, bundle, TYPE_DEFUAL, bundleTag);
    }


    /**
     * 返回上一级界面，不带参数
     *
     * @param clazz
     */
    public void mBackStartActivity(Class clazz) {
        mStartActivity(clazz, null, TYPE_BACK, null);
    }

    public void mBackStartActivity(String tagClazz) {
        Class clazz = MainActivity.class;

        switch (tagClazz) {

            case Contants.CLAZZ_MAINACTIVITY:
                clazz = MainActivity.class;
                break;
            case Contants.CLAZZ_PERSONAL_ACTIVITY:

                clazz = RecordCommActivity.class;
                break;
            case Contants.CLAZZ_MESSAGE_CENTER_ACTIVITY://消息中心
                clazz = MessageCenterActivity.class;

                break;

            case  Contants.CLAZZ_RANK_LIST_ACTIVITY:
                clazz =RankListActivity.class;

                break;
        }

        mStartActivity(clazz, null, TYPE_BACK, null);
    }

    /**
     * 跳转界面，带参数的
     *
     * @param clazz  要跳转的类，也就是要传递参数的类
     * @param bundle serializable
     */
    public void mStartActivity(Class clazz, Bundle bundle, int type, String bundleTag) {

        Intent intent = new Intent();
        intent.setClass(this, clazz);

        if (bundle != null) {
            if (TextUtils.isEmpty(bundleTag)) {
                intent.putExtras(bundle);
            } else {
                intent.putExtra(bundleTag, bundle);
            }
        }

        startActivity(intent);
        if (type == TYPE_BACK) {
            overridePendingTransition(R.animator.activity_slid_left_in, R.animator.activity_slid_right_out);
        } else {

            overridePendingTransition(R.animator.activity_slid_right_in, R.animator.activity_slid_left_out);
        }
    }

    /**
     * toast
     *
     * @param strId 文字id
     */
    public void mToast(int strId) {
        Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 点击事件的回调
     */
    public interface mActionBarOnClickListener {
        void onClick(View v);

    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handerMessage(msg);
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
           mBack();
        }


        return super.dispatchKeyEvent(event);
    }


    //主界面layout的id
    public abstract int getLayoutId();

    //获取控件实例
    public abstract void findviewbyid();

    //初始化数据
    public abstract void initData();

    //绑定数据到空间中
    public abstract void bindData();

    //初始化actionBar
//    public abstract void initActionBar();

    public abstract void handerMessage(Message msg);

    public abstract void mBack();
    /**
     * 简化findviewbyid
     *
     * @param viewID
     * @param <T>
     * @return
     */
    public <T> T $(int viewID) {
        return (T) findViewById(viewID);
    }


}