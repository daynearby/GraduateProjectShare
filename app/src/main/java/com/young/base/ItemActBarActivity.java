package com.young.base;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.young.share.R;

/**
 * 基类
 * 有actionBar，并且有点击事件
 * 使用注释
 * <p/>
 * Created by Nearby Yang on 2015-10-08.
 */
public abstract class ItemActBarActivity extends BaseAppCompatActivity {

    private ActionBar mActionbar;

    private TextView title_tv;
    private ImageView backto_im;
    private TextView barRightItem_tv;

    private BarItemOnClick barItemOnClick;

    @Override
    public void initData() {
        initActionBar();
    }


    /**
     * 初始化主界面actionbar
     */
    public void initActionBar() {

        mActionbar = getSupportActionBar();
        mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionbar.setCustomView(R.layout.actionbar_bar_item);

        setTranslucentStatus();

        title_tv = (TextView) mActionbar.getCustomView().findViewById(R.id.tv_actionbar_item_title);
        backto_im = (ImageView) mActionbar.getCustomView().findViewById(R.id.im_actionbar_item_left);
        barRightItem_tv = (TextView) mActionbar.getCustomView().findViewById(R.id.im_actionbar_item_right);

        backto_im.setOnClickListener(new mItemOnClick());
        barRightItem_tv.setOnClickListener(new mItemOnClick());
    }

    /**
     * 沉浸式
     */
    private void setTranslucentStatus() {
        boolean on = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            on = true;
        }

        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }

        win.setAttributes(winParams);


        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
//设置沉浸的颜色
        tintManager.setStatusBarTintResource(R.color.theme_puple);


    }

    /**
     * 设置上面两个按钮是否显示
     * true-->显示 false -->不显示
     *
     * @param left  左边
     * @param right 右边
     */
    public void setBarItemVisible(boolean left, boolean right) {
        if (left) {
            backto_im.setVisibility(View.VISIBLE);
        } else {
            backto_im.setVisibility(View.INVISIBLE);
        }
        if (right) {
            barRightItem_tv.setVisibility(View.VISIBLE);
        } else {
            barRightItem_tv.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 设置标题
     *
     * @param resId
     */
    public void setTvTitle(int resId) {
        title_tv.setText(resId);
    }

    /**
     * 右边按钮显示文字
     *
     * @param resId
     */
    public void setTvRight(int resId) {
        barRightItem_tv.setText(resId);
    }

    /**
     * 点击事件回调
     *
     * @param barItemOnClick
     */
    public void setItemListener(BarItemOnClick barItemOnClick) {
        this.barItemOnClick = barItemOnClick;

    }

    private class mItemOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.im_actionbar_item_left:

                    if (barItemOnClick != null) {
                        barItemOnClick.leftClick(v);
                    }

                    break;

                case R.id.im_actionbar_item_right:
                    if (barItemOnClick != null) {
                        barItemOnClick.rightClivk(v);
                    }
                    break;

            }
        }
    }

    /**
     * 两个item的点击事件回调
     */
    public interface BarItemOnClick {
        void leftClick(View v);

        void rightClivk(View v);
    }
}
