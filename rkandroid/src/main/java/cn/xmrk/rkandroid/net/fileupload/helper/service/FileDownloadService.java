package cn.xmrk.rkandroid.net.fileupload.helper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.xmrk.rkandroid.net.fileupload.helper.DownloadManager;
import cn.xmrk.rkandroid.net.fileupload.helper.DownloadTask;
import cn.xmrk.rkandroid.net.listener.DownloadTaskListener;

/**
 * Created by Au61 on 2016/1/7.
 */
public class FileDownloadService extends Service{
    public static final String EXTRA_ENTITY = "extraEntity";
    public static final String EXTRA_URL = "extraUrl";
    public static final String EXTRA_FILE_PATH = "extraFilePath";
    public static final String EXTRA_ACTION = "extraAct";
    public static final int ACT_STOP_DOWNLOAD = 1;
    public static final int ACT_UPLOAD = 0;
    public static final int ACT_DOWNLOAD = 3;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int act = intent.getIntExtra(EXTRA_ACTION, ACT_UPLOAD);
            switch (act) {
                case ACT_DOWNLOAD:
                   /* String url = intent.getStringExtra(EXTRA_URL);
                    String filePath = intent.getStringExtra(EXTRA_FILE_PATH);
                    download(url,filePath);*/
                    download();
                    break;
                case ACT_STOP_DOWNLOAD:
                    String path = intent.getStringExtra(EXTRA_FILE_PATH);
                    stopDownload(path);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void stopDownload(String filePath){
        downloadManager.cancel(filePath);
    }

//    public static final void download(Context context, String url,String filePath, DownloadTaskListener listener) {
//        if (filePath == null||url==null) {
//            return;
//        }
//        setDownloadListener(listener);
//        Intent downloadIntent = new Intent(context, FileDownloadService.class);
//        downloadIntent.putExtra(EXTRA_ACTION, ACT_DOWNLOAD);
//        downloadIntent.putExtra(EXTRA_URL, url);
//        downloadIntent.putExtra(EXTRA_FILE_PATH, filePath);
//        context.startService(downloadIntent);
//    }
    static DownloadTask mdownloadTask;
    public static final void download(Context context, DownloadTask downloadTask, DownloadTaskListener listener) {
        if (downloadTask == null) {
            return;
        }
        mdownloadTask=downloadTask;
        setDownloadListener(listener);
        Intent downloadIntent = new Intent(context, FileDownloadService.class);
        downloadIntent.putExtra(EXTRA_ACTION, ACT_DOWNLOAD);
        context.startService(downloadIntent);
    }

    private static void setDownloadListener(DownloadTaskListener mdownloadListener){
        downloadListener = mdownloadListener;
    }
    public static final void stopDownload(Context context,String filePath) {
        Intent stopIntent = new Intent(context, FileDownloadService.class);
        stopIntent.putExtra(EXTRA_ACTION, ACT_STOP_DOWNLOAD);
        stopIntent.putExtra(EXTRA_FILE_PATH, filePath);
        context.startService(stopIntent);
    }

    static DownloadTaskListener downloadListener ;
    DownloadManager downloadManager = DownloadManager.getInstance(this);
    private void download(final String url, final String filePath) {
        DownloadTask downloadTask = new DownloadTask(this,url,filePath);
        downloadManager.addDownloadTask(downloadTask,downloadListener);
    }

    private void download() {
        downloadManager.addDownloadTask(mdownloadTask,downloadListener);
    }

    /**
     * 添加多个监听器，
     * @param mdownloadListener  监听
     */
    public static void addDownloadListener( DownloadTask downloadTask,DownloadTaskListener mdownloadListener){
        if(downloadTask!=null){
        downloadTask.addDownloadListener(mdownloadListener);
        }
    }

    /**
     *移除多个监听器，
     * @param mdownloadListener  监听
     */
    public static void removeDownloadListener(DownloadTask downloadTask,DownloadTaskListener mdownloadListener){
        if(downloadTask!=null){
            downloadTask.removeDownloadListener(mdownloadListener);
        }
    }

}
