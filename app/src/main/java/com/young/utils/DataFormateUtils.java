package com.young.utils;

import com.young.config.Contants;
import com.young.model.CommRemoteModel;
import com.young.model.DiscountMessage_HZ;
import com.young.model.ShareMessage_HZ;

import java.io.Serializable;

/**
 * 格式化公用类
 * Created by Nearby Yang on 2015-12-04.
 */
public class DataFormateUtils {




    /**
     * sharemessage
     * <p/>
     * 处理数据
     *
     * @param serializableExtra
     */
    public static CommRemoteModel formateDataDiscover(Serializable serializableExtra) {
        CommRemoteModel commModel =new CommRemoteModel();

        ShareMessage_HZ shareMessage = (ShareMessage_HZ) serializableExtra;

        commModel.setContent(shareMessage.getShContent());
        commModel.setImages(shareMessage.getShImgs());
        commModel.setLocationInfo(shareMessage.getShLocation());
        commModel.setTag(shareMessage.getShTag());
        commModel.setUser(shareMessage.getUserId());
        commModel.setVisited(shareMessage.getShVisitedNum());
        commModel.setWanted(shareMessage.getShWantedNum());
        commModel.setObjectId(shareMessage.getObjectId());
        commModel.setComment(shareMessage.getShCommNum());
        commModel.setMcreatedAt(shareMessage.getCreatedAt());
        commModel.setType(Contants.DATA_MODEL_HEAD);//属于分享信息


        return commModel;
    }

    /**
     * 将DiscountMessage转换成Sharemessage_HZ
     *
     * @param discountMessage
     * @return
     */
    public static ShareMessage_HZ formateDiscover(DiscountMessage_HZ discountMessage){
        ShareMessage_HZ shareMessage = new ShareMessage_HZ();

//        shareMessage.setObjectId();
        shareMessage.setUserId(discountMessage.getUserId());
        shareMessage.setShVisitedNum(discountMessage.getDtVisitedNum());
//        shareMessage.set

        return shareMessage;
    }
}
