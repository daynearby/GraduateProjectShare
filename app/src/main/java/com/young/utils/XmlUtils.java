package com.young.utils;

import android.content.Context;

import com.young.share.R;

/**
 * 读取xml中的数据
 * 城市、标签
 * Created by Nearby Yang on 2015-10-17.
 */
public class XmlUtils {

    /**
     * 获取城市信息
     * @param ctx
     * @return String[]
     */
    public static String[] getSelectCities(Context ctx) {
        String[] items = ctx.getResources().getStringArray(R.array.select_cities_items);
        return items;
    }

    /**
     * 获取标签数据
     * @param ctx
     * @return String[]
     */
    public static String[] getSelectTag(Context ctx) {
        String[] items = ctx.getResources().getStringArray(R.array.select_tag_item);
        return items;
    }


}
