package cn.xmrk.rkandroid.net.fileupload.helper;

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

import cn.xmrk.rkandroid.net.listener.ProgressListener;
import cn.xmrk.rkandroid.net.listener.impl.UIProgressListener;

/**
 * Created by Au61 on 2016/1/5.
 */
public class OkHttpClientManager {
    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;

    private static final String TAG = "OkHttpClientManager";

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper()) {

        };
        mGson = new Gson();
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 通过标记取消call
     *
     * @param tag
     */
    public void cancelCall(int tag) {
        mOkHttpClient.cancel(tag);
    }

    public static void cancel(int tag) {
        OkHttpClientManager.getInstance().cancelCall(tag);
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    private Response _getAsyn(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return 字符串
     */
    private String _getAsString(String url) throws IOException {
        Response execute = _getAsyn(url);
        return execute.body().string();
    }


    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getAsyn(String url, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    private Response _post(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return 字符串
     */
    private String _postAsString(String url, Param... params) throws IOException {
        Response response = _post(url, params);
        return response.body().string();
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Param... params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    /**
     * 同步基于post的多个文件上传,带参数，ui线程回调进度
     *
     * @param params
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, UIProgressListener uIProgressListener, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params, uIProgressListener);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的多个文件上传,带参数
     *
     * @param params
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的多个文件上传,带参数，非ui线程回调进度
     *
     * @param params
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, ProgressListener progressListener, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params, progressListener);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的单个文件上传
     *
     * @return
     */
    private Response _post(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的单个文件上传,非Ui线程回调进度
     *
     * @return
     */
    private Response _post(String url, File file, String fileKey, ProgressListener progressListener) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null, progressListener);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的单个文件上传,Ui线程回调进度
     *
     * @return
     */
    private Response _post(String url, File file, String fileKey, UIProgressListener progressListener) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null, progressListener);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的单个文件上传,带参数
     *
     * @param params
     * @return
     */
    private Response _post(String url, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的单个文件上传,带参数，Ui线程回调进度
     *
     * @param params
     * @return
     */
    private Response _post(String url, File file, String fileKey, UIProgressListener progressListener, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params, progressListener);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的单个文件上传,带参数，非Ui线程回调进度
     *
     * @param params
     * @return
     */
    private Response _post(String url, File file, String fileKey, ProgressListener progressListener, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params, progressListener);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步基于post的文件上传，多文件带不参数上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，多文件带不参数上传,Ui线程实现回调
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, UIProgressListener uiProgressListener) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, null, uiProgressListener);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，多文件带不参数上传，非Ui线程实现回调
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, ProgressListener progressListener) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, null, progressListener);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，多文件带参数上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，多文件带参数上传,Ui线程回调进度
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, UIProgressListener uiProgressListener, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params, uiProgressListener);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，多文件带参数上传,非Ui线程回调进度
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, ProgressListener progressListener, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params, progressListener);
        deliveryResult(callback, request);
    }

    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, ProgressListener progressListener, int tag, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params, tag, progressListener);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传,ui线程实现进度回调
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey, UIProgressListener uiProgressListener) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null, uiProgressListener);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传,非Ui线程实现进度回调
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey, ProgressListener progressListener) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null, progressListener);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传,Ui线程实现回调进度
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey, UIProgressListener uiProgressListener, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params, uiProgressListener);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传,非Ui线程实现回调进度
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey, ProgressListener progressListener, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params, progressListener);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final ResultCallback callback) {
        _downloadAsyn(url, destFileDir, callback, null);
    }

/*    *//**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     *//*
    private void _downloadAsyn(final String url, final String destFileDir, final ResultCallback callback,UIProgressListener uiProgressListener)
    {
        _downloadAsyn(url,destFileDir,callback,uiProgressListener);
    }*/

    /**
     * 异步下载文件，非Ui线程回调进度
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final ResultCallback callback, ProgressListener progressListener) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = null;
        if (progressListener != null) {
            call = ProgressHelper.addProgressResponseListener(mOkHttpClient, progressListener).newCall(request);
        } else {
            call = mOkHttpClient.newCall(request);
        }

        //    ProgressHelper.addProgressResponseListener(client, uiProgressResponseListener).newCall(request1).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Log.e("TAG", "error ", e);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                Log.e("TAG", response.body().string());
//            }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

//    /**
//     * 加载图片
//     *
//     * @param view
//     * @param url
//     * @throws IOException
//     */
//    private void _displayImage(final ImageView view, final String url, final int errorResId)
//    {
//        final Request request = new Request.Builder()
//                .url(url)
//                .build();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback()
//        {
//            @Override
//            public void onFailure(Request request, IOException e)
//            {
//                setErrorResId(view, errorResId);
//            }
//
//            @Override
//            public void onResponse(Response response)
//            {
//                InputStream is = null;
//                try
//                {
//                    is = response.body().byteStream();
//                    ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
//                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
//                    int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
//                    try
//                    {
//                        is.reset();
//                    } catch (IOException e)
//                    {
//                        response = _getAsyn(url);
//                        is = response.body().byteStream();
//                    }
//
//                    BitmapFactory.Options ops = new BitmapFactory.Options();
//                    ops.inJustDecodeBounds = false;
//                    ops.inSampleSize = inSampleSize;
//                    final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
//                    mDelivery.post(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            view.setImageBitmap(bm);
//                        }
//                    });
//                } catch (Exception e)
//                {
//                    setErrorResId(view, errorResId);
//
//                } finally
//                {
//                    if (is != null) try
//                    {
//                        is.close();
//                    } catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//
//    }

    private void setErrorResId(final ImageView view, final int errorResId) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                view.setImageResource(errorResId);
            }
        });
    }


    //*************对外公布的方法************


    public static Response getAsyn(String url) throws IOException {
        return getInstance()._getAsyn(url);
    }


    public static String getAsString(String url) throws IOException {
        return getInstance()._getAsString(url);
    }

    public static void getAsyn(String url, ResultCallback callback) {
        getInstance()._getAsyn(url, callback);
    }

    public static Response post(String url, Param... params) throws IOException {
        return getInstance()._post(url, params);
    }

    public static String postAsString(String url, Param... params) throws IOException {
        return getInstance()._postAsString(url, params);
    }

    public static void postAsyn(String url, final ResultCallback callback, Param... params) {
        getInstance()._postAsyn(url, callback, params);
    }


    public static void postAsyn(String url, final ResultCallback callback, Map<String, String> params) {
        getInstance()._postAsyn(url, callback, params);
    }


    public static Response post(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        return getInstance()._post(url, files, fileKeys, params);
    }

    public static Response post(String url, File[] files, String[] fileKeys, UIProgressListener uiProgressListener, Param... params) throws IOException {
        return getInstance()._post(url, files, fileKeys, uiProgressListener, params);
    }

    public static Response post(String url, File[] files, String[] fileKeys, ProgressListener ProgressListener, Param... params) throws IOException {
        return getInstance()._post(url, files, fileKeys, ProgressListener, params);
    }

    public static Response post(String url, File file, String fileKey, UIProgressListener uIProgressListener) throws IOException {
        return getInstance()._post(url, file, fileKey, uIProgressListener);
    }

    public static Response post(String url, File file, String fileKey) throws IOException {
        return getInstance()._post(url, file, fileKey);
    }

    public static Response post(String url, File file, String fileKey, ProgressListener progressListener) throws IOException {
        return getInstance()._post(url, file, fileKey, progressListener);
    }

    public static Response post(String url, File file, String fileKey, UIProgressListener uiProgressListener, Param... params) throws IOException {
        return getInstance()._post(url, file, fileKey, uiProgressListener, params);
    }

    public static Response post(String url, File file, String fileKey, ProgressListener progressListener, Param... params) throws IOException {
        return getInstance()._post(url, file, fileKey, progressListener, params);
    }

    public static Response post(String url, File file, String fileKey, Param... params) throws IOException {
        return getInstance()._post(url, file, fileKey, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, UIProgressListener uiProgressListener) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, uiProgressListener);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, ProgressListener progressListener) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, progressListener);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, UIProgressListener uiProgressListener, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, uiProgressListener, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, ProgressListener progressListener, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, progressListener, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, ProgressListener progressListener, int tag, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, progressListener, tag, params);
    }


    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey);
    }

    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey, UIProgressListener uiProgressListener) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, uiProgressListener);
    }

    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey, ProgressListener progressListener) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, progressListener);
    }


    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey, UIProgressListener uiProgressListener, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, uiProgressListener, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey, ProgressListener progressListener, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, progressListener, params);
    }

    public static void displayImage(final ImageView view, String url, int errorResId) throws IOException {
//        getInstance()._displayImage(view, url, errorResId);
    }


    public static void displayImage(final ImageView view, String url) {
//        getInstance()._displayImage(view, url, -1);
    }

    public static void downloadAsyn(String url, String destDir, ResultCallback callback) {
        getInstance()._downloadAsyn(url, destDir, callback);
    }

    public static void downloadAsyn(String url, String destDir, ResultCallback callback, ProgressListener progressListener) {
        getInstance()._downloadAsyn(url, destDir, callback, progressListener);
    }

    //****************************
    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params) {
        return buildMultipartFormRequest(url, files, fileKeys, params, null);
    }

    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params, ProgressListener progressListener) {
        return buildMultipartFormRequest(url, files, fileKeys, params, null, progressListener);
    }

    /**
     * 表单构建request
     *
     * @param url
     * @param files
     * @param fileKeys
     * @param params
     * @param progressListener
     * @return
     */
    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params, Object tag, ProgressListener progressListener) {
        params = validateParam(params);

        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        Request.Builder builder1 = new Request.Builder();
        if (tag != null) {
            builder1.tag(tag);
        }
        builder1.url(url);
        if (progressListener == null) {
            builder1.post(requestBody);
        } else {
            builder1.post(ProgressHelper.addProgressRequestListener(requestBody, progressListener));
        }
        return builder1.build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else return params;
    }

    private Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

  /*  private static final String SESSION_KEY = "Set-Cookie";
    private static final String mSessionKey = "JSESSIONID";

    private Map<String, String> mSessions = new HashMap<String, String>();
*/

    /**
     * 处理返回结果
     *
     * @param callback
     * @param request
     */

    private void deliveryResult(final ResultCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    final String string = response.body().string();
                    if (callback.mType == String.class) {
                        sendSuccessResultCallback(string, callback);
                    } else {
                        Object o = mGson.fromJson(string, callback.mType);
                        sendSuccessResultCallback(o, callback);
                    }


                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    sendFailedStringCallback(response.request(), e, callback);
                }

            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    private Request buildPostRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }


    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }

    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }
}
