package com.young.share.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.young.share.config.ApplicationConfig;
import com.young.share.config.Contants;
import com.young.share.utils.cache.ACache;

/**
 * 百度定位
 * <p/>
 * Created by Nearby Yang on 2015-10-20.
 */
public class BDLBSUtils {

    private Context ctx;
    private LocationInfoListener locationInfoListener;
    private ACache aCache;//缓存管理

    //百度定位
    private LocationClient mLocationClient = null;
    private static BDLBSUtils bdlbsUtils;

    public static BDLBSUtils builder(Context ctx, LocationInfoListener locationInfoListener) {
        if (bdlbsUtils == null) {
            synchronized (BDLBSUtils.class) {
                if (bdlbsUtils == null) {
                    bdlbsUtils = new BDLBSUtils(ctx, locationInfoListener);
                    return bdlbsUtils;
                }
            }

        }
        return bdlbsUtils;
    }

    public BDLBSUtils(Context ctx, LocationInfoListener locationInfoListener) {
        this.ctx = ctx;
        this.locationInfoListener = locationInfoListener;
        aCache = ApplicationConfig.getInstance().getCacheInstance();

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

        mLocationClient.registerLocationListener(new MyLocationListener());


    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation data) {
            LogUtils.d("bdlbs", "纬度 = " + data.getLatitude()
                    + " 经度 =" + data.getLongitude()
                    + "\n 省 = " + data.getProvince()
                    + "\n 城市 = " + data.getCity()
                    + " 地区 = " + data.getDistrict()
                    + "\n 街道 = " + data.getStreet()
                    + "\n 门牌号 = " + data.getStreetNumber());

//保存经纬度,存储的时候不能存空的变量
            if (data.getCityCode() != null && data.getCity() != null) {
                aCache.put(Contants.ACAHE_KEY_LONGITUDE,
                        data.getLongitude() + "," + data.getLatitude());
                aCache.put(Contants.ACAHE_KEY_CITY_CODE,
                        data.getCityCode());
                aCache.put(Contants.ACAHE_KEY_CITY,
                        data.getCity());
            }

            locationInfoListener.LocationInfo(data.getLatitude(),
                    data.getLongitude(),
                    data.getProvince(),
                    data.getCity(),
                    data.getDistrict(),
                    data.getStreet(),
                    data.getStreetNumber());
        }

    }

    /**
     * 启动 定位
     */
    public void startLocation() {
        if (!lbsState()) {
            mLocationClient.start();
        }

    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (lbsState()) {
            mLocationClient.stop();
        }

    }

    /**
     * 定位是否启动
     *
     * @return
     */
    public boolean lbsState() {
        return mLocationClient.isStarted();
    }

    /**
     * 监听定位 的回调
     */
    public interface LocationInfoListener {
        void LocationInfo(double latitude, double longitude, String province, String city, String district, String street, String streetNumber);
    }
}
