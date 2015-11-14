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

    /**
     * 获取标签数据
     *
     * @param ctx
     * @return String[]
     */
    public static List<String> getSelectOption(Context ctx) {
        List<String> itemsList = new ArrayList<>();
        String[] items = ctx.getResources().getStringArray(R.array.select_option_item);

        for (int i = 0; i < items.length; i++) {
            itemsList.add(items[i]);
        }
        return itemsList;
    }

    /**
     * 性别
     * @param ctx
     * @return
     */
    public static List<String> getSelectGender(Context ctx){
        List<String> itemsList = new ArrayList<>();
        String[] items = ctx.getResources().getStringArray(R.array.select_gender_item);

        for (int i = 0; i < items.length; i++) {
            itemsList.add(items[i]);
        }
        return itemsList;
    }

    /**
     * 年龄
     * @param ctx
     * @return
     */
    public static List<String> getSelectAge(Context ctx){
        List<String> itemsList = new ArrayList<>();
        String[] items = ctx.getResources().getStringArray(R.array.select_age_item);

        for (int i = 0; i < items.length; i++) {
            itemsList.add(items[i]);
        }
        return itemsList;
    }

    /**
     * 城市对应的地区
     * @param ctx
     * @param tag
     * @return
     */
    public static List<String> getSelectArea(Context ctx,int tag) {
        List<String> itemsList = new ArrayList<>();
int rsdId = 0;
        switch (tag){
            case 0:
                rsdId=R.array.select_gz_item;
                break;
            case 1:
                rsdId=R.array.select_sz_item;
                break;
            case 2:
                rsdId=R.array.select_fs_item;
                break;
            case 3:
                rsdId=R.array.select_dg_item;
                break;
            case 4:
                rsdId=R.array.select_zhs_item;
                break;
            case 5:
                rsdId=R.array.select_zhh_item;
                break;
            case 6:
                rsdId=R.array.select_jm_item;
                break;
            case 7:
                rsdId=R.array.select_zq_item;
                break;
            case 8:
                rsdId=R.array.select_hz_item;
                break;
            case 9:
                rsdId=R.array.select_st_item;
                break;
            case 10:
                rsdId=R.array.select_chz_item;
                break;
            case 11:
                rsdId=R.array.select_jy_item;
                break;
            case 12:
                rsdId=R.array.select_shw_item;
                break;
            case 13:
                rsdId=R.array.select_zhj_item;
                break;
            case 14:
                rsdId=R.array.select_mm_item;
                break;
            case 15:
                rsdId=R.array.select_yj_item;
                break;
            case 16:
                rsdId=R.array.select_shg_item;
                break;
            case 17:
                rsdId=R.array.select_qy_item;
                break;
            case 18:
                rsdId=R.array.select_yf_item;
                break;
            case 19:
                rsdId=R.array.select_mzh_item;
                break;
            case 20:
                rsdId=R.array.select_hy_item;
                break;
        }



        String[] items = ctx.getResources().getStringArray(rsdId);

        for (int i = 0; i < items.length; i++) {
            itemsList.add(items[i]);
        }
        return itemsList;
    }

}
