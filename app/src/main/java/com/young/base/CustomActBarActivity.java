package com.young.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.young.share.R;
import com.young.utils.LogUtils;
import com.young.utils.XmlUtils;
import com.young.views.PopupWinListView;

/**
 * 基类
 * 有actionBar，并且有点击事件
 * 使用注释
 * <p/>
 * Created by Nearby Yang on 2015-10-08.
 */
public abstract class CustomActBarActivity extends BaseAppCompatActivity {

    private ActionBar mActionbar;

    private int title = R.string.title;
    private boolean showCity = false;
    private boolean showTag = false;

    private TextView title_tv;
    private TextView tag_tv;
    private TextView city_tv;
    private itemClickResult itemResult;


    @Override
    public void initData() {
        initActionBar();
    }

    public TextView getTitle_tv() {
        return title_tv;
    }

    public View getCustomView() {
        return mActionbar.getCustomView();
    }

    /**
     * 初始化主界面actionbar
     */
    public void initActionBar() {

        mActionbar = getSupportActionBar();
        assert mActionbar != null;
        mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionbar.setCustomView(R.layout.actionbar_main);

        setTranslucentStatus();

        title_tv = (TextView) mActionbar.getCustomView().findViewById(R.id.tv_actionbar_titile);
        tag_tv = (TextView) mActionbar.getCustomView().findViewById(R.id.tv_actionbar_tag);
        city_tv = (TextView) mActionbar.getCustomView().findViewById(R.id.tv_actionbar_city);
        tag_tv.setOnClickListener(new tvOnclick());
        city_tv.setOnClickListener(new tvOnclick());
    }

    /**
     * 沉浸式
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
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
     * 设置标题显示状态：显示与隐藏
     *
     * @param showCity
     * @param showTag
     */
    public void setBarVisibility(boolean showCity, boolean showTag) {

        this.showCity = showCity;
        this.showTag = showTag;
        refreshActionBar();
    }

    /**
     * 更新显示状态
     */
    private void refreshActionBar() {

        if (title != 0) {
            title_tv.setText(title);
        }

        if (showCity) {
            city_tv.setVisibility(View.VISIBLE);
        } else {
            city_tv.setVisibility(View.GONE);
        }

        if (showTag) {
            tag_tv.setVisibility(View.VISIBLE);
        } else {
            tag_tv.setVisibility(View.GONE);
        }
    }

    /**
     * 标题
     *
     * @param title
     */
    public void settitle(int title) {
        this.title = title;
        refreshActionBar();
    }

    /**
     * 设置标签的数据
     *
     * @param tagStr 标签
     */
    public void setTag(String tagStr) {

        tag_tv.setText(tagStr);

    }

    /**
     * 设置城市数据源
     *
     * @param cityStr 城市
     */
    public void setCity(String cityStr) {

        city_tv.setText(cityStr);
    }


    public TextView getTag_tv() {
        return tag_tv;
    }

    public TextView getCity_tv() {
        return city_tv;
    }


    public void setItemResult(itemClickResult itemResult) {
        this.itemResult = itemResult;
    }

    private class tvOnclick implements View.OnClickListener {

        private PopupWinListView popupWindows;
        private int isTag = 0;


        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.tv_actionbar_tag://标签
                    popupWindows = new PopupWinListView(CustomActBarActivity.this, XmlUtils.getSelectTag(CustomActBarActivity.this),false);

                    isTag = 1;

                    break;

                case R.id.tv_actionbar_city://城市

                    popupWindows = new PopupWinListView(CustomActBarActivity.this, XmlUtils.getSelectCities(CustomActBarActivity.this),false);
                    isTag = 2;

                    break;

            }


            //初始化对应的点击事件的监听
            popupWindows.setItemClick(new PopupWinListView.onItemClick() {
                @Override
                public void onClick(View view, String s, int position, long id) {

                    switch (isTag) {
                        case 1:
                            getTag_tv().setText(s);
                            break;
                        case 2:
                            getCity_tv().setText(s);
                            break;
                        default:
                            LogUtils.logI(getClass().getName(), "actionbar 点击未设置");
                            break;
                    }
                    itemResult.result(view, s,position);

                    isTag = 0;

                }
            });


            popupWindows.onShow(v);


        }


    }

    /**
     * 点击之后item之后的回调
     */
    public interface itemClickResult{
        void result(View view, String s,int position);
    }

}
