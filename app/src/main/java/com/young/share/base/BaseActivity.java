package com.young.share.base;

import android.app.Activity;
import android.os.Bundle;

import com.young.share.annotation.Injector;

/**
 * Activity 的基类
 *
 * Created by Nearby Yang on 2016-02-19.
 */
public abstract class BaseActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        Injector.inject(this);
        initData();
        setupEvent();

    }

    protected abstract void initData();

    protected abstract int getLayout();


    protected abstract void setupEvent();
}
