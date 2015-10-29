package com.young.bmobPush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.young.config.Contants;
import com.young.utils.LogUtils;

import cn.bmob.push.PushConstants;

/**
 * Created by Nearby Yang on 2015-10-15.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.logE("bmob", "onreceive ++++++++++++++   ");

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {

            LogUtils.logE("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            LogUtils.ta("接受到消息了" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));

            Intent intents = intent;
            intents.setAction(Contants.BMOB_PUSH_MESSAGES);
            context.sendBroadcast(intents);
        }
    }


}
