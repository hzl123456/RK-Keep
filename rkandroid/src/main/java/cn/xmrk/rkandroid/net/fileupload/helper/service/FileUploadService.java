package cn.xmrk.rkandroid.net.fileupload.helper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.xmrk.rkandroid.net.fileupload.helper.OkHttpClientManager;
import cn.xmrk.rkandroid.net.listener.UploadTaskListener;
import cn.xmrk.rkandroid.net.listener.impl.UIProgressListener;

/**
 * Created by Au61 on 2016/1/7.
 */
public class FileUploadService extends Service {
    public static final String EXTRA_ENTITY = "extraEntity";
    public static final String EXTRA_URL = "extraUrl";
    public static final String EXTRA_FILE_PATH_ARRAY = "extraFilePath";
    public static final String EXTRA_ACTION = "extraAct";
    public static final String EXTRA_FILE_KEY_ARRAY = "extraKeyPath";
    public static final String EXTRA_PARAMS_ARRAY = "EXTRA_PARAMS_ARRAY";
    public static final String EXTRA_TASK_TAG = "EXTRA_TASK_TAG";

    public static final int ACT_STOP_UPLOAD = 1;
    public static final int ACT_UPLOAD = 0;

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
                case ACT_UPLOAD:
                    String url = intent.getStringExtra(EXTRA_URL);
                    String[] filePath = intent.getStringArrayExtra(EXTRA_FILE_PATH_ARRAY);
                    String[] fileKey = intent.getStringArrayExtra(EXTRA_FILE_KEY_ARRAY);
                    HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra(EXTRA_PARAMS_ARRAY);
                    int tag = intent.getIntExtra(EXTRA_TASK_TAG, 0);
                    upload(url, filePath, fileKey, hashMap, tag);
                    break;
                case ACT_STOP_UPLOAD:
                    int taskTag = intent.getIntExtra(EXTRA_TASK_TAG, 0);
                    stopUpload(taskTag);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopUpload(int tag) {
        OkHttpClientManager.cancel(tag);
    }

    /**
     * @param context
     * @param url      上传地址
     * @param filePath 多个文件路径
     * @param fileKey  文件对应的key
     * @param params   携带参数
     * @param tag      上传标记
     * @param listener 监听
     */

    public static final void upload(Context context, String url, String[] filePath, String[] fileKey, HashMap<String, String> params, int tag, UploadTaskListener listener) {
        if (filePath == null || url == null) {
            return;
        }
        addUploadListener(tag,listener);
        Intent downloadIntent = new Intent(context, FileUploadService.class);
        downloadIntent.putExtra(EXTRA_ACTION, ACT_UPLOAD);
        downloadIntent.putExtra(EXTRA_URL, url);
        downloadIntent.putExtra(EXTRA_FILE_PATH_ARRAY, filePath);
        downloadIntent.putExtra(EXTRA_FILE_KEY_ARRAY, fileKey);
        downloadIntent.putExtra(EXTRA_PARAMS_ARRAY, params);
        downloadIntent.putExtra(EXTRA_TASK_TAG, tag);
        context.startService(downloadIntent);
    }


    public static final void stopUpload(Context context, int tag) {
        Intent stopIntent = new Intent(context, FileUploadService.class);
        stopIntent.putExtra(EXTRA_ACTION, ACT_STOP_UPLOAD);
        stopIntent.putExtra(EXTRA_TASK_TAG, tag);
        context.startService(stopIntent);
    }

    static  HashMap<Integer, ArrayList<UploadTaskListener>> listenerMap = new HashMap<>();

    public static void  addUploadListener(int tag,UploadTaskListener uploadListener){
        ArrayList<UploadTaskListener> uploadTaskListeners = listenerMap.get(tag);
        if(uploadTaskListeners==null){
            uploadTaskListeners = new ArrayList<>();
        }
        uploadTaskListeners.add(uploadListener);
        listenerMap.put(tag,uploadTaskListeners);
    }

    //通过tag标记一处监听器
    public static void  removeUploadListener(int tag,UploadTaskListener uploadListener){
        ArrayList<UploadTaskListener> uploadTaskListeners = listenerMap.get(tag);
        if(uploadTaskListeners==null){
            uploadTaskListeners = new ArrayList<>();
        }
        if(uploadTaskListeners.contains(uploadListener)){
             uploadTaskListeners.remove(uploadListener);
        }
        listenerMap.put(tag,uploadTaskListeners);
    }

    private void upload(final String url, final String[] filePath, String[] fileKey, HashMap<String, String> params, int tag) {
        File[] files = new File[filePath.length];
        for (int i = 0; i < filePath.length; i++) {
            files[i] = new File(filePath[i]);
        }
        OkHttpClientManager.Param[] params1 = map2Params(params);
        final ArrayList<UploadTaskListener> uploadTaskListeners=listenerMap.get(tag);

        //这个是ui线程回调，可直接操作UI
        final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
            @Override
            public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
                if(uploadTaskListeners!=null){
                    for(UploadTaskListener listener:uploadTaskListeners){
                        listener.onUIProgress(bytesWrite,contentLength,done);
                    }
                }
//                uploadTaskListener.onUIProgress(bytesWrite, contentLength, done);
            }

            @Override
            public void onUIStart(long bytesWrite, long contentLength, boolean done) {
                super.onUIStart(bytesWrite, contentLength, done);
                if(uploadTaskListeners!=null){
                    for(UploadTaskListener listener:uploadTaskListeners){
                        listener.onUIStart(bytesWrite,contentLength,done);
                    }
                }
//                uploadTaskListener.onUIStart(bytesWrite, contentLength, done);
            }

            @Override
            public void onUIFinish(long bytesWrite, long contentLength, boolean done) {
                super.onUIFinish(bytesWrite, contentLength, done);
                if(uploadTaskListeners!=null){
                    for(UploadTaskListener listener:uploadTaskListeners){
                        listener.onUIFinish(bytesWrite,contentLength,done);
                    }
                }
//                uploadTaskListener.onUIFinish(bytesWrite, contentLength, done);
            }
        };
        try {
            OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {

                    if(uploadTaskListeners!=null){
                        for(UploadTaskListener listener:uploadTaskListeners){
                            listener.onError(-1, e.getMessage());
                        }
                    }
//                    uploadTaskListener.onError(-1, e.getMessage());
                }

                @Override
                public void onResponse(String response) {
                    if(uploadTaskListeners!=null){
                        for(UploadTaskListener listener:uploadTaskListeners){
                            listener.onRespense(response);
                        }
                    }
//                    uploadTaskListener.onRespense(response);
                }
            }, files, fileKey, uiProgressRequestListener, tag, params1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OkHttpClientManager.Param[] map2Params(Map<String, String> params) {
        if (params == null) return new OkHttpClientManager.Param[0];
        int size = params.size();
        OkHttpClientManager.Param[] res = new OkHttpClientManager.Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new OkHttpClientManager.Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

}
