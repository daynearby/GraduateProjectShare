package com.young.utils;

import com.young.model.ShareMessage_HZ;
import com.young.model.User;
import com.young.model.dbmodel.DBModel;
import com.young.model.dbmodel.ShareMessage;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
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
            objFind = new DBModel();
            objFind.setCreatedAt(DateUtils.getCurrentLongDate());

            //*********************保存。需要得到对应的实例，并且有db litepal的操作方法****************************
        } else {
            int durn = DateUtils.minuBetween(DateUtils.getCurrentLongDate(), objFind.getCreatedAt());
            DBModel comp = (DBModel) clazz;
            if (!objFind.getUpdatedAt().equals(comp.getUpdatedAt())) {
                DataSupport.deleteAll(clazz.getClass());
            } else {
                if (durn > 1) {
                    DataSupport.deleteAll(temclazz, "updatedAt < ?", String.valueOf(objFind.getId()));
                    LogUtils.logE("drop table " + temclazz);
                }
            }


        }

    }


    public static boolean saveShMessages(ShareMessage shMsg) {

        List<ShareMessage> shMsgList = DataSupport.where("shContent = ?", shMsg.getShContent())
                .find(ShareMessage.class);
        if (shMsgList == null) {//直接保存
            return shMsg.save();
        } else {//不保存
            int result = shMsg.updateAll("shContent = ?", shMsg.getShContent());
            LogUtils.logI("返回码 " + result);
            //++++++++++++++++++返回码++++++++++++++++++++++
            return true;
        }
    }

    /**
     * 获取前面50条信息
     * @return
     */
    public static List<ShareMessage_HZ> getShareMessages() {
        ShareMessage_HZ shareMessage;
        User user;
        List<ShareMessage_HZ> dataList = new ArrayList<>();

        List<ShareMessage> shMsgList = DataSupport.limit(50).order("createdAt")
                .find(ShareMessage.class);


        for (ShareMessage share : shMsgList) {
            shareMessage = new ShareMessage_HZ();
            shareMessage.setShImgs(Collections.singletonList(share.getShImgs()));
            shareMessage.setShCommNum(share.getShCommNum());
            shareMessage.setShContent(share.getShContent());
            shareMessage.setShLocation(share.getShLocation());
            shareMessage.setShTag(share.getShTag());
            shareMessage.setShVisitedNum(Collections.singletonList(share.getShVisitedNum()));
            shareMessage.setShWantedNum(Collections.singletonList(share.getShWantedNum()));

            user = new User();
            com.young.model.dbmodel.User userId = share.getUserId();
            user.setAddress(userId.getAddress());
            user.setQq(userId.getAvatar());
            user.setEmail(userId.getEmail());
            user.setObjectId(userId.getObjectId());
            user.setAge(userId.getAge());
            user.setMobilePhoneNumber(userId.getMobilePhoneNumber());
            user.setGender(userId.isGender());
            user.setNickName(userId.getNickName());
            user.setEmailVerified(userId.isEmailVerified());
            user.setAvatar(userId.getAvatar());
            user.setMobilePhoneNumberVerified(userId.isMobilePhoneNumberVerified());
            user.setUsername(userId.getUsername());
            user.setSignture(userId.getSignture());
            user.setSessionToken(userId.getAccessToken());

            shareMessage.setUserId(user);
            dataList.add(shareMessage);

        }

        return dataList;
    }

}
