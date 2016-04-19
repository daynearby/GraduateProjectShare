package com.young.share.bmobPush;

import android.content.Context;

import com.young.share.config.Contants;
import com.young.share.model.MyBmobInstallation;
import com.young.share.model.MyUser;
import com.young.share.utils.LogUtils;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * bmob 的一些公共方法
 * Created by Nearby Yang on 2016-04-19.
 */
public class BmobPush {


    /**
     * 更新表中对应的uid
     */
    public static void updateinstallationId(final Context context, final MyUser cuser) {

        BmobQuery<MyBmobInstallation> query = new BmobQuery<>();
//        String cInstallationId = BmobInstallation.getInstallationId(context);

        query.addWhereEqualTo(Contants.PARAMS_INSTALLATIONID,BmobInstallation.getInstallationId(context));
        query.findObjects(context, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                if (object.size() > 0) {
                    MyBmobInstallation mbi = object.get(0);
                    mbi.setMyUser(cuser);
                    mbi.update(context, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            LogUtils.i("bmob 设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            LogUtils.i("bmob 设备信息更新失败:code = " + code + " message " + msg);
                        }
                    });
                } else {//设备还没登陆过，直接保存
                    MyBmobInstallation myBmobInstallation = new MyBmobInstallation(context);
                    myBmobInstallation.setMyUser(cuser);
                    myBmobInstallation.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            LogUtils.i("用户 绑定 installation 成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            LogUtils.i("用户 绑定 installation 失败");
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("查找installation id code = " + code + " msg = " + msg);
            }
        });
    }


}
