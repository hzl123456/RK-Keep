package cn.xmrk.rkandroid.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

/**
 * 跟手机设备相关的一些东西
 * @author 思落羽
 * 2014年9月4日 上午9:51:23
 *
 */
public class PhoneUtil {
	
	/**
	 * 返回IMEI码
	 * @return
	 */
   public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI =  tm.getDeviceId();// 设备唯一标识 IMEI
		if (IMEI == null || IMEI.equals("")) {
			IMEI = tm.getSubscriberId(); // IESI
		}
		if (IMEI == null || IMEI.equals("")) {
			// pad标识
			IMEI = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
		if (IMEI == null || IMEI.equals("")) {
			IMEI = tm.getLine1Number();
		}
		if (IMEI == null || IMEI.equals("")) {
			IMEI = "Unknow";
		}
		return IMEI;
	}
   
   /**
    * 获取本机手机号码(不一定能成功)
    * @return
    */
   public static String getPhoneNumber(Context context) {
	   TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	   String num = tm.getLine1Number();
	   return num;
   }
   
   /**
    * 返回屏幕数据
    * @return
    */
   @SuppressWarnings("deprecation")
   @SuppressLint("NewApi")
   public static Point getScreenDisplay(Context context) {
	   WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	   Point point = new Point();
	   if (Build.VERSION.SDK_INT > 13) {
		   wm.getDefaultDisplay().getSize(point);
	   } else {
		   Display display = wm.getDefaultDisplay();
		   point.x = display.getWidth();
		   point.y = display.getHeight();
	   }
	   return point;
   }
   
   /**
    * 内存卡可写
    * @return
    */
   public static boolean getSdcardWritable() {
	   return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
   }
   
   /*
    * 判断网络连接是否已开
    */
   public static boolean hasNetwork(Context context){
       boolean bisConnFlag=false;
       ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo network = conManager.getActiveNetworkInfo();
       if(network!=null){
           bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
       }
       return bisConnFlag;
   }

	/**
	 * 判断是否系统是否 >= 19(4.4)
	 * @return
	 */
	public static boolean isImmerse() {
		return Build.VERSION.SDK_INT >= 19;
	}

	public static boolean isLolipop() {
		return Build.VERSION.SDK_INT >= 23;
	}

	/**
	 * 获取状态栏高度
	 * @return
	 */
	public static int getStatusbarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * wifi网络为true
	 * @return
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager _cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo _ni = _cm.getActiveNetworkInfo();
		return _ni != null && _ni.getType() == ConnectivityManager.TYPE_WIFI;
	}

	public static boolean isEmulator() {
		return Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MANUFACTURER.contains("Genymotion")
				|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| "google_sdk".equals(Build.PRODUCT);
	}

	/**
	 * 返回内存卡路径
	 *
	 * @return
	 */
	public static final String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
   
}
