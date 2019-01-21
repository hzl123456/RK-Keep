package cn.xmrk.rkandroid.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import java.io.File;

import cn.xmrk.rkandroid.BuildConfig;

/**
 * 照片工具
 */
@SuppressLint("IntentReset")
public class PhotoUtil {

    public String fileName = "temp";// 一次操作只生成一个fileName,默认为temp
    /**
     * 拍照请求码，拍完了照片路径就是临时文件路径
     */
    public static final int REQUEST_TAKE = 101;
    /**
     * 相册选取照片请求码，intent.getData得到图片的Uri
     */
    public static final int REQUEST_PICK = 102;
    /**
     * 裁剪图片请求码
     */
    public static final int REQUEST_CROP = 103;

    private Activity mActivity;
    private Fragment mFragment;

    private int requestTake = REQUEST_TAKE;
    private int requestPick = REQUEST_PICK;
    private int requestCrop = REQUEST_CROP;
    private int width = 300;
    private int height = 300;

    public PhotoUtil(Activity activity) {
        this.mActivity = activity;
    }

    public PhotoUtil(Fragment fragment) {
        mFragment = fragment;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    //设置输出的文件获取，这里主要是适配一个android7.0的
    private void setOutputUrl(Intent intent, File outputFile) {
        //如果在Android7.0以上,使用FileProvider获取Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".FileProvider", outputFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        }
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        fileName = System.currentTimeMillis() + "temp";
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("return-data", false);
        cameraIntent.putExtra("noFaceDetection", true);
        setOutputUrl(cameraIntent, getTempPath());
        if (mActivity == null) {
            mFragment.startActivityForResult(cameraIntent, requestTake);
        } else {
            mActivity.startActivityForResult(cameraIntent, requestTake);
        }
    }

    /**
     * 从相册中选取图片
     */
    public void pickPhoto() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openAlbumIntent.putExtra("return-data", false);
        openAlbumIntent.setType("image/*");
        if (mActivity == null) {
            mFragment.startActivityForResult(openAlbumIntent, requestPick);
        } else {
            mActivity.startActivityForResult(openAlbumIntent, requestPick);
        }
    }

    /**
     * 裁剪图片
     */
    public void cropPhoto(Uri uri) {
        fileName = System.currentTimeMillis() + "";// 最后都要经过裁剪，所以有个最终的fileName
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //下面需要判断哪个文件夹是否是存在的不存在则需要创建
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempPath()));
        intent.setDataAndType(uri, "image/*"); // 设置要裁剪的图片
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        if (width > 0 && height > 0) {
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
        }
        intent.putExtra("return-data", false);
        intent.putExtra("ouputFormat", Bitmap.CompressFormat.JPEG.toString());
        if (mActivity == null) {
            mFragment.startActivityForResult(intent, requestCrop);
        } else {
            mActivity.startActivityForResult(intent, requestCrop);
        }
    }

    /**
     * 返回temp.jpg
     *
     * @return
     */
    public File getTempPath() {
        // NOTE: 不能使用 Context.getCacheDir()，因为系统是没有权限写入数据到app文件夹里的
        File tempFileDir = new File(CommonUtil.getSDCardPath() + File.separator + "tmp");
        // 文件夹必须存在
        if (!tempFileDir.isDirectory()) {
            tempFileDir.mkdirs();
        }
        //保存裁剪的图片名称为当前系统时间的毫秒值
        File tempFile = new File(tempFileDir.getAbsolutePath() + File.separator + fileName + ".jpg");
        tempFile.deleteOnExit();
        return tempFile;
    }


    /**
     * 设置REQUEST_TAKE的值
     *
     * @param requestCode
     */
    public void setTakeRequestCode(int requestCode) {
        requestTake = requestCode;
    }

    /**
     * 设置REQUEST_PICKE的值
     *
     * @param requestCode
     */
    public void setPickRequestCode(int requestCode) {
        requestPick = requestCode;
    }

    /**
     * 设置REQUEST_CROP的值
     *
     * @param requestCode
     */
    public void setCropRequestCode(int requestCode) {
        requestCrop = requestCode;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }
}
