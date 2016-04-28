package cn.xmrk.rkandroid.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.application.RKApplication;

/**
 * 各种测量单位相关的工具
 */
public class UnitUtil {

    /**
     * 分：时格式化，假如被格式成：xx"x'
     *
     * @param format
     * @param milliseconds
     * @return
     */
    public static final String msFormat(String format, long milliseconds) {
        int seconds = (int) (milliseconds / 1000);
        if (seconds < 60) {
            return String.format(format, "00", StringUtil.getBit(seconds, 2));
        } else {
            return String.format(format, StringUtil.getBit(seconds / 60, 2), StringUtil.getBit(seconds % 60, 2));
        }
    }

    /**
     * 返回 xx"xx' 表示时长
     *
     * @param millisecond
     * @return
     */
    public static String getTimeStr(int millisecond) {
        millisecond /= 1000;
        StringBuilder sb = new StringBuilder();
        int minute = millisecond / 60;
        if (minute > 0) {
            sb.append(minute).append('\"');
        }
        sb.append(millisecond % 60).append('\'');
        return sb.toString();
    }

    /**
     * dp转像素
     *
     * @param dpValue
     * @return
     */
    public static final int dip2px(float dpValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * 返回较友好的时间戳
     *
     * @param millisecond
     * @return
     */
    public static final String getTimestamp(long millisecond) {

        if (DateUtils.isToday(millisecond)) {
            return new SimpleDateFormat("HH:mm:ss", Locale.CHINESE).format(new Date(millisecond));
        } else {
            return getFullTimestamp(millisecond);
        }
    }

    public static final long getNowTimePhp() {
        return System.currentTimeMillis() / 1000;
    }

    public static final long getNowTimeAndroid() {
        return System.currentTimeMillis();
    }

    /**
     * php的时间转完整时间戳
     *
     * @return
     */
    public static final String getFullTimestrampPHP(long millisecond) {
        return getFullTimestamp(millisecond * 1000);
    }

    /**
     * 返回完整时间戳
     *
     * @return
     */
    public static final String getFullTimestamp(long millisecond) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(new Date(millisecond));
    }

    /**
     * 比 getTimestamp 更亲和的返回时间戳
     *
     * @param millisecond
     * @return
     */
    public static final String getAffineTimestamp(long millisecond) {

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - millisecond;
        if (deltaTime < 120000l) { // 两分钟内为 刚刚
            return RKApplication.getInstance().getString(R.string.time_just_now);
        } else if (deltaTime < 3600000l) { // 一小时内的均显示 %1$d 分钟前
            return String.format(RKApplication.getInstance().getString(R.string.time_minute_ago), deltaTime / 60000);
        } else if (deltaTime < 86400000l) { // 一天内显示 %1$d 小时前
            return String.format(RKApplication.getInstance().getString(R.string.time_hour_ago), deltaTime / 3600000);
        } else if (deltaTime < 259200000) { // 三天内显示 %1$d 天前
            return String.format(RKApplication.getInstance().getString(R.string.time_day_ago), deltaTime / 86400000l);
        } else {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE).format(new Date(millisecond));
        }
    }

    /**
     * 友好方式返回数据大小
     *
     * @param byteSize
     * @param digit    小数的位数
     * @return
     */
    public static final String getSize(long byteSize, int digit) {

        double size = byteSize;
        String[] unit = new String[]{"Byte", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB"};
        int pos = 0;
        for (; size > 1024; pos++) {
            size /= 1024;
        }
        return CommonUtil.digit(size, digit) + unit[pos];
    }

    /**
     * 返回距离字符串
     *
     * @param mile
     * @return
     */
    public static String getDistanceStr(double mile) {
        return mile > 1000 ? CommonUtil.digit(mile / 1000, 2) + "km" : (int) mile + "m";
    }

    /**
     * sp转换像素
     * @param var0
     * @param var1
     * @return
     */
    public static int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 * var2 + 0.5F);
    }

}
