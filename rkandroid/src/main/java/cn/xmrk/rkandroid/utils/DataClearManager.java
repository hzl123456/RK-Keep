package cn.xmrk.rkandroid.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.apache.log4j.Logger;

import java.io.File;

import cn.xmrk.rkandroid.application.RKApplication;

/** * 本应用数据清除管理器 */  
public class DataClearManager {  
	
    /** 
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * 
     *  
     * @param context 
     */  
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());  
    }  
  
    /** 
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * 
     *  
     * @param context 
     */  
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File(context.getFilesDir().getPath()+ "/databases"));
    }  
  
    /** 
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * 
     *  
     * @param context 
     */  
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File( context.getFilesDir().getPath() + "/shared_prefs"));
    }  
  
    /** 
     * * 按名字清除本应用数据库 * * 
     *  
     * @param context 
     * @param dbName 
     */  
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);  
    }  
  
    /** 
     * * 清除/data/data/com.xxx.xxx/files下的内容 * * 
     *  
     * @param context 
     */  
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());  
    }  
  
    /** 
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) 
     *  
     * @param context 
     */  
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());  
        }  
    }  
    /** 
     * * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * 
     *  
     * @param filePath 
     * */  
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }  
  
    /** 
     * * 清除本应用所有的数据 * * 
     *  
     * @param
     * @param filepath 
     */  
    public static void cleanApplicationData(boolean cleanDatabases, boolean cleanSharedPreferences, String... filepath) {
//    	ImageLoader.getInstance().clearMemoryCache();
//    	ImageLoader.getInstance().clearDiskCache();
    	Context context = RKApplication.getInstance();
        cleanInternalCache(context);  
        cleanExternalCache(context);  
        if (cleanDatabases) {
        	cleanDatabases(context);  
        }
        if (cleanSharedPreferences) {
        	cleanSharedPreference(context);  
        }
        cleanFiles(context);  
        if (filepath == null) {  
            return;  
        }  
        for (String filePath : filepath) {
            cleanCustomCache(filePath);  
        }  
    }  
    
    /**
     * 计算应用缓存大小，不算数据库及SharedPreferences
     * @return
     */
    public static long getApplicationCacheData() {
    	Context context = RKApplication.getInstance();
    	long size = 0;
    	try {
			size += getFolderSize(context.getCacheDir());
			size += getFolderSize(context.getExternalCacheDir());
			return size;
		} catch (Exception e) {
			return 0l;
		}
    }
  
    /** 
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * 
     *  
     * @param directory 
     */  
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {  
            for (File item : directory.listFiles()) {
                item.delete();  
            }  
        }  
    }  
      
    // 获取文件  
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据  
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据  
    public static long getFolderSize(File file) throws Exception {
        long size = 0;  
        try {  
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {  
                // 如果下面还有文件  
                if (fileList[i].isDirectory()) {  
                    size = size + getFolderSize(fileList[i]);  
                } else {  
                    size = size + fileList[i].length();  
                }  
            }  
        } catch (Exception e) {
        	Logger.getLogger("DataClearManager").debug("[getFolderSize]", e);
        }  
        return size;  
    }  
      
    /** 
     * 删除指定目录下文件及目录 
     *  
     * @param deleteThisPath 
     * @param  filePath
     * @return 
     */  
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {  
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件  
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {  
                        deleteFolderFile(files[i].getAbsolutePath(), true);  
                    }  
                }  
                if (deleteThisPath) {  
                    if (!file.isDirectory()) {// 如果是文件，删除  
                        file.delete();  
                    } else {// 目录  
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除  
                            file.delete();  
                        }  
                    }  
                }  
            } catch (Exception e) {
            	Logger.getLogger("DataClearManager").debug("[deleteFolderFile]", e);
            }  
        }  
    }  
      
    public static String getCacheSize(File file) throws Exception {
        return UnitUtil.getSize((getFolderSize(file)), 2);
    }  
      
}  