package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
import com.young.share.adapter.MapSearchListAdapter;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.model.gson.Longitude2Location;
import com.young.share.model.gson.PlaceSearch;
import com.young.share.model.gson.PlaceSuggestion;
import com.young.share.network.NetworkReuqest;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.LogUtils;

import java.util.ArrayList;
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
    private ListView searchList;
    private List<PlaceSearch.ResultsEntity> placeList;
    private MapSearchListAdapter adapter;

    private Marker marker;//进行拖拽的对象
    private LatLng resultPoint;//拖拽之后确定的坐标
    private BmobGeoPoint geoPoint;
    private boolean isPosition;//是准备定位状态还是直接显示定位信息
    private String geoStr = "geo:%s,%s";//经纬度
    private String LOCATION = "%s,%s";//经纬度
    private int cityCode = 0;//城市代码，用作城市搜索
    private String city;//城市
    private int viewHeight = 0;//ListView

    private static final int HANDLER_PLACE_SUGGEST = 0x01;

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

        cityCode = Integer.valueOf(app.getCacheInstance().getAsString(Contants.ACAHE_KEY_CITY_CODE));
        city = app.getCacheInstance().getAsString(Contants.ACAHE_KEY_CITY_CODE);

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
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
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
        searchList = $(R.id.ls_baidumap_search);

        mBaiduMap = mMapView.getMap();
        viewHeight = DisplayUtils.getScreenHeightPixels(mActivity) - DisplayUtils.getStatusBarHeight(searchList);

    }

    @Override
    public void bindData() {
        if (isPosition) {
/*附近地点搜索*/
            nearbyPlaceSuggestion();
        } else {
        /*百度地图初始化*/
            baidu();
        }
// TODO: 2016-03-12 在你需要重新设置菜单的时候，调用这个方法：invalidateOptionsMenu();

    }

    /**
     * 搜索地点
     */
    private void nearbyPlaceSuggestion() {
        mMapView.setVisibility(View.GONE);
        searchList.setVisibility(View.VISIBLE);
        placeList = new ArrayList<>();
        adapter = new MapSearchListAdapter(this);
        searchList.setAdapter(adapter);

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LogUtils.logE("条目 " + placeList.get(position));
            }
        });

        queryPlace(city);
    }


    /**
     * 百度地图初始化
     */
    private void baidu() {

        //定义Maker坐标点
        LatLng point = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        mMapView.showZoomControls(false);

        mBaiduMap.showMapPoi(true);


//debug，只有在发送信息的时候，进入该状态
        if (isPosition) {

            //// TODO: 2016-03-04 需要先定位，获取citycode，存储起来，等下需要用到搜索，那么就使用该citycode
// 当不需要定位图层时关闭定位图层

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
            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING,
                    true, null);
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
            mBaiduMap.setMyLocationEnabled(false);

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

        switch (msg.what) {
            case HANDLER_PLACE_SUGGEST:
                if (placeList != null && placeList.size() >= 1) {
                    adapter.setVisible(false);
                    adapter.setData(placeList);
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if (isPosition) {
            getMenuInflater().inflate(R.menu.menu_baidumap, menu);

            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
            searchView.setQueryHint(getString(R.string.hint_search_places));
//            searchView.setSubmitButtonEnabled(true);
            searchView.setIconifiedByDefault(false);
//            final String query = String.valueOf(searchView.getQuery());

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    queryPlace(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {

//                    Toast.makeText(mActivity, query + "", Toast.LENGTH_SHORT).show();
                    queryPlace(query);

                    return false;
                }
            });


        } else {
            getMenuInflater().inflate(R.menu.menu_baidumap2, menu);


        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 查询位置
     *
     * @param query
     */
    private void queryPlace(String query) {
        if (!TextUtils.isEmpty(query)) {
//            Toast.makeText(mActivity, query + "---", Toast.LENGTH_SHORT).show();

//            NetworkReuqest.baiduPlaceSuggestion(mActivity, query, cityCode, new NetworkReuqest.SimpleRequestCallback<List<PlaceSuggestion.ResultEntity>>() {
//                @Override
//                public void response(List<PlaceSuggestion.ResultEntity> resultEntities) {
//                    placeList = resultEntities;
//                    if (placeList ==null){
//                        placeList = new ArrayList<PlaceSuggestion.ResultEntity>();
//                    }
//                    placeList.add(0, new PlaceSuggestion.ResultEntity("不显示"));
//                    mHandler.sendEmptyMessage(HANDLER_PLACE_SUGGEST);
//                }
//            });

            NetworkReuqest.baiduPlaceSearch(mActivity, query, cityCode, new NetworkReuqest.SimpleRequestCallback<List<PlaceSearch.ResultsEntity>>() {
                @Override
                public void response(List<PlaceSearch.ResultsEntity> resultsEntities) {
                    placeList = resultsEntities;
                    if (placeList == null) {
                        placeList = new ArrayList<PlaceSearch.ResultsEntity>();
                    }
                    placeList.add(0, new PlaceSearch.ResultsEntity("不显示"));
                    mHandler.sendEmptyMessage(HANDLER_PLACE_SUGGEST);
                }
            });
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (isPosition) {


            View view = MenuItemCompat.getActionView(menu.findItem(R.id.menu_search_btn));

            LogUtils.logE(" view = " + view);

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!isPosition && item.getItemId() == R.id.menu_navigation) {//导航
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
