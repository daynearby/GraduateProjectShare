package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.young.share.adapter.MapSearchListAdapter;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.model.gson.PlaceSearch;
import com.young.share.model.gson.PlaceSuggestion;
import com.young.share.network.NetworkReuqest;
import com.young.share.views.actionProvider.MapSearchProvider;

import java.io.Serializable;
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
    private MapSearchProvider mapSearch;

    private BmobGeoPoint geoPoint;
    private boolean isPosition;//是准备定位状态还是直接显示定位信息
    private String geoStr = "geo:%s,%s";//经纬度
    private int cityCode = 0;//城市代码，用作城市搜索
    private String city;//城市
    private int selectItem = 0;

    private static final int HANDLER_PLACE_SUGGEST = 0x01;
    private static final int HANDLER_PLACE_TEXT_CHANGE = 0x02;


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
        selectItem = getIntent().getIntExtra(Contants.INTENT_SELECTOR_POSITION, 0);

        cityCode = Integer.valueOf(app.getCacheInstance().getAsString(Contants.ACAHE_KEY_CITY_CODE));
        city = app.getCacheInstance().getAsString(Contants.ACAHE_KEY_CITY);
        placeList = (List<PlaceSearch.ResultsEntity>) app.getCacheInstance().getAsObject(Contants.ACACHE_PLACE_SERVE);

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
//               LogUtils.logE("isShow = " + mapSearch.searchView.isShown());
                if (mapSearch.isShow) {
                    mapSearch.searchView.setIconified(true);
                } else {
                    mActivity.finish();
                }

            }
        });

    }

    @Override
    public void findviewbyid() {
        mMapView = $(R.id.cusview_bmapView_map);
        searchList = $(R.id.ls_baidumap_search);

        mBaiduMap = mMapView.getMap();
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

    }

    /**
     * 搜索地点
     */
    private void nearbyPlaceSuggestion() {
        mMapView.setVisibility(View.GONE);
        searchList.setVisibility(View.VISIBLE);

        adapter = new MapSearchListAdapter(this);
        searchList.setAdapter(adapter);

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.setSelectItem(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Contants.INTENT_PLACE, adapter.getData().get(position));
                intent.putExtra(Contants.INTENT_SELECTOR_POSITION, position);
                intent.putExtras(bundle);
                mActivity.setResult(Contants.RESULT_CODE_PLACE, intent);
                mActivity.finish();
            }
        });

        //数据绑定
        if (placeList != null && placeList.size() > 0) {
            mHandler.sendEmptyMessage(HANDLER_PLACE_SUGGEST);
            adapter.setSelectItem(selectItem);
            searchList.smoothScrollToPosition(selectItem);
            searchList.setSelected(true);
        } else {//有数据进行更新
            placeList = new ArrayList<>();
            queryPlace(city);
        }
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

//这里是直接显示定位
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

        switch (msg.what) {
            case HANDLER_PLACE_SUGGEST://
                if (placeList != null && placeList.size() >= 1) {
                    adapter.setVisible(false);
                    adapter.setData(placeList);
                }

                break;

            case HANDLER_PLACE_TEXT_CHANGE://自动搜索，当文字发生改变的时候
                queryPlace((String) msg.obj);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if (isPosition) {
            getMenuInflater().inflate(R.menu.menu_baidumap, menu);
            MenuItem item = menu.findItem(R.id.menu_search_provider);

            mapSearch = (MapSearchProvider) MenuItemCompat.getActionProvider(item);
            if (mapSearch != null) {
                mapSearch.setSearchButtonClick(new MapSearchProvider.SearchButtonClick() {
                    @Override
                    public void search(String query) {

                        mapSearch.searchView.clearFocus();
                        queryPlace(query);
                    }
                });
                mapSearch.setTextChangeListener(new MapSearchProvider.TextChangeListener() {
                    @Override
                    public void textChange(String query) {
                        Message msg = new Message();
                        msg.obj = query;
                        msg.what = HANDLER_PLACE_TEXT_CHANGE;
                        mHandler.sendMessageDelayed(msg, 300L);//延时，防止重复无效搜索
                    }
                });
            }
        } else {
            getMenuInflater().inflate(R.menu.menu_baidu_navition, menu);


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

            adapter.setSelectItem(0);
/*详细地点信息*/
            NetworkReuqest.baiduPlaceSearch(mActivity, query, cityCode, new NetworkReuqest.SimpleRequestCallback<List<PlaceSearch.ResultsEntity>>() {
                @Override
                public void response(List<PlaceSearch.ResultsEntity> resultsEntities) {
                    placeList = resultsEntities;
                    if (placeList == null) {
                        placeList = new ArrayList<>();
                    }
                    placeList.add(0, new PlaceSearch.ResultsEntity("不显示"));

                    app.getCacheInstance().put(Contants.ACACHE_PLACE_SERVE, (Serializable) placeList);
                    mHandler.sendEmptyMessage(HANDLER_PLACE_SUGGEST);
                }
            });
        }

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
