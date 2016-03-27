package com.young.share.network;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.young.share.config.Contants;
import com.young.share.model.gson.Longitude2Location;
import com.young.share.model.gson.PlaceSearch;
import com.young.share.model.gson.PlaceSuggestion;
import com.young.share.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;


/**
 * Volley网络请求类
 * <p/>
 * Created by Nearby Yang on 2016-03-03.
 */
public class NetworkReuqest {

    private static final String BAIDU_GEOCODER = "http://api.map.baidu.com/geocoder/v2/";
    private static final String BAIDU_PLACE_SUGGESTION = "http://api.map.baidu.com/place/v2/suggestion";//周围
    private static final String BAIDU_PLACE_SEARCH = "http://api.map.baidu.com/place/v2/search";//服务
    public static final String BMOB_HOST = "https://api.bmob.cn/1/";//bmob
    public static final String ADVERTISERMENT = "classes/Advertisement";


    /**
     * @param context
     * @param geo                   坐标
     * @param simpleRequestCallback
     */
    public static void convertLongitude2Location(Context context, String geo,
                                                 final SimpleRequestCallback<Longitude2Location.ResultEntity> simpleRequestCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Contants.PARAM_AK, Contants.AK);
        params.put(Contants.PARAM_OUTPUT, Contants.PARAM_JSON);
        params.put(Contants.PARAM_LOCATION, geo);
        params.put(Contants.PARAM_MCODE, Contants.MCODE);

        request(context, BAIDU_GEOCODER, params, Longitude2Location.class,
                new JsonRequstCallback<Longitude2Location>() {

                    @Override
                    public void onSuccess(Longitude2Location longitude2Location) {
                        if (simpleRequestCallback != null) {
                            simpleRequestCallback.response(longitude2Location.getResult());
                        }
                    }

                    @Override
                    public void onFaile(VolleyError error) {

                    }
                });

    }

    /**
     * @param context
     * @param query   查询的关键字
     * @param region  如果没有citycode 那么默认是全国
     */
    public static void baiduPlaceSuggestion(Context context, String query, int region,
                                            final SimpleRequestCallback<List<PlaceSuggestion.ResultEntity>> simpleRequestCallback) {
        try {
            query = URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("网址中文编码失败 " + e.toString());
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Contants.PARAM_QUERY, query);
        params.put(Contants.PARAM_REGION, String.valueOf(region));
        params.put(Contants.PARAM_OUTPUT, Contants.PARAM_JSON);
        params.put(Contants.PARAM_AK, Contants.AK);
        params.put(Contants.PARAM_MCODE,Contants.MCODE);
//        params.put(Contants.PARAM_SCOPE,String.valueOf(2));


        request(context, BAIDU_PLACE_SUGGESTION, params, PlaceSuggestion.class,
                new JsonRequstCallback<PlaceSuggestion>() {

                    @Override
                    public void onSuccess(PlaceSuggestion entity) {
                        if (simpleRequestCallback != null) {
                            simpleRequestCallback.response(entity.getResult());
                        }
                    }

                    @Override
                    public void onFaile(VolleyError error) {

                    }
                });

    }

    /**
     * @param context
     * @param query   查询的关键字
     * @param region  如果没有citycode 那么默认是全国
     */
    public static void baiduPlaceSearch(Context context, String query, int region,
                                            final SimpleRequestCallback<List<PlaceSearch.ResultsEntity>> simpleRequestCallback) {
        try {
            query = URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("网址中文编码失败 " + e.toString());
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Contants.PARAM_QUERY, query);
        params.put(Contants.PARAM_REGION, String.valueOf(region));
        params.put(Contants.PARAM_OUTPUT, Contants.PARAM_JSON);
        params.put(Contants.PARAM_AK, Contants.AK);
        params.put(Contants.PARAM_MCODE,Contants.MCODE);
        params.put(Contants.PARAM_SCOPE,String.valueOf(1));
        params.put(Contants.PARAM_PAGE_NUM,String.valueOf(0));
        params.put(Contants.PARAM_PAGE_SIZE,String.valueOf(20));


        request(context, BAIDU_PLACE_SEARCH, params, PlaceSearch.class,
                new JsonRequstCallback<PlaceSearch>() {

                    @Override
                    public void onSuccess(PlaceSearch entity) {
                        if (simpleRequestCallback != null) {
                            simpleRequestCallback.response(entity.getResults());
                        }
                    }

                    @Override
                    public void onFaile(VolleyError error) {

                    }
                });

    }

    /**
     * post请求
     * @param context
     * @param url
     * @param params
     * @param clazz
     * @param jsonRequstCallback
     * @param <T>
     */
    public static <T>void  postRequest(Context context, String url, HashMap<String, String> params,
                                    Class clazz, final JsonRequstCallback<T> jsonRequstCallback){


        GSONRequest<T> jr = new GSONRequest<T>(url,
                clazz,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T t) {
                        if (jsonRequstCallback != null) {
                            jsonRequstCallback.onSuccess(t);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.e(error.toString());
                if (jsonRequstCallback != null) {
                    jsonRequstCallback.onFaile(error);
                }
            }
        });

        jr.setTag(url);

//启动
        VolleyApi.getInstence(context).add(jr);
    }


    /**
     * get请求的通用模块
     *
     * @param context
     * @param host
     * @param params
     * @param clazz
     * @param <T>
     */
    public static <T> void request(Context context, String host, HashMap<String, String> params,
                                   Class clazz, final JsonRequstCallback<T> jsonRequstCallback) {

        StringBuilder paramsStr = new StringBuilder("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsStr = paramsStr.append(entry.getKey());
            paramsStr = paramsStr.append("=");
            paramsStr = paramsStr.append(entry.getValue());
            paramsStr.append("&");
        }

        String url = host + paramsStr.substring(0, paramsStr.length()-1);


        GSONRequest<T> jr = new GSONRequest<T>(url,
                clazz,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T t) {
                        if (jsonRequstCallback != null) {
                            jsonRequstCallback.onSuccess(t);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.e(error.toString());
                if (jsonRequstCallback != null) {
                    jsonRequstCallback.onFaile(error);
                }
            }
        });

        jr.setTag(url);

//启动
        VolleyApi.getInstence(context).add(jr);

    }




    public interface JsonRequstCallback<T> {
        void onSuccess(T t);

        void onFaile(VolleyError error);

    }

    public interface SimpleRequestCallback<T> {
        void response(T t);
    }

}
