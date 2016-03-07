package com.young.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.young.share.base.BaseAppCompatActivity;
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
public class BaiduMapActivity extends BaseAppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private ListView searchResultLs;

    private Marker marker;//进行拖拽的对象
    private LatLng resultPoint;//拖拽之后确定的坐标
    private BmobGeoPoint geoPoint;
    private boolean isPosition;//是准备定位状态还是直接显示定位信息
    private String geoStr = "geo:%s,%s";//经纬度
    private String LOCATION = "%s,%s";//经纬度
    private int cityCode = 0;//城市代码，用作城市搜索

    // TODO: 2016-02-19 分享信息，拖拽实现定位

    @Override
    public int getLayoutId() {
        return R.layout.activity_baidu_map;
    }

    @Override
    public void initData() {
        initializeToolbar();
        setTitle(R.string.location);
        geoPoint = (BmobGeoPoint) getIntent().getExtras().getSerializable(Contants.INTENT_BMOB_GEOPONIT);
        isPosition = getIntent().getBooleanExtra(Contants.INTENT_BMOB_IS_POSITION, false);

        if (geoPoint == null) {
            String LONGITUDE = app.getCacheInstance().getAsString(Contants.ACAHE_KEY_LONGITUDE);
            String[] strs = LONGITUDE.split(",");
            double longitude = Double.valueOf(strs[0]);
            double latitude = Double.valueOf(strs[1]);

            if (longitude > 0 && latitude > 0) {
                geoPoint = new BmobGeoPoint(longitude, latitude);
            } else {
                geoPoint = new BmobGeoPoint(114.424984, 23.043472);//惠州学院
            }
        }


    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_baidu_map);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

    }

    @Override
    public void findviewbyid() {
        mMapView = $(R.id.cusview_bmapView_map);
        searchResultLs = $(R.id.ls_baidumap_search);

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


//debug，只有在发送信息的时候，进入该状态
        if (isPosition) {

            //// TODO: 2016-03-04 需要先定位，获取citycode，存储起来，等下需要用到搜索，那么就使用该citycode
// 当不需要定位图层时关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
            OverlayOptions options = new MarkerOptions()
                    .position(point)  //设置marker的位置
                    .icon(bitmap)  //设置marker图标
                    .zIndex(9)  //设置marker所在层级
                    .draggable(true);  //设置手势拖拽
//将marker添加到地图上
            marker = (Marker) (mBaiduMap.addOverlay(options));
// 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
// 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(200)//设置定位数据的精度信息，单位：米
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(geoPoint.getLatitude())
                    .longitude(geoPoint.getLongitude()).build();
// 设置定位数据
            mBaiduMap.setMyLocationData(locData);
// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            MyLocationConfiguration config = new MyLocationConfiguration( MyLocationConfiguration.LocationMode.FOLLOWING ,
                    true,null);
            mBaiduMap.setMyLocationConfigeration(config);

            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(point)
                    .zoom(16)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);

            //调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
            mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
                public void onMarkerDrag(Marker marker) {
                    //拖拽中
                }

                public void onMarkerDragEnd(Marker marker) {
                    //拖拽结束,通过网络请求，获取正确的位置信息
                    resultPoint = marker.getPosition();
                    NetworkReuqest.convertLongitude2Location(mActivity,
                            String.format(LOCATION, resultPoint.latitude, resultPoint.longitude),
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

        } else {//这里是直接显示定位
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


    }

    @Override
    public void handerMessage(Message msg) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isPosition) {
            getMenuInflater().inflate(R.menu.menu_baidumap, menu);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
            searchView.setQueryHint(getString(R.string.hint_search_places));
            searchView.setSubmitButtonEnabled(true);
            final String query = String.valueOf(searchView.getQuery());

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mActivity, query, Toast.LENGTH_SHORT).show();
                }
            });
            searchView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Toast.makeText(mActivity, "long click ", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        } else {
            getMenuInflater().inflate(R.menu.menu_baidumap2, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_navigation) {//导航
            //调用外部地图显示位置
            Uri uri = Uri.parse(String.format(geoStr, geoPoint.getLatitude(), geoPoint.getLongitude()));

            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    private void queryPlace() {
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
