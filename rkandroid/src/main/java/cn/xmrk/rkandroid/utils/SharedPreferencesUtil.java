package cn.xmrk.rkandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Type;

public class SharedPreferencesUtil {
	
	private SharedPreferences mSharedPreferences;
	
	public SharedPreferencesUtil(Context context, String name) {
		mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}
	
	public void remove(String key) {
		mSharedPreferences.edit().remove(key).apply();
	}
	
	public void putInt(String key, int value) {
		mSharedPreferences.edit().putInt(key, value).apply();
	}
	
	public void putBoolean(String key, boolean value) {
		mSharedPreferences.edit().putBoolean(key, value).apply();
	}
	
	public void putString(String key, String value) {
		if (value == null) {
			remove(key);
		} else {
			mSharedPreferences.edit().putString(key, value).apply();
		}
	}
	
	public void putFloat(String key, float value) {
		
		mSharedPreferences.edit().putFloat(key, value).apply();
	}
	
	public void putLong(String key, Long value) {
		mSharedPreferences.edit().putLong(key, value).apply();
	}
	
	public void putDouble(String key, double value) {
		
		/* 默认没有保存 double 的功能，将double转为字符串后保存 */
		mSharedPreferences.edit().putString(key, String.valueOf(value)).apply();
	}

	/**
	 * 通过Gson转换把保存对象
	 * @param key
	 * @param obj
	 */
	public void putObjectWithGson(String key, Object obj) {
		if (obj == null) {
			remove(key);
		} else {
			mSharedPreferences.edit().putString(key, CommonUtil.getGson().toJson(obj)).apply();
		}
	}

	public double getDouble(String key, double defaultValue) {
		
		/* 默认不能取double，将字符串解析为doueble */
		String result = mSharedPreferences.getString(key, String.valueOf(defaultValue));
		return Double.valueOf(result);
	}
	
	public int getInt(String key, int defaultValue) {
		
		return mSharedPreferences.getInt(key, defaultValue);
	}
	
	public String getString(String key, String defaultValue) {
		
		return mSharedPreferences.getString(key, defaultValue);
	}
	
	public float getFloat(String key, float defaultValue) {
		
		return mSharedPreferences.getFloat(key, defaultValue);
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		
		return mSharedPreferences.getBoolean(key, defaultValue);
	}
	
	public long getLong(String key, long defaultValue) {
		
		return mSharedPreferences.getLong(key, defaultValue);
	}

	/**
	 * 获取数据，通过Gson进行转换
	 * @param key
	 * @param cls
	 * @return
	 */
	public Object getObjectWithGson(String key, Class cls) {
		String str = mSharedPreferences.getString(key, null);
		if (str == null) {
			return null;
		} else {
			return CommonUtil.getGson().fromJson(str, cls);
		}
	}

	/**
	 * 获取数据，通过Gson进行转换
	 * @param key
	 * @param type
	 * @return
	 */
	public Object getObjectWithGson(String key, Type type) {
		String str = mSharedPreferences.getString(key, null);
		if (str == null) {
			return null;
		} else {
			return CommonUtil.getGson().fromJson(str, type);
		}
	}


	
}
