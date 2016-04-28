package cn.xmrk.rkandroid.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.xmrk.rkandroid.application.RKApplication;

/**
 *
 */
public class PackageUtil {

    /**
     * 返回软件版本号码
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return 0;
    }

    /**
     * 返回软件版本名称
     *
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    /**
     * 返回编译时间
     *
     * @return
     */
    public static String getBuildTime() {
        try {
            ApplicationInfo ai = RKApplication.getInstance().getApplicationInfo();
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            zf.close();
            return UnitUtil.getFullTimestamp(time);
        } catch (Exception e) {
            return "未知时间";
        }
    }

    /**
     * 返回程序路径(内存卡存在则为内存卡上的)
     *
     * @return
     */
    public static final String getDir(Context context) {
        // 内存卡不存在返回app的文件夹，内存卡存在返回内存卡位置
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
            return context.getFilesDir().getAbsolutePath();
        } else {
            try {
                return context.getExternalCacheDir().getAbsolutePath();
            } catch (NullPointerException e) {
                return context.getFilesDir().getAbsolutePath();
            }
        }
    }

}
