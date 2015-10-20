package com.young.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 百度定位
 * <p>
 * Created by Nearby Yang on 2015-10-20.
 */
public class BDLBSUtils {
    private Context ctx;
    private LocationInfoListener locationInfoListener;

    //百度定位
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();

    public BDLBSUtils(Context ctx, LocationInfoListener locationInfoListener) {
        this.ctx = ctx;
        this.locationInfoListener = locationInfoListener;

        initBDlbs();
        setLocationOpintion();

    }

    private void setLocationOpintion() {
        LocationClientOption localOption = new LocationClientOption();
        localOption.setOpenGps(true);//gps
        localOption.setProdName("com.young.location.service");
        localOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        localOption.setCoorType("bd09ll");
        localOption.setScanSpan(5000);
        localOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(localOption);
    }

    private void initBDlbs() {
        mLocationClient = new LocationClient(ctx);

        mLocationClient.registerLocationListener(myListener);


    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation data) {
            locationInfoListener.LocationInfo(data.getProvince(), data.getCity(),
                    data.getDistrict(), data.getStreet(), data.getStreetNumber());

            LogUtils.logI("百度定位"," 省 = "+data.getProvince()+" 城市 = "+data.getCity()+" 地区 = "+
                    data.getDistrict()+ " 街道 = "+data.getStreet()+" 门牌号 = "+data.getStreetNumber());

        }

    }

    /**
     * 启动 定位
     */
    public void startLocation() {
        if (!lbsState()){
            mLocationClient.start();
        }

    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (lbsState()){
            mLocationClient.stop();
        }

    }

    /**
     * 定位是否启动
     * @return
     */
    public boolean lbsState(){
       return mLocationClient.isStarted();
    }
    /**
     * 监听定位 的回调
     */
    public interface LocationInfoListener {
        public void LocationInfo(String province, String city, String district, String street, String streetNumber);
    }
}
