package cn.xmrk.rkandroid.net.fileupload.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.net.fileupload.helper.filedb.FileDbModel;
import cn.xmrk.rkandroid.net.fileupload.helper.filedb.FileDbUtil;
import cn.xmrk.rkandroid.net.listener.DownloadTaskListener;

/**
 * Created by dzc on 15/11/21.
 */
public class DownloadTask implements Runnable {
    private FileDbUtil fileDbUtil;
    private DownloadManager downloadManager;
    private OkHttpClient client;
    private FileDbModel fileDbModel;


    private long toolSize;
    private long completedSize;//已经下载完的部分
//    private float percent;//完成百分比
    private String url;
    private RandomAccessFile file;
    private int UPDATE_SIZE = 40 * 1024;//每40k更新一次数据库
    private int downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;

    private String filePath;
    Context context;



    private List<DownloadTaskListener> listeners;

    public DownloadTask(Context context,String url,String filePath) {
        this.context = context;
        this.url = url;
        this.filePath = filePath;
        fileDbUtil = new FileDbUtil(context);
        listeners = new ArrayList<>();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 执行下载
     */
    @Override
    public void run() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_PREPARE;
//        onPrepare();
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            fileDbModel = fileDbUtil.getFileInfoWithPath(filePath,url);

            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_START;
            Request request = new Request.Builder()
                    .url(url)
                    .header("RANGE", "bytes=" + completedSize + "-")//设置http断点RANGE值
                    .build();
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            if(responseBody!=null){
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
                toolSize = responseBody.contentLength();
                inputStream = responseBody.byteStream();
                bis = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[2 * 1024];
                int length = 0;
                int buffOffset = 0;

                if(fileDbModel==null){
                    fileDbModel = new FileDbModel(url,filePath,toolSize,0,false);
                    fileDbUtil.add(fileDbModel);
                    File f = new File(filePath);
                    if( f.exists()){
                        f.delete();
                        f.createNewFile();
                    }
                    file = new RandomAccessFile( f,"rwd");
                }else{
                    completedSize = fileDbModel.getCurLength();
                    file = new RandomAccessFile(filePath,"rwd");
                    if(file.length()>=completedSize&&completedSize<toolSize){
                        file.seek(completedSize);
                    }else{
                        File f = new File(filePath);
                        if(f.exists()){
                            f.delete();
                            f.createNewFile();
                        }
                        file = new RandomAccessFile( f,"rwd");
                        completedSize = 0;
                    }
                }
                while ((length = bis.read(buffer)) > 0 && downloadStatus!=DownloadStatus.DOWNLOAD_STATUS_CANCEL) {
                    file.write(buffer, 0, length);
                    completedSize += length;
                    buffOffset += length;
                    if (buffOffset >= UPDATE_SIZE) {
                        // 更新数据库中的下载信息
                        buffOffset = 0;
//                        fileDbModel.setFileLength(toolSize);
//                        fileDbModel.setCurLength(completedSize);
                        fileDbUtil.updateFileInfo(filePath,completedSize);
                        onDownloading(completedSize,toolSize,false);
                    }
                }
                fileDbUtil.updateFileInfo(filePath,completedSize);
                onDownloading(completedSize,toolSize,true);
            }
        } catch (FileNotFoundException e) {
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_FILE_NOT_FOUND,"文件未找到");
            return;
//            e.printStackTrace();
        } catch (IOException e) {
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR,"操作文件失败");
            return;
//            e.printStackTrace();
        }finally {
            if(bis!=null) try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(inputStream!=null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(file!=null) try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
        fileDbUtil.updateFileInfo(filePath,completedSize);

        onCompleted();
    }



    public float getPercent() {
        return completedSize*100 /toolSize;
    }


    public long getToolSize() {
        return toolSize;
    }

    public void setToolSize(long toolSize) {
        this.toolSize = toolSize;
    }



    public long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }


    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public void setHttpClient(OkHttpClient client) {
        this.client = client;
    }



    public void cancel(){
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_CANCEL;
    }

//    private void onPrepare(){
//        for(DownloadTaskListener listener:listeners){
//            listener.onUIStart(this);
//        }
//    }
//    private void onStart(){
//        for(DownloadTaskListener listener:listeners){
//            listener.onStart(this);
//        }
//    }
    private void onDownloading(long curLength,long contentLength,boolean isDone){
        for(DownloadTaskListener listener:listeners){
            listener.onProgress(curLength,contentLength,isDone);
        }
    }
    private void onCompleted(){
        if(toolSize==completedSize){
            cn.xmrk.rkandroid.net.fileupload.helper.DownloadManager.getInstance(context).removeDownloadTask(this);
        }
    }
    private void onError(int errorCode,String msg){
        for(DownloadTaskListener listener:listeners){
            listener.onError(errorCode,msg);
        }
    }

    public void addDownloadListener(DownloadTaskListener listener){
        listeners.add(listener);
    }

    public void removeDownloadListener(DownloadTaskListener listener){
        listeners.remove(listener);
    }
    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }


    @Override
    public boolean equals(Object o) {
        if(this==o){
            return true;
        }
        if(!(o instanceof DownloadTask)){
            return false;
        }
        if(TextUtils.isEmpty(url)||TextUtils.isEmpty(filePath)){
            return false;
        }
        return url.equals(((DownloadTask) o).url) && filePath.equals(((DownloadTask) o).filePath);
    }

   public static DownloadTask parse(Context context,FileDbModel entity){
        DownloadTask task = new DownloadTask(context,entity.getUrl(),entity.getFilePath());
        return task;
    }
}
