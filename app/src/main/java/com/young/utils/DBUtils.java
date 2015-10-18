package com.young.utils;

import com.young.model.dbmodel.DBModel;
import com.young.model.dbmodel.ShareMessage;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 本地缓存操作，包括网站、其他String
 * Created by Nearby Yang on 2015-10-16.
 */
public class DBUtils {

    //一分钟之后要更新一下本地数据库
    private static final int HOUR_5 = 1;

    /**
     * 检查时间
     * 时间超过
     *
     * @param clazz
     */
    private static void DBManage(Object clazz) {
        Class temclazz = clazz.getClass();
        DBModel objFind = (DBModel) DataSupport.findFirst(clazz.getClass());
        if (objFind == null) {
            objFind.setCreatedAt(DateUtils.getCurrentLongDate());

           //*********************保存。需要得到对应的实例，并且有db litepal的操作方法****************************
        } else {
            int durn = DateUtils.minuBetween(DateUtils.getCurrentLongDate(), objFind.getCreatedAt());
            DBModel comp = (DBModel) clazz;
            if (objFind.getUpdatedAt() != comp.getUpdatedAt()) {
                DataSupport.deleteAll(clazz.getClass());
            } else {
                if (durn > 1) {
                    DataSupport.deleteAll(temclazz, "updatedAt < ?", String.valueOf(objFind.getId()));
                    LogUtils.logE("drop table "+temclazz);
                }
            }


        }

    }


    // TODO: 2015-10-16 建立本地缓存的表，配置litepal

    public static boolean saveShMessages(ShareMessage shMsg) {

        List<ShareMessage> shMsgList = DataSupport.where("shContent = ?", shMsg.getShContent()).find(ShareMessage.class);
        if (shMsgList == null) {//直接保存
            return shMsg.save();
        } else {//不保存
            int result = shMsg.updateAll("shContent = ?", shMsg.getShContent());
            LogUtils.logI("返回码 " + result);
            //++++++++++++++++++返回码++++++++++++++++++++++
            return true;
        }
    }

}
