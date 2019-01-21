package rkkeep.keep.application;

import android.content.Intent;
import android.os.Build;

import com.baidu.mapapi.SDKInitializer;

import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.config.IRKConfig;
import rkkeep.keep.service.NoticeInfoService;
import rkkeep.keep.util.UserInfoUtil;

/**
 * Created by Au61 on 2016/4/26.
 */
public class KeepApplication extends RKApplication {


    @Override
    public IRKConfig getRKConfig() {

        return new IRKConfig() {
            @Override
            public boolean isDebug() {
                return false;
            }

            @Override
            public String getBaseUrl() {
                return null;//这里没有网络请求
            }

            @Override
            public boolean isLeakWatch() {
                return false;
            }

            @Override
            public int getNetTimeout() {
                return 0;
            }

            @Override
            public int getNetRetryCount() {
                return 0;
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UserInfoUtil.initDefaultUserInfo();
        //加载百度地图
        SDKInitializer.initialize(this);
        //启动后台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            startForegroundService(new Intent(this, NoticeInfoService.class));
        } else {
            startService(new Intent(this, NoticeInfoService.class));
        }
    }
}
