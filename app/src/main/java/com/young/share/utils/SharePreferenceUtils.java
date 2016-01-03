package com.young.share.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * shareperference 工具类
 * 存储和读取的模式都是私有类型
 * 可以读写：布尔值、整形数、浮点数、字符串
 *
 * Created by Nearby Yang on 2015-08-13.
 *
 */
public class SharePreferenceUtils {

    private SharedPreferences sharedPreferences;
    private Context ctx;

    public SharePreferenceUtils(Context ctx){
        this.ctx=ctx;

    }

    /**
     * 存储布尔值
     * @param name 存储的文件名
     * @param key 存储的键值对的键名
     * @param value 存储的键值对的值
     */
    public void setBoolean(String name,String key,boolean value ){

        sharedPreferences=ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();

    }

    /**
     * 存储整型数
     * @param name 存储的文件名
     * @param key 存储的键值对的键名
     * @param value 存储的键值对的值
     */

    public void setInt(String name,String key,int value ){

        sharedPreferences=ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    /**
     * 存储字符串
     * @param name 存储的文件名
     * @param key 存储的键值对的键名
     * @param value 存储的键值对的值
     */
    public void setString(String name,String key,String value ){

        sharedPreferences=ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    /**
     * 获取布尔值
     * @param name 存储的文件名
     * @param key 存储的键名
     * @return 键值对的值，不存在则返回false
     *  默认返回 true
     */

    public boolean getBoolean(String name,String key ){

        sharedPreferences=ctx.getSharedPreferences(name, Context.MODE_PRIVATE);


        return sharedPreferences.getBoolean(key,true);
    }

    /**
     * 获取整形数
     *
     * @param name 存储的文件名
     * @param key 存储的键名
     * @return 键值对的值，没有则返回0
     */
    public int getInt(String name,String key){

        sharedPreferences=ctx.getSharedPreferences(name,Context.MODE_PRIVATE);


        return sharedPreferences.getInt(key, 0);
    }

    /**
     * 获取浮点数
     * @param name 存储的文件名
     * @param key 存储的键名
     * @return 键值对的值，如果不存在就返回0.0
     */
    public float setFloat(String name,String key){

        sharedPreferences=ctx.getSharedPreferences(name,Context.MODE_PRIVATE);

        return sharedPreferences.getFloat(key,0.0f);
    }
    /**
     * 获取字符串
     * @param name 存储的文件名
     * @param key 存储的键名
     * @return 键值对的值，没有返回null
     */
    public String getString(String name,String key){
        sharedPreferences=ctx.getSharedPreferences(name,Context.MODE_PRIVATE);

        return sharedPreferences.getString(key,null);

    }

//未定
    public void setAll(String name,String key){
        sharedPreferences=ctx.getSharedPreferences(name,Context.MODE_PRIVATE);
    }
}
