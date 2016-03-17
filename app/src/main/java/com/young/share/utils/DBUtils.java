package com.young.share.utils;

import com.young.share.model.MyUser;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.dbmodel.ShareMessage;
import com.young.share.model.dbmodel.ShareRecrod;
import com.young.share.model.dbmodel.User;

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
     * 将记录保存到本地
     */
    public static boolean saveShMessages(ShareMessage shMsg) {
        User userId = shMsg.getUserId();
        userId.save();
        shMsg.setUserId(userId);
        return shMsg.save();
//
//        List<ShareMessage> shMsgList = DataSupport.where("objectId = ?", shMsg.getObjectId())
//                .find(ShareMessage.class);
//        if (shMsgList == null) {//直接保存
//
//            return shMsg.save();
//
//        } else {//不保存
//
//            int result = shMsg.updateAll("shContent = ?", shMsg.getShContent());
//            LogUtils.logI("返回码 " + result);
//            //++++++++++++++++++返回码++++++++++++++++++++++
//            return true;
//        }
    }

    /**
     * 获取前面50条信息
     *
     * @return
     */
    public static List<ShareMessage_HZ> getShareMessages() {
        ShareMessage_HZ shareMessage;
        MyUser myUser;
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

            myUser = new MyUser();
            User userId = share.getUserId();

//            DataSupport.where("objectid", )
            myUser.setAddress(userId.getAddress());
            myUser.setQq(userId.getAvatar());
            myUser.setEmail(userId.getEmail());
            myUser.setObjectId(userId.getObjectId());
            myUser.setAge(userId.getAge());
            myUser.setMobilePhoneNumber(userId.getMobilePhoneNumber());
            myUser.setGender(userId.isGender());
            myUser.setNickName(userId.getNickName());
            myUser.setEmailVerified(userId.isEmailVerified());
            myUser.setAvatar(userId.getAvatar());
            myUser.setMobilePhoneNumberVerified(userId.isMobilePhoneNumberVerified());
            myUser.setUsername(userId.getUsername());
            myUser.setSignture(userId.getSignture());
            myUser.setSessionToken(userId.getAccessToken());

            shareMessage.setMyUserId(myUser);
            dataList.add(shareMessage);

        }

        return dataList;
    }

    /**
     * 保存 发送记录
     *
     * @param messageRec
     * @return
     */
    public static boolean saveShareRecord(ShareRecrod messageRec) {

        List<ShareRecrod> recList = DataSupport.where("shContent = ?", messageRec.getShContent())
                .find(ShareRecrod.class);
        if (recList == null) {//直接保存
            return messageRec.save();
        } else {//不保存
            int result = messageRec.updateAll("shContent = ?", messageRec.getShContent());
            LogUtils.i("返回码 " + result);
            //++++++++++++++++++返回码++++++++++++++++++++++
            return true;
        }
    }

    /**
     * 获取分享记录的前50条记录
     * 超出这50条则向云端取
     *
     * @return
     */
    public static List<ShareMessage_HZ> getShareRecord() {

        List<ShareMessage_HZ> dataList = new ArrayList<>();
        List<ShareRecrod> shMsgList = DataSupport.limit(50).order("createdAt")
                .find(ShareRecrod.class);

        for (ShareRecrod share : shMsgList) {
            ShareMessage_HZ shareMessage = new ShareMessage_HZ();
            shareMessage.setShImgs(Collections.singletonList(share.getShImgs()));
            shareMessage.setShCommNum(share.getShCommNum());
            shareMessage.setShContent(share.getShContent());
            shareMessage.setShLocation(share.getShLocation());
            shareMessage.setShTag(share.getShTag());
            shareMessage.setShVisitedNum(Collections.singletonList(share.getShVisitedNum()));
            shareMessage.setShWantedNum(Collections.singletonList(share.getShWantedNum()));

            dataList.add(shareMessage);

        }

        return dataList;

    }
}
