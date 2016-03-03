package com.young.share.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * bmob的https请求
 * Created by Nearby Yang on 2015-11-16.
 */
public class GSONRequest<T> extends Request<T> {

    private final Gson gson = new Gson();
    private Response.Listener<T> listener;
    private Class<T> clazz;
    private HashMap<String, String> params;

    /**
     * get 不带参数
     * @param url
     * @param clazz
     * @param listener
     * @param errorListener
     */
    public GSONRequest(String url, Class<T> clazz,  Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
        this.clazz = clazz;

    }
    public GSONRequest(int method, String url, Class<T> clazz, HashMap<String, String> params, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.clazz = clazz;
        this.params = params;

    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            /**
             * 得到返回的数据
             */
            String jsonStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            /**
             * 转化成对象
             */
            return Response.success(gson.fromJson(jsonStr, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }


    }

    @Override
    protected void deliverResponse(T t) {
        listener.onResponse(t);
    }

    @Override
    public HashMap<String, String> getParams() {
        return params;
    }

    @Override
    protected Map<String, String> getPostParams() throws AuthFailureError {
        params.putAll(super.getPostParams());
        return params;
    }
}
