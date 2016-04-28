package cn.xmrk.rkandroid.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.application.RKApplication;

/**
 * 创建日期： 2016/1/8.
 */
public class NotificationUtil {

    public static void cancelAll() {
        getNotificationManager().cancelAll();
    }

    public static void cancel(int noticeId) {
        getNotificationManager().cancel(noticeId);
    }

    public static void cancel(String tag, int noticeId) {
        getNotificationManager().cancel(tag, noticeId);
    }

    public static void notify(int noticeId, Notification notification) {
        getNotificationManager().notify(noticeId, notification);
    }

    public static void notify(String tag, int noticeId, Notification notification) {
        getNotificationManager().notify(noticeId, notification);
    }

    /**
     * 发出一条id为-1的notification，样式是默认样式
     * @param contentTitle
     * @param contentInfo
     * @param ticker
     * @param pi 点击后的响应Intent
     */
    public static void notifyDefault(CharSequence contentTitle, CharSequence contentInfo, CharSequence ticker, PendingIntent pi) {
        Notification _notification = new NotificationCompat.Builder(RKApplication.getInstance())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setTicker(ticker)
                .setContentTitle(contentTitle)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentText(contentInfo)
                .setContentIntent(pi)
                .build();
        notify(-1, _notification);
    }

    private static NotificationManagerCompat getNotificationManager() {
        return NotificationManagerCompat.from(RKApplication.getInstance());
    }
}
