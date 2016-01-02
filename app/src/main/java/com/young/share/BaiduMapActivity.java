package com.young.share;

import android.os.Message;

import com.baidu.mapapi.map.MapView;
import com.young.base.ItemActBarActivity;

/**
 * 百度地图
 * 显示地理位置
 * <p>
 * Created by Nearby Yang on 2016-01-02.
 */
public class BaiduMapActivity extends ItemActBarActivity {

    private MapView mMapView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_baidu_map;
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void findviewbyid() {
        mMapView = $(R.id.cusview_bmapView_map);
    }

    @Override
    public void bindData() {

    }

    @Override
    public void handerMessage(Message msg) {

    }

    @Override
    public void mBack() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
