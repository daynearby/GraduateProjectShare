package com.young.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.young.share.base.ItemActBarActivity;
import com.young.share.config.Contants;
import com.young.share.model.gson.Longitude2Location;
import com.young.share.model.gson.PlaceSuggestion;
import com.young.share.network.NetworkReuqest;

import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 百度地图
 * 显示地理位置，拾取坐标
 * 都需要传入初始坐标
 * <p/>
 * <p/>
 * Created by Nearby Yang on 2016-01-02.
 */
public class BaiduMapActivity extends ItemActBarActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Marker marker;//进行拖拽的对象
    private LatLng resultPoint;//拖拽之后确定的坐标
    private BmobGeoPoint geoPoint;
    private boolean isPosition;//是准备定位状态还是直接显示定位信息
    private String geoStr = "geo:%s,%s";//经纬度
    private String location = "%s,%s";//经纬度
    private int cityCode = 0;//城市代码，用作城市搜索

    // TODO: 2016-02-19 分享信息，拖拽实现定位

    @Override
    public int getLayoutId() {
        return R.layout.activity_baidu_map;
    }

    @Override
    public void initData() {
        super.initData();

        geoPoint = (BmobGeoPoint) getIntent().getExtras().getSerializable(Contants.INTENT_BMOB_GEOPONIT);
        isPosition = getIntent().getBooleanExtra(Contants.INTENT_BMOB_IS_POSITION, false);

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
//调用外部地图显示位置
                Uri uri = Uri.parse(String.format(geoStr, geoPoint.getLatitude(), geoPoint.getLongitude()));

                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
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
                .fromResource(R.drawable.icon_mark);
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

//debug，只有在发送信息的时候，进入该状态
        if (isPosition) {

            //// TODO: 2016-03-04 需要先定位，获取citycode，存储起来，等下需要用到搜索，那么就使用该citycode

            OverlayOptions options = new MarkerOptions()
                    .position(point)  //设置marker的位置
                    .icon(bitmap)  //设置marker图标
                    .zIndex(9)  //设置marker所在层级
                    .draggable(true);  //设置手势拖拽
//将marker添加到地图上
            marker = (Marker) (mBaiduMap.addOverlay(options));


            //调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
            mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
                public void onMarkerDrag(Marker marker) {
                    //拖拽中
                }

                public void onMarkerDragEnd(Marker marker) {
                    //拖拽结束,通过网络请求，获取正确的位置信息
                    resultPoint = marker.getPosition();
                    NetworkReuqest.convertLongitude2Location(mActivity,
                            String.format(location, resultPoint.latitude, resultPoint.longitude),
                            new NetworkReuqest.SimpleRequestCallback<Longitude2Location.ResultEntity>() {

                                @Override
                                public void response(Longitude2Location.ResultEntity resultEntity) {
//                                    Toast.makeText(mActivity,
//                                    resultEntity.getFormattedAddress()+"--"+resultEntity.toString(),
//                                    Toast.LENGTH_SHORT).show();
// TODO: 2016-02-16 拖拽结束之后，获取拖拽之后的坐标，点击完成，获取准确的坐标
                                    resultEntity.getFormattedAddress();//完整的地址
                                }

                            });

                }

                public void onMarkerDragStart(Marker marker) {
                    //开始拖拽
                }
            });

        }



    }

    @Override
    public void handerMessage(Message msg) {

    }


    private void queryPlace(){
        String query = "获取输入框文字";
        NetworkReuqest.baiduPlaceSuggestion(mActivity, query, cityCode, new NetworkReuqest.SimpleRequestCallback<List<PlaceSuggestion.ResultEntity>>() {
            @Override
            public void response(List<PlaceSuggestion.ResultEntity> resultEntities) {
                // TODO: 2016-03-04 一个listview显示地点信息，ListView后面有单选按钮



            }
        });
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
