package com.young.share.utils;

import android.content.Context;

import com.young.share.config.Contants;
import com.young.share.model.Comment_HZ;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.MyUser;
import com.young.share.model.PictureInfo;
import com.young.share.model.RemoteModel;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.dbmodel.ShareMessage;
import com.young.share.model.dbmodel.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 格式化公用类
 * Created by Nearby Yang on 2015-12-04.
 */
public class DataFormateUtils {

    /**
     * 将只是图片地址的list转化成pictureInfo的list
     * 处理图片地址,添加缩略图地址与高清大图地址
     *
     * @param context
     * @param list
     * @return 图片缩率图与高清大图的链表
     */
    public static List<PictureInfo> formate2PictureInfo(Context context, List<String> list) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();
        if (list != null) {
            for (String url : list) {
                PictureInfo pictureInfo = new PictureInfo(NetworkUtils.getRealUrl(context, url, false), NetworkUtils.getRealUrl(context, url));
                pictureInfoList.add(pictureInfo);
            }
        }
        return pictureInfoList;
    }

    /**
     * 转化成本地的文件地址
     *
     * @param list 要转化的地址
     * @return
     */
    public static List<PictureInfo> formate2PictureInfo4Local( List<String> list) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();
        if (list != null) {
            for (String url : list) {
                PictureInfo pictureInfo = new PictureInfo(url);
                pictureInfoList.add(pictureInfo);
            }
        }
        return pictureInfoList;
    }

    /**
     * 批量将网址转化为缩略图地址
     * bmob中需要使用
     *
     * @param context
     * @param list
     * @return
     */
    public static List<String> thumbnailList(Context context, List<String> list) {
        List<String> urlList = new ArrayList<>();
        if (list != null) {
            for (String thumbnailUrl : list) {
                urlList.add(NetworkUtils.getRealUrl(context, thumbnailUrl));
//            LogUtils.d(" 真实的url = " + NetworkUtils.getRealUrl(context, thumbnailUrl));
            }
        }
        return urlList;
    }


    /**
     * 将sharemessage格式化成通用格式
     * <p/>
     * 处理数据
     *
     * @param shareMessage 要格式化的分享信息
     * @return 通用格式
     */
    public static RemoteModel formateDataDiscover(ShareMessage_HZ shareMessage) {
        RemoteModel commModel = new RemoteModel();

        commModel.setContent(shareMessage.getShContent());
        commModel.setImages(shareMessage.getShImgs());
        commModel.setLocationInfo(shareMessage.getShLocation());
        commModel.setTag(shareMessage.getShTag());
        commModel.setMyUser(shareMessage.getMyUserId());
        commModel.setVisited(shareMessage.getShVisitedNum());
        commModel.setWanted(shareMessage.getShWantedNum());
        commModel.setObjectId(shareMessage.getObjectId());
        commModel.setComment(shareMessage.getShCommNum());
        commModel.setCreatedAt(shareMessage.getCreatedAt());
        commModel.setVideo(shareMessage.getVideo());
        commModel.setVideoPreview(shareMessage.getVideoPreview());
        commModel.setType(Contants.DATA_MODEL_SHARE_MESSAGES);//属于分享信息Contants.DATA_MODEL_HEAD

        return commModel;
    }

    /**
     * 将sharemessage格式化成通用格式
     * <p/>
     * 处理数据
     *
     * @param remoteModel 要格式化的分享信息
     * @return 通用格式
     */
    public static ShareMessage_HZ formateDataRemoteModel(RemoteModel remoteModel) {
        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
        shareMessage.setShContent(remoteModel.getContent());

        shareMessage.setShImgs(remoteModel.getImages());
        shareMessage.setShLocation(remoteModel.getLocationInfo());
        shareMessage.setShTag(remoteModel.getTag());
        shareMessage.setMyUserId(remoteModel.getMyUser());
        shareMessage.setShVisitedNum(remoteModel.getVisited());
        shareMessage.setShWantedNum(remoteModel.getWanted());
        shareMessage.setObjectId(remoteModel.getObjectId());
        shareMessage.setShCommNum(remoteModel.getComment());
        shareMessage.setCreatedAt(remoteModel.getCreatedAt());
        shareMessage.setVideo(remoteModel.getVideo());
        shareMessage.setVideoPreview(remoteModel.getVideoPreview());

        return shareMessage;
    }


    /**
     * 商家优惠格式化通用的结构
     *
     * @param discountMessage 要格式化的商家优惠数据结构
     * @return 通用结构
     */
    public static RemoteModel formateDataDiscount(DiscountMessage_HZ discountMessage) {
        RemoteModel commModel = new RemoteModel();

        commModel.setContent(discountMessage.getDtContent());
        commModel.setImages(discountMessage.getDtImgs());
        commModel.setLocationInfo(discountMessage.getDtLocation());
        commModel.setTag(discountMessage.getDtTag());
        commModel.setMyUser(discountMessage.getMyUserId());
        commModel.setVisited(discountMessage.getDtVisitedNum());
        commModel.setWanted(discountMessage.getDtWantedNum());
        commModel.setObjectId(discountMessage.getObjectId());
        commModel.setCreatedAt(discountMessage.getCreatedAt());
        commModel.setType(Contants.DATA_MODEL_DISCOUNT_MESSAGES);//属于折扣


        return commModel;
    }

    /**
     * 通用的结构转化成商家优惠
     *
     * @param remoteModel 要格式化的通用数据结构
     * @return 折扣数据结构
     */
    public static DiscountMessage_HZ formateDataDiscount(RemoteModel remoteModel) {
        DiscountMessage_HZ discountMessage = new DiscountMessage_HZ();

        discountMessage.setDtContent(remoteModel.getContent());
        discountMessage.setDtImgs(remoteModel.getImages());
        discountMessage.setDtLocation(remoteModel.getLocationInfo());
        discountMessage.setDtTag(remoteModel.getTag());
        discountMessage.setMyUserId(remoteModel.getMyUser());
        discountMessage.setDtVisitedNum(remoteModel.getVisited());
        discountMessage.setDtWantedNum(remoteModel.getWanted());
        discountMessage.setObjectId(remoteModel.getObjectId());
        discountMessage.setCreatedAt(remoteModel.getCreatedAt());

        return discountMessage;
    }

    /**
     * 将通用的结构格式化成分享信息结构
     *
     * @param commModel 通用的格式
     * @return 分享信息的格式
     */
    public ShareMessage_HZ formateDataCommremoteModel(RemoteModel commModel) {

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


        return shareMessageHz;
    }

    /**
     * 格式化数据
     * <p/>
     * 将Comment_HZ中的数据转换成CommRemoteModel
     *
     * @param comm
     */
    public static RemoteModel formateComments(Comment_HZ comm) {

        RemoteModel remoteModel = new RemoteModel();

        remoteModel.setContent(comm.getMessageId().getCommContent());
        remoteModel.setObjectId(comm.getMessageId().getObjectId());
        remoteModel.setCreatedAt(comm.getMessageId().getCreatedAt());
        remoteModel.setSender(comm.getSenderId());
        remoteModel.setReceiver(comm.getReveicerId());

        remoteModel.setType(Contants.DATA_MODEL_BODY);//评论内容

        return remoteModel;
    }

    /**
     * 将远程数据库的数据格式化本地数据库格式
     *
     * @param share
     * @return
     */
    public static ShareMessage formateShareMessae(ShareMessage_HZ share) {
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
     *
     * @param myUser
     * @return
     */
    public static User formateUser(MyUser myUser) {
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
    public static List<PictureInfo> formateImageInfoList(Context context, ShareMessage_HZ shareMessage) {

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
    public static List<PictureInfo> formateStringInfoList(Context context, List<String> uriList) {

        List<PictureInfo> pictureInfoList = new ArrayList<>();

        if (uriList != null && uriList.size() > 0) {

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

        if (uriList != null && uriList.size() > 0) {

            for (String uri : uriList) {

                //大图uri、小图uri
                PictureInfo pictureInfo = new PictureInfo(uri);
                pictureInfoList.add(pictureInfo);
            }

        }
        return pictureInfoList;
    }
}
