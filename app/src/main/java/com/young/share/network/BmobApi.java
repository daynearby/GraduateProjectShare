package com.young.share.network;

import android.content.Context;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.ThumbnailListener;
import com.bmob.btp.callback.UploadBatchListener;
import com.google.gson.Gson;
import com.young.share.R;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.GoToUploadImages;
import com.young.share.model.BaseModel;
import com.young.share.model.Collection_HZ;
import com.young.share.model.Comment_HZ;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.Message_HZ;
import com.young.share.model.MyUser;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 网络请求 基类
 * Created by Nearby Yang on 2015-10-17.
 */
public class BmobApi {


    //找回密码
    public static final String FINDPWD = "FindPwd";
    public static final String GET_RECENTLY_SHAREMESSAGES = "GetRecentlyShareMessages";//获取最新的的前50条记录
    public static final String REMOVE_COLLECTION = "RemoveCollection";//移除收藏
    public static final String PUSH_MESSAGE_BY_UID = "PushMessageByUID";//发送信息
    public static final String GET_MESSAGE_COMMENTS = "GetMessageComments";//获取全部的评论数据
    public static final String GET_SHARE_RECROD = "GetShareRecord";//获取全部分享记录
    public static final String GET_RECOLLECTION_RECORD = "GetRecollectionRecord";//获取全部收藏记录
    public static final String GET_NEW_MESSAGES = "GetNewMessages";//获取全部消息记录
    public static final String GET_RECENTLY_DICOUNT = "GetRecentlyDicount";//获取全部商家优惠
    public static final String GET_RANK_DATA = "GetRankData";//获取排行榜数据
    public static final String GET_HEAT_MESSAGES = "GetHeatMessages";//获取排行榜热门数据
    public static final String GET_USER_AVATAR = "GetUserAvatar";//获取用户头像的地址，还需要进行编码
    public static final String GET_AD = "GetAd";//获取广告信息
    public static final String GET_USER_RECORD = "GetUserRecord";//获取用户的全部发送记录


    private static Gson gson = new Gson();

    /**
     * 云端代码
     *
     * @param ctx
     * @param params        JSONObject 请求参数
     * @param funcationName 方法名
     */
    public static void AsyncFunction(final Context ctx, JSONObject params, String funcationName,
                                     final Class clazz, final AsyncListener listener) {

        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();

//第一个参数是上下文对象，
// 第二个参数是云端代码的方法名称，
// 第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），
// 第四个参数是回调类
        ace.callEndpoint(ctx, funcationName, params,
                new CloudCodeListener() {
                    @Override
                    public void onSuccess(Object object) {
/*在json解析出现错误的时候，这里会显示两次*/
                        LogUtils.i("data clazz = " + clazz);
                        LogUtils.i("data string =　" + object.toString());

                        Object obj = gson.fromJson(object.toString(), clazz);
//                        LogUtils.i("返回JSON数据:" + obj.toString());
                        listener.onSuccess(gson.fromJson(object.toString(), clazz));

                    }

                    @Override
                    public void onFailure(int code, String msg) {

                        listener.onFailure(code, msg);
                        mToast(ctx, R.string.network_erro);

                        LogUtils.i("访问云端方法失败:" + msg);
                    }
                });
    }

    /**
     * 没有更多返回结果
     * 云端代码
     *
     * @param ctx
     * @param params        JSONObject 请求参数
     * @param funcationName 方法名
     */
    public static void AsyncFunction(Context ctx, JSONObject params, String funcationName,
                                     final AsyncListener listener) {
        AsyncFunction(ctx, params, funcationName, BaseModel.class
                , listener);

//第一个参数是上下文对象，
// 第二个参数是云端代码的方法名称，
// 第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），
// 第四个参数是回调类
    }


    /**
     * Bmob批量上传文件
     * 先修正文件地址，再进行操作
     *
     * @param file     文件地址，带file：//的
     * @param type     上传文件的类型。Contants.FILE_TYPE_SIGNAL 单个文件，对上传的地址不做修改。
     *                 Contants.IMAGE_TYPE_SHARE多个问及按，这里作为上传图片，对文件地址进行修改
     * @param context
     * @param listener 回调
     */
    public static void UploadFiles(final Context context, String[] file, final int type, final GoToUploadImages listener) {
//
//        String[] files = new String[file.length];
//
//        switch (type) {
//            case Contants.FILE_TYPE_MULTI://文件的路径
//                files = file;
//                break;
//            case Contants.IMAGE_TYPE_SHARE://分享信息的照片
//                for (int i = 0; i < file.length; i++) {
//                    files[i] = file[i].substring(Contants.FILE_HEAD.length(), file[i].length());
//                }
//                break;
//        }


        BmobProFile.getInstance(context).uploadBatch(file, new UploadBatchListener() {

            @Override
            public void onSuccess(boolean isFinish, String[] fileNames, String[] urls, BmobFile[] files) {

                if (isFinish) {
                    if (listener != null) {
                        listener.Result(urls, files);
                    }


                        for (String filename : fileNames) {
                            setThumbnail(context, filename);


                    }

                }

                // isFinish ：批量上传是否完成
                // fileNames：文件名数组
                // urls        : url：文件地址数组
                // files     : BmobFile文件数组，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
//                注：若上传的是图片，url(s)并不能直接在浏览器查看（会出现404错误），需要经过`URL签名`得到真正的可访问的URL地址,当然，`V3.4.1`版本可直接从BmobFile中获得可访问的文件地址。
                LogUtils.i("uploadBatch isFinish = " + isFinish + " imgUrl = " + urls.toString());

            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                // curIndex    :表示当前第几个文件正在上传
                // curPercent  :表示当前上传文件的进度值（百分比）
                // total       :表示总的上传文件数
                // totalPercent:表示总的上传进度（百分比）
                LogUtils.i("uploadBatch", "onProgress :" + curIndex + "---" + curPercent + "---" + total + "----" + totalPercent);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                if (listener != null) {
                    listener.onError(statuscode, errormsg);
                }
                LogUtils.i("uploadBatch", "批量上传出错：" + statuscode + "--" + errormsg);
            }
        });
    }


    private static void setThumbnail(Context context, String fileName) {
        BmobProFile.getInstance(context).submitThumnailTask(fileName, Contants.MODEL_ID, new ThumbnailListener() {

            @Override
            public void onSuccess(String thumbnailName, String thumbnailUrl) {
                //此处得到的缩略图地址（thumbnailUrl）不一定能够请求的到，此方法为异步方法
                LogUtils.i("thumbnailName = " + thumbnailName + " thumbnailUrl = " + thumbnailUrl);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                LogUtils.e("setThumbnail faile  code = " + statuscode + "  errormsg = " + errormsg);
            }
        });
    }

    /**
     * 收藏分享信息
     *
     * @param ctx
     * @param myUser
     * @param bmobObject
     * @param messageType Contants.MESSAGE_TYPE_SHAREMESSAGE://分享信息  Contants.MESSAGE_TYPE_DISCOUNT://商家优惠
     */
    public static void saveCollectionShareMessage(Context ctx, MyUser myUser, BmobObject bmobObject, int messageType) {

        Collection_HZ collection = new Collection_HZ();
        collection.setCollUserId(myUser);//current myUser

        switch (messageType) {
            case Contants.MESSAGE_TYPE_SHAREMESSAGE://分享信息
                ShareMessage_HZ shareMessage = (ShareMessage_HZ) bmobObject;
                collection.setShUserId(shareMessage.getMyUserId());
                collection.setShMsgId(shareMessage);

                break;

            case Contants.MESSAGE_TYPE_DISCOUNT://商家优惠
                DiscountMessage_HZ discountMessage = (DiscountMessage_HZ) bmobObject;
                collection.setShUserId(discountMessage.getMyUserId());
                collection.setDtMsgId(discountMessage);
                break;
        }

        collection.save(ctx);

    }


    /**
     * 发送信息
     * 将信息保存到远程数据库
     * 将信息推送给指定的人
     *
     * @param context
     * @param senderId
     * @param receiverId
     * @param content
     * @param shareId
     * @param sendMessageCall
     */
    public static void sendMessage(final Context context, String senderId, final String receiverId,
                                   final String content, String shareId, final SendMessageCallback sendMessageCall) {
        MyUser sender = new MyUser();
        MyUser receiver = new MyUser();
        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
        final Message_HZ message = new Message_HZ();
        final Comment_HZ comment = new Comment_HZ();

//        1.将信息保存到远程数据库

        sender.setObjectId(senderId);
        receiver.setObjectId(receiverId);
        shareMessage.setObjectId(shareId);

        comment.setReveicerId(receiver);
        comment.setSenderId(sender);
        comment.setShMsgId(shareMessage);

        message.setCommContent(content);
        message.setRead(false);
        LogUtils.e("senderId = " + senderId + " receiverId = " + receiverId);

        message.save(context, new SaveListener() {
                    @Override
                    public void onSuccess() {

                        comment.setMessageId(message);
                        comment.save(context, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                LogUtils.d("save message success");
                                //保存信息成功。开始发送信息
                                sendMessage(context, receiverId, content, sendMessageCall);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                mToast(context, R.string.network_erro);
                            }
                        });

            }

            @Override
            public void onFailure(int i, String s) {
                mToast(context, R.string.network_erro);
            }
        });


//                2.将信息推送给指定的人


    }

    /**
     * 使用云端代码。发送信息
     *
     * @param ctx
     * @param receiverId
     * @param content
     */
    private static void sendMessage(final Context ctx, String receiverId,
                                    String content, final SendMessageCallback sendMessageCall) {
        JSONObject params = new JSONObject();
        try {
            params.put(Contants.PARAM_USERID, receiverId);
            params.put(Contants.PARAMS_CONTENT, content);
        } catch (JSONException e) {
            LogUtils.i("send messa add params fail " + e.toString());
        }
        LogUtils.e(" PARAM_USERID = " + receiverId
                + " PARAMS_CONTENT = " + content);

        AsyncFunction(ctx, params, PUSH_MESSAGE_BY_UID, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                BaseModel baseModel = (BaseModel) object;
                LogUtils.d("send message success ");
                if (baseModel.getCode() == BaseModel.SUCCESS) {
//刷新前台数据
                    if (sendMessageCall != null) {
                        sendMessageCall.onSuccessReflesh();
                    }

                } else {
                    mToast(ctx, "推送消息失败！消息会送达");
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.i("send message faile code = " + code + " message = " + msg);
            }
        });

    }

    /**
     * toast
     *
     * @param context
     * @param strId   提示语
     */
    private static void mToast(Context context, int strId) {
        Toast.makeText(context, strId, Toast.LENGTH_LONG).show();
    }
    /**
     * toast
     *
     * @param context
     * @param str   提示语
     */
    private static void mToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    /**
     * 发送信息成功的回调
     */
    public interface SendMessageCallback {
        void onSuccessReflesh();
    }


}


