package com.young.share;

import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import com.young.base.BaseAppCompatActivity;

/**
 * 个人中心
 *
 * Created by Nearby Yang on 2015-10-22.
 */
public class PersonalCenterActivity extends BaseAppCompatActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void findviewbyid() {
        initToolbar();
    }

    @Override
    public void initData() {

    }

    @Override
    public void bindData() {

    }

    @Override
    public void handerMessage(Message msg) {

    }

    /**
     * toolbar 初始化
     */
    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
    }
}
