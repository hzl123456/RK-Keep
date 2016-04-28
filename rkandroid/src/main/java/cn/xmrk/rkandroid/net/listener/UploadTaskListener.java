package cn.xmrk.rkandroid.net.listener;

import cn.xmrk.rkandroid.net.listener.impl.UIProgressListener;

/**
 * Created by dzc on 15/11/21.
 */
public abstract class UploadTaskListener extends UIProgressListener{
    public abstract void onError(int errorCode, String errorMsg);

    public abstract void onRespense(String result);
    @Override
    public void onUIStart(long currentBytes, long contentLength, boolean done) {
        super.onUIStart(currentBytes, contentLength, done);
    }

    @Override
    public void onUIFinish(long currentBytes, long contentLength, boolean done) {
        super.onUIFinish(currentBytes, contentLength, done);
    }

    public static final int UPLOAD_ERROR_FILE_NOT_FOUND = -1;
    public static final int UPLOAD_ERROR_IO_ERROR = -2;
    public static final int UPLOAD_ERROR_IS_ALREAY_UPLOAD = -3;
}
