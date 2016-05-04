package com.example.dawan.near02;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by dawan on 2016/2/17.
 */
public class MyReceiver extends BroadcastReceiver {

    private static int NOTIFY_ID = 1000;
    private static final String tag = "NotificationReceiver";
    private String notificationTitle;
    private String notificationMessage;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            String msg = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            try {
                JSONObject jsonObject = new JSONObject(msg);
                notificationMessage = jsonObject.getString("alert");
                Log.d("bmob", notificationMessage);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //创建通知
            sendNotification(context, notificationMessage);
        }
    }

    private void sendNotification(Context context, String message) {


        notificationTitle = "顺帮主提醒你：";
        Intent intent = new Intent(context, Start_Activity.class);
        intent.addCategory("COM.SBB");
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(context);
        Notification.Builder builder = notification.setContentIntent(pi).setTicker(notificationTitle).setSmallIcon(R.drawable.icon28)
                .setContentTitle(notificationTitle).setContentText(message).setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);
        Notification n = notification.build();

        notifyMgr.notify(1, n);
    }


}