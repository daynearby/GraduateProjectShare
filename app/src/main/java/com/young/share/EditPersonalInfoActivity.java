package com.young.share;

import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.young.base.ItemActBarActivity;

/**
 * Created by Nearby Yang on 2015-11-13.
 */
public class EditPersonalInfoActivity extends ItemActBarActivity implements View.OnClickListener {
    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_personal_info;
    }

    @Override
    public void initData() {
        super.initData();

        setTvTitle(R.string.edit_personal_info);
        setBarItemVisible(true, false);
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
    public void findviewbyid() {

    }

    @Override
    public void bindData() {

    }

    @Override
    public void handerMessage(Message msg) {

    }

    private void back2super() {
        mBackStartActivity(PersonalCenterActivity.class);
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            mBackStartActivity(MainActivity.class);
        }


        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {

    }
}
