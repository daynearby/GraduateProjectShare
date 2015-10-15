package com.young.bmobPush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import cn.bmob.push.PushConstants;

/**
 * Created by Nearby Yang on 2015-10-15.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("bmob", "onreceive ++++++++++++++   ");

        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            Toast.makeText(context,"接受到消息了"+intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING),Toast.LENGTH_LONG).show();
        }
    }
}
