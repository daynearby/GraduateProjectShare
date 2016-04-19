package com.young.share.bmobPush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;

import com.young.share.MessageCenterActivity;
import com.young.share.R;

/**
 * 消息推送的通知栏显示样式
 *
 * Created by Nearby Yang on 2016-04-09.
 */
public class MessageNotification {

    private static final int NOTIFICATION_FLAG = 1;
    /**
     * 显示通知栏消息
     * @param context
     * @param content
     */
    public static void showReceiveComment(Context context,SpannableString content){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
                new Intent(context, MessageCenterActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API11之后才支持
        Notification notify2 = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                        // icon)
                .setTicker(content)// 设置在status
                        // bar上显示的提示文字
                .setContentTitle(context.getString(R.string.app_name))// 设置在下拉status
                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentText(content)// TextView中显示的详细内容
                .setContentIntent(pendingIntent2) // 关联PendingIntent
                .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .getNotification(); // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(NOTIFICATION_FLAG, notify2);
    }

}
