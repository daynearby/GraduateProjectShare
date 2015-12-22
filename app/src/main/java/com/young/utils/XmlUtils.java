package com.young.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.young.share.R;

import java.util.ArrayList;
import java.util.Collections;
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

        Collections.addAll(itemsList, items);
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

        Collections.addAll(itemsList, items);
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

        Collections.addAll(itemsList, items);
        return itemsList;
    }

    /**
     * 性别
     *
     * @param ctx
     * @return
     */
    public static List<String> getSelectGender(Context ctx) {
        List<String> itemsList = new ArrayList<>();
        String[] items = ctx.getResources().getStringArray(R.array.select_gender_item);

        Collections.addAll(itemsList, items);
        return itemsList;
    }

    /**
     * 年龄
     *
     * @param ctx
     * @return
     */
    public static List<String> getSelectAge(Context ctx) {
        List<String> itemsList = new ArrayList<>();
        String[] items = ctx.getResources().getStringArray(R.array.select_age_item);

        Collections.addAll(itemsList, items);
        return itemsList;
    }

    /**
     * 城市对应的地区
     *
     * @param ctx
     * @param tag
     * @return
     */
    public static List<String> getSelectArea(Context ctx, int tag) {
        List<String> itemsList = new ArrayList<>();
        int rsdId = 0;
        switch (tag) {
            case 0:
                rsdId = R.array.select_gz_item;
                break;
            case 1:
                rsdId = R.array.select_sz_item;
                break;
            case 2:
                rsdId = R.array.select_fs_item;
                break;
            case 3:
                rsdId = R.array.select_dg_item;
                break;
            case 4:
                rsdId = R.array.select_zhs_item;
                break;
            case 5:
                rsdId = R.array.select_zhh_item;
                break;
            case 6:
                rsdId = R.array.select_jm_item;
                break;
            case 7:
                rsdId = R.array.select_zq_item;
                break;
            case 8:
                rsdId = R.array.select_hz_item;
                break;
            case 9:
                rsdId = R.array.select_st_item;
                break;
            case 10:
                rsdId = R.array.select_chz_item;
                break;
            case 11:
                rsdId = R.array.select_jy_item;
                break;
            case 12:
                rsdId = R.array.select_shw_item;
                break;
            case 13:
                rsdId = R.array.select_zhj_item;
                break;
            case 14:
                rsdId = R.array.select_mm_item;
                break;
            case 15:
                rsdId = R.array.select_yj_item;
                break;
            case 16:
                rsdId = R.array.select_shg_item;
                break;
            case 17:
                rsdId = R.array.select_qy_item;
                break;
            case 18:
                rsdId = R.array.select_yf_item;
                break;
            case 19:
                rsdId = R.array.select_mzh_item;
                break;
            case 20:
                rsdId = R.array.select_hy_item;
                break;
        }


        String[] items = ctx.getResources().getStringArray(rsdId);

        Collections.addAll(itemsList, items);
        return itemsList;
    }

    /**
     * 获取背景颜色
     *
     * @param ctx
     * @return
     */
    public static List<Integer> getSelectRankBackgroundColor(Context ctx) {
        List<Integer> bacegroundColorIdList = new ArrayList<>();
//
//        TypedArray ar = ctx.getResources().obtainTypedArray(R.array.select_rank_backgound_color);
//
//        for (int i = 0; i < ar.length(); i++) {
//
//            bacegroundColorIdList.add(ar.getResourceId(i, 0));
//        }
//        ar.recycle();

        int[] drawableId = ctx.getResources().getIntArray(R.array.select_rank_backgound_color);
        for (Integer in : drawableId) {
            bacegroundColorIdList.add(in);
        }
        return bacegroundColorIdList;
    }

    /**
     * 获取rank的图标id
     *
     * @param ctx
     * @return
     */
    public static List<Integer> getSelectRankIcon(Context ctx) {
        List<Integer> rankIconList = new ArrayList<>();
        TypedArray ar = ctx.getResources().obtainTypedArray(R.array.select_rank_icon);
//        int[] drawableId = ctx.getResources().getIntArray(R.array.select_rank_icon);
        for (int i = 0; i < ar.length(); i++) {
            rankIconList.add(ar.getResourceId(i, 0));
        }
        ar.recycle();
        return rankIconList;
    }

}
