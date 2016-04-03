package com.young.share.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.R;
import com.young.share.config.Contants;
import com.young.share.model.BaseModel;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.MyUser;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.interfaces.AsyncListener;
import com.young.share.network.BmobApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * sharemessage 一些公共方法
 * <p>
 * Created by Nearby Yang on 2015-12-03.
 */
public class LocationUtils {

    private static final String MESSAGE_TYPE = "message";//类型
    private static final String MESSAGE_TYPE_DISCOVER = "1";
    private static final String MESSAGE_TYPE_DISCOUNT = "2";
    private static final String USER_ID = "userid";
    private static final String COLLECTION_ID = "collectionid";


    /**
     * 用户想去与否
     *
     * @param tv
     * @param userID
     */
    public static void leftDrawableWantoGO(TextView tv, List<String> userID, String currentUserID) {
        int drawableId;

        if (UserUtils.isHadCurrentUser(userID, currentUserID)) {
            drawableId = R.drawable.icon_wantogo_light;
        } else {
            drawableId = R.drawable.icon_wantogo;
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);

    }

    /**
     * 用户去过与否
     *
     * @param tv
     * @param userID
     */
    public static void leftDrawableVisited(TextView tv, List<String> userID, String currentUserID) {
        int drawableId;

        if (UserUtils.isHadCurrentUser(userID, currentUserID)) {
            drawableId = R.drawable.icon_hadgo;
        } else {
            drawableId = R.drawable.icon_bottombar_hadgo;
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
    }

    /**
     * 去过的逻辑处理
     *
     * @param ctx
     * @param cuser
     * @param hadGo
     * @param shareMessage
     * @param v
     */
    public static void visit(Context ctx, MyUser cuser, boolean hadGo, ShareMessage_HZ shareMessage,
                             final View v,final Callback listener) {
        if (hadGo) {
            shareMessage.getShVisitedNum().remove(cuser.getObjectId());
        } else {
            shareMessage.getShVisitedNum().add(cuser.getObjectId());
        }


        ((TextView) v).setText(String.valueOf(shareMessage.getShVisitedNum() == null ?
                0 : shareMessage.getShVisitedNum().size()));

        LocationUtils.leftDrawableVisited(((TextView) v), shareMessage.getShVisitedNum(), cuser.getObjectId());

        shareMessage.update(ctx, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {

                v.setClickable(true);
                if (listener !=null){
                    listener.onSuccesss();
                }
//                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
                if (listener !=null){
                    listener.onFailure();
                }
                LogUtils.d(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }

    /**
     * 想去逻辑处理
     *
     * @param ctx
     * @param cuser
     * @param hadWant
     * @param shareMessage
     * @param v
     */
    public static void wantToGo(Context ctx, MyUser cuser, boolean hadWant, ShareMessage_HZ shareMessage,
                                final TextView v,final Callback listener) {

        JSONObject jsonObject = new JSONObject();//参数
        try {
            jsonObject.put(MESSAGE_TYPE, MESSAGE_TYPE_DISCOVER);//分享信息
            jsonObject.put(USER_ID, cuser.getObjectId());
            jsonObject.put(COLLECTION_ID, shareMessage.getObjectId());

        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtils.d("云端代码 添加参数失败 " + e.toString());
        }

        if (hadWant) {
//            strId = R.string.cancel_collect_success;
            shareMessage.getShWantedNum().remove(cuser.getObjectId());

//操作收藏表
            BmobApi.AsyncFunction(ctx, jsonObject, BmobApi.REMOVE_COLLECTION, new AsyncListener() {
                @Override
                public void onSuccess(Object object) {

                    BaseModel baseModel = (BaseModel) object;

                    if (baseModel.getCode() == BaseModel.SUCCESS) {
//                        mToast(R.string.operation_success);
                        LogUtils.d("删除收藏记录 成功  data = " + baseModel.getData());
                        if (listener !=null){
                            listener.onSuccesss();
                        }
                    } else {

                        LogUtils.d("删除收藏记录 失败  data = " + baseModel.getData());
                        if (listener !=null){
                            listener.onFailure();
                        }
                    }
                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });
        } else {
//            strId = R.string.collect_success;
            shareMessage.getShWantedNum().add(cuser.getObjectId());

            BmobApi.saveCollectionShareMessage(ctx, cuser, shareMessage, Contants.MESSAGE_TYPE_SHAREMESSAGE);
        }

        v.setText(String.valueOf(shareMessage.getShWantedNum() == null ?
                0 : shareMessage.getShWantedNum().size()));
        LocationUtils.leftDrawableWantoGO((v), shareMessage.getShWantedNum(), cuser.getObjectId());

        shareMessage.update(ctx, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {

                v.setClickable(true);
                if (listener !=null){
                    listener.onSuccesss();
                }
//                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
                if (listener !=null){
                    listener.onFailure();
                }
                LogUtils.d(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }


    /**
     * 关闭弹窗
     */
    public static void processDialog(Context ctx) {
        if (SVProgressHUD.isShowing(ctx)) {
            SVProgressHUD.dismiss(ctx);
        }
    }

    /**
     * 商家优惠 "想去"逻辑处理
     *
     * @param ctx
     * @param cuser
     * @param discountMessage
     * @param hadWant
     * @param v
     */
    public static void discountWanto(Context ctx, MyUser cuser, DiscountMessage_HZ discountMessage,
                                     boolean hadWant, final TextView v,final Callback listener) {

        JSONObject jsonObject = new JSONObject();//参数

        try {
            jsonObject.put(MESSAGE_TYPE, MESSAGE_TYPE_DISCOUNT);//商家优惠信息
            jsonObject.put(USER_ID, cuser.getObjectId());
            jsonObject.put(COLLECTION_ID, discountMessage.getObjectId());

        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtils.d("云端代码 添加参数失败 " + e.toString());
        }

        if (hadWant) {

            discountMessage.getDtWantedNum().remove(cuser.getObjectId());

//操作收藏表
            BmobApi.AsyncFunction(ctx, jsonObject, BmobApi.REMOVE_COLLECTION, new AsyncListener() {
                @Override
                public void onSuccess(Object object) {

                    BaseModel baseModel = (BaseModel) object;

                    if (baseModel.getCode() == BaseModel.SUCCESS) {
//                        mToast(R.string.operation_success);
                        LogUtils.d("删除收藏记录 成功  data = " + baseModel.getData());
                        if (listener !=null){
                            listener.onSuccesss();
                        }
                    } else {

                        LogUtils.d("删除收藏记录 失败  data = " + baseModel.getData());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {

                    LogUtils.d("删除收藏记录 请求 失败  code = " + code + " message = " + msg);
                    if (listener !=null){
                        listener.onFailure();
                    }
                }
            });

        } else {
//            strId = R.string.collect_success;
            if (discountMessage.getDtWantedNum() == null) {
                discountMessage.setDtWantedNum(new ArrayList<String>());
            }
            discountMessage.getDtWantedNum().add(cuser.getObjectId());

            BmobApi.saveCollectionShareMessage(ctx, cuser, discountMessage, Contants.MESSAGE_TYPE_DISCOUNT);
        }

        v.setText(String.valueOf(discountMessage.getDtWantedNum() == null ?
                0 : discountMessage.getDtWantedNum().size()));

        LocationUtils.leftDrawableWantoGO(v, discountMessage.getDtWantedNum(), cuser.getObjectId());

        discountMessage.update(ctx, discountMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {

                v.setClickable(true);
                if (listener !=null){
                    listener.onSuccesss();
                }
//                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {

                v.setClickable(true);
                if (listener !=null){
                    listener.onFailure();
                }
                LogUtils.d(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });
    }

    /**
     * 商家优惠 “去过”逻辑处理
     *
     * @param ctx
     * @param cuser
     * @param discountMessage
     * @param hadGo
     * @param v
     */
    public static void discountVisit(Context ctx, MyUser cuser, DiscountMessage_HZ discountMessage,
                                     boolean hadGo, final TextView v, final Callback listener) {

        if (hadGo) {
            discountMessage.getDtVisitedNum().remove(cuser.getObjectId());
        } else {
            if (discountMessage.getDtVisitedNum() == null) {
                discountMessage.setDtVisitedNum(new ArrayList<String>());
            }
            discountMessage.getDtVisitedNum().add(cuser.getObjectId());
        }


        v.setText(discountMessage.getDtVisitedNum() == null ?
                ctx.getString(R.string.hadgo) : String.valueOf(discountMessage.getDtVisitedNum().size()));

        LocationUtils.leftDrawableVisited(v, discountMessage.getDtVisitedNum(), cuser.getObjectId());

        discountMessage.update(ctx, discountMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {

                v.setClickable(true);
                if (listener !=null){
                    listener.onSuccesss();
                }

            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
                if (listener !=null){
                    listener.onFailure();
                }
                LogUtils.d(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });
    }

    /**
     * 启动activity
     *
     * @param ctx
     * @param bundle
     * @param clazz
     */
    public static void startActivity(Context ctx, Bundle bundle, Class clazz) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(ctx, clazz);
        ctx.startActivity(intent);
        ((Activity) ctx).overridePendingTransition(R.animator.activity_slid_right_in,
                R.animator.activity_slid_left_out);
    }


    /**
     * 发送广播刷新UI
     *
     * @param ctx
     * @param refreshType
     */
    public static void sendBordCast(Context ctx, int refreshType) {
        Intent intent = new Intent();
        intent.putExtra(Contants.REFRESH_TYPE, refreshType);
        intent.setAction(Contants.BORDCAST_REQUEST_REFRESH);
        ctx.sendBroadcast(intent);
    }

    /**
     * 操作成功失败的回调
     */
    public interface Callback {
        void onSuccesss();

        void onFailure();
    }
}
