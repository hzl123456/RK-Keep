package cn.xmrk.rkandroid.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.config.RKConfigHelper;


/**
 * @author 思落羽 2014年4月15日 下午5:02:39
 */

public class CommonUtil {

    protected static Toast toast;


    /**
     * uri转化为图片路径
     *
     * @return
     */
    public static String uri2Path(Uri uri) {
        if (uri == null) {
            return null;
        }
        if (StringUtil.isEqualsString(uri.getScheme(), "file")) {
            return uri.getPath();
        }
        // 查询，返回cursor
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getAppContext().getContentResolver().query(uri, projection, null, null, null);
        try {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            cursor.close();
        }
    }

    /**
     * 小于10的数字前面加0
     * <p/>
     * 返回字符串
     **/
    public static final String changeOne2Two(int number) {
        return String.valueOf(number > 9 ? "" + number : "0" + number);
    }

    /**
     * 按钮长按进行提示
     **/
    public static final void setLongClick(View view, final String text) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CommonUtil.showToast(text);
                return false;
            }
        });
    }

    /**
     * 返回程序路径(内存卡存在则为内存卡上的)
     *
     * @return
     */
    public static final String getDir() {
        // 内存卡不存在返回app的文件夹，内存卡存在返回内存卡位置
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
            return getAppContext().getFilesDir().getAbsolutePath();
        } else {
            try {
                return getAppContext().getExternalCacheDir().getAbsolutePath();
            } catch (NullPointerException e) {
                return getAppContext().getFilesDir().getAbsolutePath();
            }
        }
    }

    /**
     * 返回内存卡路径
     *
     * @return
     */
    public static final String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 把对象里的字段通过Json解析以后再设置到 map 中
     *
     * @param map
     * @param obj
     */
    public static void putObj2Map(Map<String, String> map, Object obj) {
        JsonObject json = new Gson().toJsonTree(obj).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> set = json.entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
        Map.Entry<String, JsonElement> tempEntry;
        while (iterator.hasNext()) {
            tempEntry = iterator.next();
            map.put(tempEntry.getKey(), tempEntry.getValue().getAsString());
        }
    }

    /**
     * 获取视频文件的tup
     **/
    public static Bitmap getVidioBitmap(String filePath, int width, int height, int kind) {
        if (kind == 0) {
            kind = MediaStore.Images.Thumbnails.MICRO_KIND;
        }
        if (width == 0) {
            width =  RKApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.video_height);;
        }
        if (height == 0) {
            height = RKApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.video_height);
        }
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * dp转像素
     *
     * @param dpValue
     * @return
     */
    public static final int dip2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getAppContext().getResources().getDisplayMetrics());
    }

    public static Context getAppContext() {
        return RKConfigHelper.getInstance().getContext();
    }

    public static final void showToast(final int resId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(getAppContext(), resId, Toast.LENGTH_SHORT);
                } else {
                    toast.setText(resId);
                }
                toast.show();
            }
        });
    }

    public static final void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(getAppContext(), text, Toast.LENGTH_SHORT);
                } else {
                    toast.setText(text);
                }
                toast.show();
            }
        });
    }

    public static final void showSnackToast(String text, View view) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }


    /**
     * 精度
     *
     * @param source
     * @param digit  小数的位数
     * @return
     */
    public static final double digit(double source, int digit) {
        if (digit < 0) {
            return source;
        }
        int d = 1;
        for (int i = 0; i < digit; i++) {
            d *= 10;
        }
        // 小数转整数，抛弃一些不要的值
        long ls = (long) (source * d * 10);
        // 四舍五入
        ls += 5;
        ls /= 10;
        return (double) ls / d;
    }

    /**
     * 显示输入法
     *
     * @param v
     */
    public static void showKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

    /**
     * 隐藏输入法
     */
    public static void hideKeyboard(Activity activity) {
        hideKeyboard(activity.getWindow().getDecorView().getWindowToken());
    }

    /**
     * 隐藏输入法
     */
    public static void hideKeyboard(IBinder token) {
        InputMethodManager inputMethodManager = (InputMethodManager) getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 返回屏幕数据
     *
     * @return
     */
    public static Display getScreenDisplay() {
        WindowManager wm = (WindowManager) getAppContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay();
    }

    /**
     * @return 输入法为显示状态是返回 {@code true}
     */
    public static boolean isKeyboardShow(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    /**
     * 把 map 转化为 url 字符串
     *
     * @param params
     * @param encodeParams 使用 URLEncoder 转码参数
     * @return
     */
    public final static String generatyParams(Map<String, String> params, boolean encodeParams) {
        String paramStr = null;
        /* 参数存在拼接参数 */
        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder();
            String key;
            /* 迭代参数名进行拼接 */
            Iterator<String> pIterator = params.keySet().iterator();
            while (pIterator.hasNext()) {
                key = pIterator.next();
                sb.append(key);
                sb.append('=');
                if (params.get(key) == null) {
                    sb.append("");
                } else {
                    try {
                        if (encodeParams) {
                            sb.append(URLEncoder.encode(params.get(key), "utf-8"));
                        } else {
                            sb.append(params.get(key));
                        }
                    } catch (UnsupportedEncodingException e) {
                    }
                }
                sb.append('&');
            }
            paramStr = sb.substring(0, sb.length() - 1);
        } else {
            paramStr = null;
        }
        return paramStr;
    }

    public static Gson getGson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setVersion(1).create();
    }

    /**
     * 振动铃声
     */
    @SuppressWarnings("deprecation")
    public static void vibrateNotify() {
        Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(getAppContext(), RingtoneManager.TYPE_NOTIFICATION);
        // 静音模式无铃声
        if (notificationSound != null)
            RingtoneManager.getRingtone(getAppContext(), notificationSound).play();
        AudioManager am = (AudioManager) getAppContext().getSystemService(Context.AUDIO_SERVICE);
        boolean vibrate = am.shouldVibrate(AudioManager.VIBRATE_TYPE_NOTIFICATION);
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator())
                vibrator.vibrate(300);
        }
    }

    /**
     * @param link 要打开的链接
     * @throws ActivityNotFoundException 链接没有对应的打开方式(如在没有安装银联插件的时候打开 uppay://uppayservice/xxxx 链接)
     */
    public static void openLink(String link) throws ActivityNotFoundException {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(link));
        getAppContext().startActivity(intent);
    }

    /**
     * 拨打电话
     *
     * @param number
     */
    public static void callNumber(Activity activity, String number) {
        if (activity == null) {
            return;
        }
        // 用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        activity.startActivity(intent);
    }

    public static int generateId() {
        AtomicInteger sNextGeneratedId = new AtomicInteger(1);
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * 判断 current 是否在 snip 时间段内
     *
     * @param current
     * @param snip
     * @return
     */
    public static boolean isInTime(Date current, String snip) {
        // 时间段分割出开始与结束
        String[] _hm = snip.split("-");
        // 开始时间与结束时间
        Date _start, _end, _cur;
        SimpleDateFormat _tf = new SimpleDateFormat("kk:mm");
        try {
            _start = _tf.parse(_hm[0]);
            _end = _tf.parse(_hm[1]);
            // 此处只比较时间，利用转换时的精度损失去掉年月日，大家一起从1970年1月1号开始
            _cur = _tf.parse(_tf.format(current));
            if (_end.before(_start)) {
                // 结束时间早于开始时间，说明跨过一天，把日期添加一天，一天的毫秒值为 86400000
                _end.setTime(_end.getTime() + 86400000);
            }
            if (_cur.before(_start)) {
                // 当前时间早于开始时间，不在时间段内
                return false;
            } else return _end.after(_cur);
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isToday(long time) {
        if (System.currentTimeMillis() - time > 1000 * 60 * 60 * 24) {
            // 大于24小时
            return false;
        } else {
            // 小于24小时，判断是同“日”
            Calendar _ltCalendar = Calendar.getInstance();
            _ltCalendar.setTimeInMillis(time);
            Calendar _cc = Calendar.getInstance();
            // 同一天
            return _cc.get(Calendar.DATE) == _ltCalendar.get(Calendar.DATE);
        }
    }

    /**
     * 聊天根据时间戳返回时间显示
     *
     * @param millisecond
     * @return
     */
    public static final String getAffineTimestampForGroupChat(long millisecond) {
        long currentTime = System.currentTimeMillis();
        Date milliDate = new Date(millisecond);// 需要转换的时间
        if (milliDate.getDay() == (new Date(currentTime)).getDay()) {// 同一天的
            if (milliDate.getHours() < 12) {// 早上
                return new SimpleDateFormat("上午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            } else if (milliDate.getHours() == 12) {// 中午
                return new SimpleDateFormat("中午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            } else {
                return new SimpleDateFormat("下午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            }

        } else if (milliDate.getYear() == (new Date(currentTime)).getYear()) {// 同一年的
            if (milliDate.getHours() < 12) {// 早上
                return new SimpleDateFormat("MM月dd日 上午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            } else if (milliDate.getHours() == 12) {// 中午
                return new SimpleDateFormat("MM月dd日 中午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            } else {
                return new SimpleDateFormat("MM月dd日 下午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            }
        } else {
            if (milliDate.getHours() < 12) {// 早上
                return new SimpleDateFormat("yyyy年MM月dd日 上午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            } else if (milliDate.getHours() == 12) {// 中午
                return new SimpleDateFormat("yyyy年MM月dd日 中午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            } else {
                return new SimpleDateFormat("yyyy年MM月dd日 下午hh:mm", Locale.CHINESE).format(new Date(millisecond));
            }
        }
    }

    public static int getStateBarHeight() {
        int result = 0;
        int resourceId = getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getAppContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
