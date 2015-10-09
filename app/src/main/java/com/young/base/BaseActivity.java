package com.young.base;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

    private String title = "标题";
    private boolean showCity = false;
    private boolean showTag = false;

    private List<String> tagList;//标签数据源
    private List<String> cityList;//城市数据源
    private TextView title_tv;

    private Spinner spinnerCity;
    private Spinner spinnerTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();

        initData();
        findviewbyid();
        bindData();
    }

    /**
     * 初始化主界面actionbar
     */
    private void initActionBar() {
        LayoutInflater mlayoutInflater = LayoutInflater.from(this);
        View view = mlayoutInflater.inflate(R.layout.actionbar_main, null);

        spinnerCity = (Spinner) view.findViewById(R.id.sp_actionbar_city);
        spinnerTag = (Spinner) view.findViewById(R.id.sp_actionbar_tag);
        title_tv = (TextView) view.findViewById(R.id.tv_actionbar_titile);

        mActionbar = getActionBar();
        mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionbar.setCustomView(view);
        refreshActionBar();


        mActionbar.getCustomView().setOnClickListener(new mOnclickListener());

    }


    /**
     * 设置标题显示状态
     *
     * @param showCity
     * @param showTag
     */
    public void setTitle(boolean showCity, boolean showTag) {

        this.showCity = showCity;
        this.showTag = showTag;

    }

    /**
     * 更新显示状态
     */
    private void refreshActionBar() {

        if (title != null) {
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
    public void setTitle(String title) {
        this.title = title;
        refreshActionBar();
    }

    /**
     * 设置标签的数据
     *
     * @param tagList
     */
    public void setTag(List<String> tagList) {
        this.tagList = tagList;
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
        this.cityList = cityList;
        //适配器
        ArrayAdapter arr_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagList);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerCity.setAdapter(arr_adapter);

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
     * 点击事件的回调
     */
    public interface mActionBarOnClickListener {
        void onClick(View v);
    }

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
