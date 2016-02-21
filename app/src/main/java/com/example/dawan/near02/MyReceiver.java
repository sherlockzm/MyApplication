package com.example.dawan.near02;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.bmob.push.PushConstants;

/**
 * Created by dawan on 2016/2/17.
 */
public class MyReceiver extends BroadcastReceiver {

    private static int NOTIFY_ID = 1000;
    private static final String tag = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            String msg = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);

            //创建通知
            sendNotification(context, msg);
        }
    }
    private void sendNotification(Context context, String message) {
        //【1】获取Notification 管理器的参考

        //【2】设置通知。PendingIntent表示延后触发，是在用户下来状态栏并点击通知时触发，触发时PendingIntent发送intent，本例为打开浏览器到指定页面。
        Intent intent = new Intent(context,MainActivity.class);
        intent.addCategory("COM.SBB");
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(context);
        Notification.Builder builder = notification.setContentIntent(pi).setAutoCancel(true).setTicker("This is test!").setSmallIcon(R.drawable.abc)
                .setContentTitle("this is title").setContentText(message).setWhen(System.currentTimeMillis())
                .setOngoing(true).setVibrate(new long[]{0, 1000, 1000, 1000});
        Notification n = notification.build();

        notifyMgr.notify(1,n);
    }

}