package com.young.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * 有actionBar，并且有点击事件
 * <p/>
 * Created by Nearby Yang on 2015-10-08.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();

    }

    /**
     * 初始化主界面actionbar
     */
    private void initActionBar() {


    }


}
