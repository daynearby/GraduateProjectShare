package com.young.utils;

import android.content.Context;

import com.young.share.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 读取xml中的数据
 * 城市、标签
 * Created by Nearby Yang on 2015-10-17.
 */
public class XmlUtils {

    /**
     * 获取城市信息
     *
     * @param ctx
     * @return String[]
     */
    public static List<String> getSelectCities(Context ctx) {
        List<String> itemsList = new ArrayList<>();
        String[] items = ctx.getResources().getStringArray(R.array.select_cities_items);

        for (int i = 0; i < items.length; i++) {
            itemsList.add(items[i]);
        }
        return itemsList;
    }

    /**
     * 获取标签数据
     *
     * @param ctx
     * @return String[]
     */
    public static List<String> getSelectTag(Context ctx) {
        List<String> itemsList = new ArrayList<>();
        String[] items = ctx.getResources().getStringArray(R.array.select_tag_item);

        for (int i = 0; i < items.length; i++) {
            itemsList.add(items[i]);
        }
        return itemsList;
    }


}
