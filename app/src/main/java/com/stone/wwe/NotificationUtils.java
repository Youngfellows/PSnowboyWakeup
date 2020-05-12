package com.stone.wwe;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

public class NotificationUtils {

    /**
     * 设置为前台服务
     */
    public static void startForeground(Service context) {
        String CHANNEL_ID = context.getPackageName();//Channel ID
        String CHANNEL_NAME = "wwe Channel";//Channel 名称
        //设置点击跳转页面
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification notification = null;
        NotificationChannel notificationChannel = null;
        Notification.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
            notificationChannel.setLightColor(Color.RED);//小圆点颜色
            notificationChannel.setShowBadge(true);//是否在久按桌面图标时显示此渠道的通知
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(context.getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context.getApplicationContext());
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("wwe")
                //                .setContentTitle(Utils.getAppName(this))
                .setContentText("wwe正在运行中")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.flags |= Notification.FLAG_NO_CLEAR;
            context.startForeground(1, notification);
        }
    }

}
