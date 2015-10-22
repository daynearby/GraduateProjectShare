package com.young.bmobPush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.young.utils.LogUtils;

import cn.bmob.push.PushConstants;

/**
 * Created by Nearby Yang on 2015-10-15.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {

    public static final String BMOB_PUSH_MESSAGES = "Bmob_Push_Messages";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.logE("bmob", "onreceive ++++++++++++++   ");

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {

            LogUtils.logE("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            LogUtils.ta("接受到消息了" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));

            intent.setAction(BMOB_PUSH_MESSAGES);
            context.sendBroadcast(intent);
        }
    }


}
