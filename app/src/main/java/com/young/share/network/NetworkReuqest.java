package com.young.share.network;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.young.share.config.Contants;
import com.young.share.model.gson.Longitude2Location;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

;


/**
 * Volley网络请求类
 * <p>
 * Created by Nearby Yang on 2016-03-03.
 */
public class NetworkReuqest {

    private static final String BAIDU_GEOCODER = "http://api.map.baidu.com/geocoder/v2/";


    /**
     * 将拖拽获取的坐标转化成地址信息
     *
     * @param context
     * @param geoPoint
     * @param jsonRequstCallback
     */
    public static void convertLongitude2Location(Context context, String geoPoint, final JSonRequstCallback jsonRequstCallback) {

        JSONObject params = new JSONObject();

        try {
            params.put(Contants.PARAM_AK, Contants.AK);
            params.put(Contants.PARAM_OUTPUT, Contants.PARAM_JSON);
            params.put(Contants.PARAM_LOCATION, geoPoint);
            params.put(Contants.PARAM_MCODE, Contants.MCODE);
        } catch (JSONException e) {
            LogUtils.logD("百度地图，添加参数失败");
            e.printStackTrace();
        }
        final GSONRequest<Longitude2Location> gsonRequest = new GSONRequest<>(BAIDU_GEOCODER,
                Longitude2Location.class,
                new Response.Listener<Longitude2Location>() {

                    @Override
                    public void onResponse(Longitude2Location longitude2Location) {
                        if (jsonRequstCallback != null) {
                            jsonRequstCallback.callback(longitude2Location.getResult());
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        LogUtils.logE("volley 请求出错 " + volleyError.toString());
                    }


                });
//启动
        VolleyApi.getInstence(context).add(gsonRequest);

    }


    public interface JSonRequstCallback {
        void callback(Longitude2Location.ResultEntity resultEntity);
    }

}
