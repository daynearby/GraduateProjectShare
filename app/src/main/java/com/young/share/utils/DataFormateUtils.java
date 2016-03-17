package com.young.share.utils;

import android.content.Context;

import com.young.share.config.Contants;
import com.young.share.model.CommRemoteModel;
import com.young.share.model.Comment_HZ;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.MyUser;
import com.young.share.model.PictureInfo;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.dbmodel.ShareMessage;
import com.young.share.model.dbmodel.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    public static CommRemoteModel formateDataDiscover(Serializable serializableExtra,int type) {
        CommRemoteModel commModel =new CommRemoteModel();

        ShareMessage_HZ shareMessage = (ShareMessage_HZ) serializableExtra;

        commModel.setContent(shareMessage.getShContent());
        commModel.setImages(shareMessage.getShImgs());
        commModel.setLocationInfo(shareMessage.getShLocation());
        commModel.setTag(shareMessage.getShTag());
        commModel.setMyUser(shareMessage.getMyUserId());
        commModel.setVisited(shareMessage.getShVisitedNum());
        commModel.setWanted(shareMessage.getShWantedNum());
        commModel.setObjectId(shareMessage.getObjectId());
        commModel.setComment(shareMessage.getShCommNum());
        commModel.setMcreatedAt(shareMessage.getCreatedAt());
        commModel.setType(type);//属于分享信息Contants.DATA_MODEL_HEAD


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
        shareMessage.setMyUserId(discountMessage.getMyUserId());
        shareMessage.setShVisitedNum(discountMessage.getDtVisitedNum());
//        shareMessage.set

        return shareMessage;
    }

    /**
     * 格式化商家优惠
     * @param serializableExtra
     * @return
     */
    public static CommRemoteModel formateDataDiscount(Serializable serializableExtra) {
        CommRemoteModel commModel =new CommRemoteModel();

        DiscountMessage_HZ discountMessage = (DiscountMessage_HZ) serializableExtra;

        commModel.setContent(discountMessage.getDtContent());
        commModel.setImages(discountMessage.getDtImgs());
        commModel.setLocationInfo(discountMessage.getDtLocation());
        commModel.setTag(discountMessage.getDtTag());
        commModel.setMyUser(discountMessage.getMyUserId());
        commModel.setVisited(discountMessage.getDtVisitedNum());
        commModel.setWanted(discountMessage.getDtWantedNum());
        commModel.setObjectId(discountMessage.getObjectId());
        commModel.setMcreatedAt(discountMessage.getCreatedAt());
        commModel.setType(Contants.DATA_MODEL_DISCOUNT_MESSAGES);//属于折扣


        return commModel;
    }

    public ShareMessage_HZ formateDataCommremoteModel(CommRemoteModel commModel){

        ShareMessage_HZ shareMessageHz = new ShareMessage_HZ();
        shareMessageHz.setShContent(commModel.getContent());
        shareMessageHz.setShImgs(commModel.getImages());
        shareMessageHz.setShLocation(commModel.getLocationInfo());
        shareMessageHz.setShTag(commModel.getTag());
        shareMessageHz.setMyUserId(commModel.getMyUser());
        shareMessageHz.setShVisitedNum(commModel.getVisited());
        shareMessageHz.setShWantedNum(commModel.getWanted());
        shareMessageHz.setObjectId(commModel.getObjectId());
        shareMessageHz.setShCommNum(commModel.getComment());
//        commModel.setMcreatedAt(shareMessage.getCreatedAt());


        return shareMessageHz;
    }

    /**
     * 格式化数据
     * <p/>
     * 将Comment_HZ中的数据转换成CommRemoteModel
     *
     * @param comm
     */
    public static CommRemoteModel formateComments(Comment_HZ comm) {

        CommRemoteModel commRemoteModel = new CommRemoteModel();

        commRemoteModel.setContent(comm.getMessageId().getCommContent());
        commRemoteModel.setObjectId(comm.getMessageId().getObjectId());
        commRemoteModel.setMcreatedAt(comm.getMessageId().getCreatedAt());
        commRemoteModel.setSender(comm.getSenderId());
        commRemoteModel.setReceiver(comm.getReveicerId());

        commRemoteModel.setType(Contants.DATA_MODEL_BODY);//评论内容

        return commRemoteModel;
    }

    /**
     * 将远程数据库的数据格式化本地数据库格式
     * @param share
     * @return
     */
    public static ShareMessage formateShareMessage(ShareMessage_HZ share){
        ShareMessage shareMessage = new ShareMessage();
        shareMessage.setObjectId(share.getObjectId());
        shareMessage.setShImgs(String.valueOf(share.getShImgs()));
        shareMessage.setShContent(share.getShContent());
        shareMessage.setCreatedAt(share.getCreatedAt());
        shareMessage.setShLocation(share.getShLocation());
        shareMessage.setShVisitedNum(String.valueOf(share.getShVisitedNum()));
        shareMessage.setShCommNum(share.getShCommNum());
        shareMessage.setShWantedNum(String.valueOf(share.getShWantedNum()));
        shareMessage.setShTag(share.getShTag());
        shareMessage.setUpdatedAt(share.getUpdatedAt());

        return shareMessage;
    }

    /**
     * 将远程数据库的数据格式化本地数据库格式
     * @param myUser
     * @return
     */
    public static User formateUser(MyUser myUser){
        User u = new User();

        u.setCreatedAt(myUser.getCreatedAt());
        u.setUpdatedAt(myUser.getUpdatedAt());
        u.setAddress(myUser.getAddress());
        u.setGender(myUser.isGender());
        u.setAge(myUser.getAge());
        u.setQq(myUser.getQq());
        u.setAvatar(myUser.getAvatar());
        u.setSignture(myUser.getSignture());
        u.setEmail(myUser.getEmail());
        u.setMobilePhoneNumber(myUser.getMobilePhoneNumber());
        u.setNickName(myUser.getNickName());
        u.setMobilePhoneNumberVerified(myUser.getMobilePhoneNumberVerified());
        u.setEmailVerified(myUser.getEmailVerified());
        u.setObjectId(myUser.getObjectId());
        u.setAccessToken(myUser.getSessionToken());
        u.setUsername(myUser.getUsername());

        return u;
    }


    /**
     * 格式化数据
     *
     * @param shareMessage
     * @param context
     * @return
     */
    public static List<PictureInfo> formateImageInfoList(Context context,ShareMessage_HZ shareMessage) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();

        if (shareMessage.getShImgs() != null && shareMessage.getShImgs().size() > 0) {

            for (String uri : shareMessage.getShImgs()) {

                //大图uri、小图uri
                PictureInfo pictureInfo = new PictureInfo(NetworkUtils.getRealUrl(context, uri, false),
                        NetworkUtils.getRealUrl(context, uri));
                pictureInfoList.add(pictureInfo);
            }

        }
        return pictureInfoList;
    }

    /**
     * 格式化数据
     *
     * @param uriList
     * @param context
     * @return
     */
    public static List<PictureInfo> formateStringInfoList(Context context,List<String> uriList) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();

        if (uriList!= null && uriList.size() > 0) {

            for (String uri : uriList) {

                //大图uri、小图uri
                PictureInfo pictureInfo = new PictureInfo(NetworkUtils.getRealUrl(context, uri, false),
                        NetworkUtils.getRealUrl(context, uri));
                pictureInfoList.add(pictureInfo);
            }

        }
        return pictureInfoList;
    }

    /**
     * 格式化数据
     *
     * @param uriList
     * @return
     */
    public static List<PictureInfo> formateLocalImage(List<String> uriList) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();

        if (uriList!= null && uriList.size() > 0) {

            for (String uri : uriList) {

                //大图uri、小图uri
                PictureInfo pictureInfo = new PictureInfo(uri);
                pictureInfoList.add(pictureInfo);
            }

        }
        return pictureInfoList;
    }
}
