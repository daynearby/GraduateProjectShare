package com.young.base;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.young.annotation.Injector;
import com.young.model.User;
import com.young.share.R;

import cn.bmob.v3.BmobUser;

/**
 * 基类 v7 兼容activity
 * 有actionBar，并且有点击事件
 * 使用注释
 * <p>
 * Created by Nearby Yang on 2015-10-08.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {


    private int title = R.string.title;
    private boolean showCity = false;
    private boolean showTag = false;
    public Activity mActivity;

    private TextView title_tv;

    public Intent intents = new Intent();
    public User mUser;

    public final static String BUNDLE_TAG = "Serializable_Data";
    public final static String BUNDLE_BROADCAST = "sendBroadcast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        Injector.inject(this);
        mUser = BmobUser.getCurrentUser(this, User.class);
        mActivity = BaseAppCompatActivity.this;
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


        mStartActivity(clazz, null);
    }

    /**
     * 跳转界面，带参数的
     *
     * @param clazz  要跳转的类，也就是要传递参数的类
     * @param bundle serializable
     */
    public void mStartActivity(Class clazz, Bundle bundle) {

        Intent intent = new Intent();
        intent.setClass(this, clazz);
        if (bundle != null) {
            intent.putExtra(BUNDLE_TAG, bundle);
        }

        startActivity(intent);
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
