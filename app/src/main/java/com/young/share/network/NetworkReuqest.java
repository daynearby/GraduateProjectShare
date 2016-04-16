package com.young.share.network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.young.share.R;
import com.young.share.config.Contants;
import com.young.share.model.gson.Longitude2Location;
import com.young.share.model.gson.PlaceSearch;
import com.young.share.model.gson.PlaceSuggestion;
import com.young.share.utils.LogUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
    private static Gson gson = new Gson();

    private static final String BAIDU_GEOCODER = "http://api.map.baidu.com/geocoder/v2/";
    private static final String BAIDU_PLACE_SUGGESTION = "http://api.map.baidu.com/place/v2/suggestion";//周围
    private static final String BAIDU_PLACE_SEARCH = "http://api.map.baidu.com/place/v2/search";//服务
    public static final String BMOB_HOST = "https://api.bmob.cn/1/";//bmob
    public static final String ADVERTISERMENT = "classes/Advertisement";
    static int downloadNumber = 0;

    /**
     * 下载文件，下载完成返回url
     *
     * @param context
     * @param url
     * @param jsonResponse
     */
    public static void call(Context context, String url, final JsonRequstCallback<String> jsonResponse) {

        SmallFiledownloadRequest smallFiledownloadRequest = new SmallFiledownloadRequest(Request.Method.GET,
                SmallFiledownloadRequest.FILE_TYPE_VIDEO,
                null, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.d("success = " + response);
                if (jsonResponse != null) {
                    jsonResponse.onSuccess(response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (jsonResponse != null) {
                    jsonResponse.onFaile(error);
                }
                LogUtils.e("failure = " + error.toString());

            }
        });


        smallFiledownloadRequest.setTag(url);

//启动
        VolleyApi.getInstence(context).add(smallFiledownloadRequest);

    }

    /**
     * 下载图片
     *
     * @param context
     * @param url     未进行添加key的url,原始的url
     */
    public static void call(final Context context, String url) {
        SmallFiledownloadRequest iamgeDownload = new SmallFiledownloadRequest(context,
                url,
                SmallFiledownloadRequest.FILE_TYPE_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, context.getString(R.string.toast_save_iamge_success) + response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, R.string.toast_save_iamge_failure, Toast.LENGTH_LONG).show();
                    }
                }
        );

        iamgeDownload.setTag(url);

//启动
        VolleyApi.getInstence(context).add(iamgeDownload);

    }

    /**
     * 下载图片
     *
     * @param context
     * @param url     修改过uri的请求，添加了bmob的前面的
     */
    public static void call2(final Context context, String url) {
        SmallFiledownloadRequest iamgeDownload = new SmallFiledownloadRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, context.getString(R.string.toast_save_iamge_success) + response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, R.string.toast_save_iamge_failure, Toast.LENGTH_LONG).show();
                    }
                }
        );

        iamgeDownload.setTag(url);

//启动
        VolleyApi.getInstence(context).add(iamgeDownload);

    }

    /**
     * 下载图片,返回图片地址，用作分享
     *
     * @param context
     * @param urlList list 修改过uri的请求，添加了bmob的前面的
     */
    public static void call(final Context context, List<String> urlList, final JSONRespond jsR) {
        if (urlList == null) {
            throw new IllegalArgumentException("urlList is null...");

        }

        final int number = urlList.size();
        final List<String> imagePath = new ArrayList<>();
        for (String url : urlList) {
            SmallFiledownloadRequest iamgeDownload = new SmallFiledownloadRequest(
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            imagePath.add(response);
                            downloadNumber++;

                            if (jsR != null && number == downloadNumber) {
                                jsR.onSuccess(imagePath);
                            }


//                        Toast.makeText(context, context.getString(R.string.toast_save_iamge_success) + response, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            downloadNumber++;
                            if (jsR != null && number == downloadNumber) {
                                jsR.onFailure(error.toString());
                            }

//                        Toast.makeText(context, R.string.toast_save_iamge_failure, Toast.LENGTH_LONG).show();
                        }
                    }
            );

            iamgeDownload.setTag(url);
//启动
            VolleyApi.getInstence(context).add(iamgeDownload);

        }


    }


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
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("网址中文编码失败 " + e.toString());
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Contants.PARAM_QUERY, query);
        params.put(Contants.PARAM_REGION, String.valueOf(region));
        params.put(Contants.PARAM_OUTPUT, Contants.PARAM_JSON);
        params.put(Contants.PARAM_AK, Contants.AK);
        params.put(Contants.PARAM_MCODE, Contants.MCODE);
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
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("网址中文编码失败 " + e.toString());
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Contants.PARAM_QUERY, query);
        params.put(Contants.PARAM_REGION, String.valueOf(region));
        params.put(Contants.PARAM_OUTPUT, Contants.PARAM_JSON);
        params.put(Contants.PARAM_AK, Contants.AK);
        params.put(Contants.PARAM_MCODE, Contants.MCODE);
        params.put(Contants.PARAM_SCOPE, String.valueOf(1));
        params.put(Contants.PARAM_PAGE_NUM, String.valueOf(0));
        params.put(Contants.PARAM_PAGE_SIZE, String.valueOf(20));


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
     *
     * @param context
     * @param url
     * @param params
     * @param clazz
     * @param jsonRequstCallback
     * @param <T>
     */
    public static <T> void postRequest(Context context, String url, HashMap<String, String> params,
                                       Class clazz, final JsonRequstCallback<T> jsonRequstCallback) {


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

        String url = host + paramsStr.substring(0, paramsStr.length() - 1);


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
     * 使用httpclient实现https请求
     *
     * @param url            请求地址
     * @param clazz          gson解析模板类
     * @param simpleCallback 回调函数
     * @param <T>            返回的类型
     */
    public static <T> void getHttpsReponse(final String url,
                                           final HashMap<String, String> params,
                                           final Class<T> clazz,
                                           final SimpleRequestCallback<T> simpleCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    StringBuilder paramsStr = new StringBuilder("?");
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        paramsStr = paramsStr.append(entry.getKey());
                        paramsStr = paramsStr.append("=");
                        paramsStr = paramsStr.append(entry.getValue());
                        paramsStr.append("&");
                    }

                    String serveUrl = url + paramsStr.substring(0, paramsStr.length() - 1);
                    String responseStr = HttpsRequest.getRequest(serveUrl);

                    LogUtils.d(" responseStr = " + responseStr);

                    simpleCallback.response(gson.fromJson(responseStr, clazz));
                } catch (Exception e) {
                    LogUtils.e("get failure. message = " + e.toString());
                }
            }
        }).start();


    }

    /**
     * 使用httpclient实现https请求
     *
     * @param url            请求地址
     * @param params         post请求参数
     * @param clazz          gson解析模板类
     * @param simpleCallback 回调函数
     * @param <T>            返回的类型
     */
    public static <T> void postHttpsReponse(final String url,
                                            final JSONObject params,
                                            final Class<T> clazz,
                                            final SimpleRequestCallback<T> simpleCallback) {
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   String responseStr = HttpsRequest.postRequest(url, params);
                   simpleCallback.response(gson.fromJson(responseStr, clazz));
               } catch (Exception e) {
                   LogUtils.e("get failure. message = " + e.toString());
               }
           }
       }).start();


    }

    /**
     * 最简单的回调
     */
    public interface JSONRespond {
        void onSuccess(List<String> response);

        void onFailure(String erroMsg);
    }

    public interface JsonRequstCallback<T> {
        void onSuccess(T t);

        void onFaile(VolleyError error);

    }

    public interface SimpleRequestCallback<T> {
        void response(T t);
    }

}
