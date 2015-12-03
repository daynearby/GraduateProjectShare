package com.young.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.young.config.Contants;
import com.young.model.BaseModel;
import com.young.model.ShareMessage_HZ;
import com.young.model.User;
import com.young.myInterface.GotoAsyncFunction;
import com.young.network.BmobApi;
import com.young.share.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * sharemessage 一些公共方法
 *
 * Created by Nearby Yang on 2015-12-03.
 */
public class LocationUtils {

    /**
     * 用户想去与否
     *
     * @param tv
     * @param userID
     */
    public static void leftDrawableWantoGO(TextView tv, List<String> userID,String currentUserID) {
        int drawableId;

        if (UserUtils.isHadCurrentUser(userID,currentUserID)) {
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
    public static void leftDrawableVisited(TextView tv, List<String> userID,String currentUserID) {
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
    public static void visit(Context ctx,User cuser,boolean hadGo, ShareMessage_HZ shareMessage, final View v) {
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

                v.setClickable(false);
//                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(false);
                LogUtils.logD(" wantToGo faile  erro code = " + i + " erro message = " + s);
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
    public static void wantToGo(Context ctx,User cuser,boolean hadWant, ShareMessage_HZ shareMessage, final View v) {

        JSONObject jsonObject = new JSONObject();//参数
        try {
            jsonObject.put("message", "1");
            jsonObject.put("userid", cuser.getObjectId());
            jsonObject.put("collectionid", shareMessage.getObjectId());

        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtils.logD("云端代码 添加参数失败 " + e.toString());
        }

        if (hadWant) {
//            strId = R.string.cancel_collect_success;
            shareMessage.getShWantedNum().remove(cuser.getObjectId());

//操作收藏表
            BmobApi.AsyncFunction(ctx, jsonObject, BmobApi.REMOVE_COLLECTION, new GotoAsyncFunction() {
                @Override
                public void onSuccess(Object object) {

                    BaseModel baseModel = (BaseModel) object;

                    if (baseModel.getCode() == BaseModel.SUCCESS) {
//                        mToast(R.string.operation_success);
                        LogUtils.logD("删除收藏记录 成功  data = " + baseModel.getData());
                    } else {

                        LogUtils.logD("删除收藏记录 失败  data = " + baseModel.getData());
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

        ((TextView) v).setText(String.valueOf(shareMessage.getShWantedNum() == null ?
                0 : shareMessage.getShWantedNum().size()));
        LocationUtils.leftDrawableWantoGO(((TextView) v), shareMessage.getShWantedNum(), cuser.getObjectId());

        shareMessage.update(ctx, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {

                v.setClickable(false);
//                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(false);
                LogUtils.logD(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }

}
