package rkkeep.keep.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;

import java.util.List;

import cn.xmrk.rkandroid.application.RKApplication;
import rkkeep.keep.R;
import rkkeep.keep.activity.AddNoticeActivity;
import rkkeep.keep.db.NoticeInfoDbHelper;
import rkkeep.keep.help.LocationHelper;
import rkkeep.keep.pojo.NoticeInfo;

public class NoticeInfoService extends Service {

    //用于兼容android8.0的弹窗通知栏显示
    private NotificationManager notificationManager;
    private String notificationId = "channelId";
    private String notificationName = "channelName";


    public NoticeInfoDbHelper mHelper;

    private LocationHelper mLocationHelper;
    private List<NoticeInfo> mData;
    private boolean isLoading;
    private boolean hasArun; //已经有了一个循环了

    //获取唯一的service对象
    private static NoticeInfoService mService;

    public static NoticeInfoService getInstance() {
        return mService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        //初始化定位相关的一些东东
        initHelper();
        initLocation();
        //用于android8.0的前台服务使用
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(778899, getNotification());
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        return builder.build();
    }

    private void initLocation() {
        mLocationHelper = new LocationHelper();
        mLocationHelper.startLocation();
        mLocationHelper.setOnPoiGetListener(new LocationHelper.OnPoiGetListener() {
            @Override
            public void onGet(final BDLocation info) {
                if (hasArun) {
                    return;
                }
                hasArun = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (hasArun) {
                            if (isLoading) {
                                return;
                            }
                            try {
                                //休眠2秒钟吧
                                Thread.sleep(2000);
                                NoticeInfo ntInfo;
                                if (mData != null && mData.size() > 0) {
                                    for (int i = 0; i < mData.size(); i++) {
                                        ntInfo = mData.get(i);
                                        if (ntInfo.remindTime > 0 && System.currentTimeMillis() >= ntInfo.remindTime && ntInfo.noticeTimes == 0) {//当提醒次数为0，切当前时间大于提醒时间的时候进行提醒
                                            ntInfo.noticeTimes = 1;
                                            mHelper.saveNoticeInfo(ntInfo);
                                            showNotification(ntInfo);
                                        }
                                        if (ntInfo.addressInfo != null) {
                                            //求出两点经纬度的距离
                                            double length = Math.sqrt(Math.pow((info.getLatitude() - ntInfo.addressInfo.latitude), 2) + Math.pow((info.getLongitude() - ntInfo.addressInfo.longitude), 2));
                                            //经过多次测试得出的这么一个距离
                                            if (length <= 0.03) {
                                                ntInfo.noticeTimes = 1;
                                                mHelper.saveNoticeInfo(ntInfo);
                                                showNotification(ntInfo);
                                            }
                                        }
                                    }
                                }
                                //循环一次结束就刷新下数据然后继续操作
                                refreshNoticeInfo();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * 刷新需要提醒的数据
     **/
    public void refreshNoticeInfo() {
        isLoading = true;
        mData = mHelper.getTiXingNoticeInfoList();
        isLoading = false;
    }

    private void initHelper() {
        mHelper = new NoticeInfoDbHelper();
        refreshNoticeInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationHelper.stopLocation();
    }

    /**
     * 进行通知
     **/
    public void showNotification(final NoticeInfo ntInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int NOTIFICATION_ID = (int) System.currentTimeMillis();
                RKApplication appliction = RKApplication.getInstance();

                // 点击后打开的项目 创建一个Intent
                Intent notificationIntent = new Intent(appliction, AddNoticeActivity.class);
                notificationIntent.putExtra("data", ntInfo);
                PendingIntent contentIntent = PendingIntent.getActivity(appliction, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                Bitmap bitmap = BitmapFactory.decodeResource(appliction.getResources(), R.drawable.ic_launcher);

                //创建通知
                Notification.Builder mBuilder = new Notification.Builder(appliction)
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent);

                //android8.0以上使用
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mBuilder.setChannelId(notificationId);
                }
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }).start();
    }
}
