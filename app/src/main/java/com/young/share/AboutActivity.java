package com.young.share;

import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.young.base.ItemActBarActivity;

/**
 * 关于
 * Created by Nearby Yang on 2015-11-13.
 */
public class AboutActivity extends ItemActBarActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void findviewbyid() {

    }

    @Override
    public void initData() {
        super.initData();
        setTvTitle(R.string.about);
        setBarItemVisible(true, false);
    }

    @Override
    public void bindData() {
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                back2super();
            }

            @Override
            public void rightClivk(View v) {

            }
        });
    }

    @Override
    public void handerMessage(Message msg) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            back2super();
        }


        return super.dispatchKeyEvent(event);
    }

    /**
     * 返回上一层
     */
    private void back2super() {
        mBackStartActivity(PersonalCenterActivity.class);
        this.finish();
    }

}
