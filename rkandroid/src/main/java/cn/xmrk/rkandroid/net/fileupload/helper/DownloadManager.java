package cn.xmrk.rkandroid.net.fileupload.helper;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cn.xmrk.rkandroid.net.fileupload.helper.filedb.FileDbModel;
import cn.xmrk.rkandroid.net.fileupload.helper.filedb.FileDbUtil;
import cn.xmrk.rkandroid.net.listener.DownloadTaskListener;

/**
 * Created by dzc on 15/11/21.
 */
public class DownloadManager {
    private static final String TAG = "DownloadManager";
    private Context context;
    private static DownloadManager downloadManager;
    private int mPoolSize = 5;
    private ExecutorService executorService;
    private Map<String,Future> futureMap;
    private OkHttpClient client;
    private FileDbUtil fileDbUtil;

    public Map<String, DownloadTask> getCurrentTaskList() {
        return currentTaskList;
    }

    private Map<String,DownloadTask> currentTaskList = new HashMap<>();
    public void init(){
        fileDbUtil = new FileDbUtil(context);
        executorService = Executors.newFixedThreadPool(mPoolSize);
        futureMap = new HashMap<>();
        client = new OkHttpClient();
    }

    private DownloadManager() {
        init();
    }

    private DownloadManager(Context context) {
        this.context = context;
        init();
    }

    public static DownloadManager getInstance(Context context){
        if(downloadManager==null){
            downloadManager = new DownloadManager(context);
        }
        return downloadManager;
    }

    public void addDownloadTask(DownloadTask task,DownloadTaskListener listenr){
        if(null!=currentTaskList.get(task.getFilePath())){
            Log.d(TAG,"task already exist");
            if(listenr!=null){
                listenr.onError(DownloadTaskListener.DOWNLOAD_ERROR_IS_ALREAY_DOWNLOAD,"文件已经正在下载中");
            }
            return ;
        }
        currentTaskList.put(task.getFilePath(), task);
        task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PREPARE);
        task.setHttpClient(client);
        task.addDownloadListener(listenr);
        Future future =  executorService.submit(task);
        futureMap.put(task.getFilePath(),future);
    }

    public void addDownloadListener(DownloadTask task,DownloadTaskListener listener){
        task.addDownloadListener(listener);
    }

    public void removeDownloadListener(DownloadTask task,DownloadTaskListener listener){
        task.removeDownloadListener(listener);
    }
    public void removeDownloadTask(DownloadTask task){
        currentTaskList.remove(task.getFilePath());
    }

    public void addDownloadTask(DownloadTask task){
        addDownloadTask(task, null);
    }

    public void cancel(String filePath){
        DownloadTask downloadTask = currentTaskList.get(filePath);
        if(downloadTask!=null){
            downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
            removeDownloadTask(downloadTask);
        }

    }

    public List<FileDbModel> loadAllDownloadEntityFromDB(){
        return fileDbUtil.getFileList();
    }

    public List<DownloadTask> loadAllDownloadTaskFromDB(){
        List<FileDbModel> list = loadAllDownloadEntityFromDB();
        List<DownloadTask> downloadTaskList = null;
        if(list!=null&&!list.isEmpty()){
            downloadTaskList = new ArrayList<>();
            for(FileDbModel entity:list){
                downloadTaskList.add(DownloadTask.parse(context,entity));
            }
        }
        return downloadTaskList;
    }

    public DownloadTask getTaskById(String taskId){
        return currentTaskList.get(taskId);
    }

}
