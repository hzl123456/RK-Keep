package rkkeep.keep.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;

import java.util.List;

import cn.xmrk.rkandroid.application.RKApplication;
import rkkeep.keep.R;
import rkkeep.keep.activity.AddNoticeActivity;
import rkkeep.keep.db.NoticeInfoDbHelper;
import rkkeep.keep.help.LocationHelper;
import rkkeep.keep.pojo.NoticeInfo;

/**
 * Created by Au61 on 2016/5/9.
 */
public class NoticeInfoService extends Service {

    public NoticeInfoDbHelper mHelper;

    private LocationHelper mLocationHelper;

    private List<NoticeInfo> mData;

    private static NoticeInfoService mService;

    private boolean isLoading;

    /**
     * 已经有了一个循环了
     **/
    private boolean hasArun;

    /**
     * 获取唯一对象
     **/
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
        initHelper();
        initLocation();
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
                                NoticeInfo ntInfo = null;
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
                //为了与后续的群主进行区分，单聊的id都加上10000
                int NOTIFICATION_ID = (int) System.currentTimeMillis();
                RKApplication appliction = RKApplication.getInstance();
                Context context = appliction.getApplicationContext();
                CharSequence contentTitle = ntInfo.title;
                CharSequence contentText = ntInfo.content;

                // 点击后打开的项目 创建一个Intent
                Intent notificationIntent = new Intent(appliction, AddNoticeActivity.class);
                notificationIntent.putExtra("data", ntInfo);
                PendingIntent contentIntent = PendingIntent.getActivity(appliction, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                Bitmap bitmap = BitmapFactory.decodeResource(appliction.getResources(), R.drawable.ic_launcher);

                NotificationManager notificationManager = (NotificationManager) appliction.getSystemService(Context.NOTIFICATION_SERVICE);
                //创建通知
                Notification.Builder mBuilder = new Notification.Builder(context)
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent);

                Notification.BigPictureStyle btnPicStyle = new Notification.BigPictureStyle();
                btnPicStyle.setBigContentTitle(contentTitle);
                btnPicStyle.setSummaryText(contentText);

                if (ntInfo.infos != null && ntInfo.infos.size() > 0) {
                    Bitmap picBitamp = BitmapFactory.decodeFile(ntInfo.infos.get(0).imagePic);
                    btnPicStyle.bigPicture(picBitamp);
                }

                mBuilder.setStyle(btnPicStyle);

                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }).start();
    }
}
