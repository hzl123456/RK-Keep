package cn.xmrk.rkandroid.net.listener;

import cn.xmrk.rkandroid.net.listener.impl.UIProgressListener;

/**
 * Created by dzc on 15/11/21.
 */
public abstract class DownloadTaskListener extends UIProgressListener{
//    public abstract void onPause(DownloadTask downloadTask);
    public abstract void onError(int errorCode,String msg);

    @Override
    public void onUIStart(long currentBytes, long contentLength, boolean done) {
        super.onUIStart(currentBytes, contentLength, done);
    }

    @Override
    public void onUIFinish(long currentBytes, long contentLength, boolean done) {
        super.onUIFinish(currentBytes, contentLength, done);
    }

    public static final int DOWNLOAD_ERROR_FILE_NOT_FOUND = -1;
    public static final int DOWNLOAD_ERROR_IO_ERROR = -2;
    public static final int DOWNLOAD_ERROR_IS_ALREAY_DOWNLOAD = -3;
}
