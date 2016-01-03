package com.young.share;

import android.os.Message;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.young.share.base.ItemActBarActivity;
import com.young.share.config.Contants;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 百度地图
 * 显示地理位置
 * <p/>
 * Created by Nearby Yang on 2016-01-02.
 */
public class BaiduMapActivity extends ItemActBarActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BmobGeoPoint geoPoint;

    @Override
    public int getLayoutId() {
        return R.layout.activity_baidu_map;
    }

    @Override
    public void initData() {
        super.initData();
        geoPoint = (BmobGeoPoint) getIntent().getExtras().getSerializable(Contants.INTENT_BMOB_GEOPONIT);
        if (geoPoint == null) {
            geoPoint = new BmobGeoPoint(116.400244, 39.963175);
        }
        setTvTitle(R.string.location);
        setTvRight(R.string.navigation);
        setBarItemVisible(true, true);
        setItemListener(new BarItemOnClick() {//Bar item listener
            @Override
            public void leftClick(View v) {
                mActivity.finish();
            }

            @Override
            public void rightClivk(View v) {

            }
        });
    }

    @Override
    public void findviewbyid() {
        mMapView = $(R.id.cusview_bmapView_map);
        mBaiduMap = mMapView.getMap();
    }

    @Override
    public void bindData() {



//定义Maker坐标点
        LatLng point = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

        //定义地图状态

        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(16)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }

    @Override
    public void handerMessage(Message msg) {

    }

    @Override
    public void mBack() {
        this.finish();
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
