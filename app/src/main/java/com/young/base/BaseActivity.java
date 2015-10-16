package com.young.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.young.annotation.Injector;
import com.young.share.R;
import com.young.utils.LogUtils;

import java.util.List;

/**
 * 基类
 * 有actionBar，并且有点击事件
 * 使用注释
 * <p>
 * Created by Nearby Yang on 2015-10-08.
 */
public abstract class BaseActivity extends Activity {

    private ActionBar mActionbar;
    private mActionBarOnClickListener mActionBarOnClickListener;

    private int title = R.string.title;
    private boolean showCity = false;
    private boolean showTag = false;

    private List<String> tagList;//标签数据源
    private List<String> cityList;//城市数据源
    private TextView title_tv;

    private Spinner spinnerCity;
    private Spinner spinnerTag;

    public final static String BUNDLE_TAG = "Serializable_Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        Injector.inject(BaseActivity.this);
        initActionBar();
        initData();
        findviewbyid();
        bindData();
    }

    /**
     * 初始化主界面actionbar
     */
    private void initActionBar() {

        mActionbar = getActionBar();
        mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionbar.setCustomView(R.layout.actionbar_main);

        spinnerCity = (Spinner) mActionbar.getCustomView().findViewById(R.id.sp_actionbar_city);
        spinnerTag = (Spinner) mActionbar.getCustomView().findViewById(R.id.sp_actionbar_tag);
        title_tv = (TextView) mActionbar.getCustomView().findViewById(R.id.tv_actionbar_titile);


    }


    /**
     * 设置标题显示状态
     *
     * @param showCity
     * @param showTag
     */
    public void setBarVisibility(boolean showCity, boolean showTag) {

        this.showCity = showCity;
        this.showTag = showTag;

    }

    /**
     * 更新显示状态
     */
    private void refreshActionBar() {

        if (title != 0) {
            title_tv.setText(title);
        }

        if (showCity) {
            spinnerCity.setVisibility(View.VISIBLE);
        } else {
            spinnerCity.setVisibility(View.GONE);
        }

        if (showTag) {
            spinnerTag.setVisibility(View.VISIBLE);
        } else {
            spinnerTag.setVisibility(View.GONE);
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
     * @param tagList
     */
    public void setTag(List<String> tagList) {

        //适配器
        ArrayAdapter arr_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagList);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerTag.setAdapter(arr_adapter);

        refreshActionBar();
    }

    /**
     * 设置城市数据源
     *
     * @param cityList
     */
    public void setCity(List<String> cityList) {

        //适配器
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerCity.setAdapter(adapter);

        refreshActionBar();
    }

    /**
     * 点击事件回调对象
     *
     * @param mActionBarOnClickListener
     */
    public void setOnClickListener(mActionBarOnClickListener mActionBarOnClickListener) {
        this.mActionBarOnClickListener = mActionBarOnClickListener;
    }

    private class mOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (mActionBarOnClickListener != null) {
                mActionBarOnClickListener.onClick(v);
            } else {
                LogUtils.logE(getPackageName(), "没有设置点击回调");
            }


        }
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
     * @param clazz 要跳转的类，也就是要传递参数的类
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
     * 点击事件的回调
     */
    public interface mActionBarOnClickListener {
        void onClick(View v);
    }

    //主界面layout的id
    public abstract int getLayoutId();

    //获取控件实例
    public abstract void findviewbyid();

    //初始化数据
    public abstract void initData();

    //绑定数据到空间中
    public abstract void bindData();

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
