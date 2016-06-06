package cn.xmrk.rkandroid.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;

/**
 * Created by Au61 on 2016/5/18.
 */
public class VideoUtil {

    /**
     * 操作形成的文件
     **/
    public String fileName;
    /**
     * 视频录制请求码
     */
    public static final int REQUEST_TAKE = 201;
    /**
     * 本地选取视频请求码
     */
    public static final int REQUEST_PICK = 202;

    private Activity mActivity;
    private Fragment mFragment;

    public VideoUtil(Activity activity) {
        this.mActivity = activity;
    }

    public VideoUtil(Fragment fragment) {
        this.mFragment = fragment;
    }

    /**
     * 返回录制视频的文件，文件格式为
     *
     * @return
     */
    public File getTempPath() {
        // 视频文件保存在sd卡文件的RKVideo文件夹下
        File tempFileDir = new File(CommonUtil.getSDCardPath() + File.separator + "RKVideo");
        // 文件夹必须存在
        if (!tempFileDir.isDirectory()) {
            tempFileDir.mkdirs();
        }
        // 文件名称为系统时间的毫秒值，保证唯一性，格式为mp4格式
        File tempFile = new File(tempFileDir.getAbsolutePath() + File.separator + fileName + ".mp4");
        tempFile.deleteOnExit();
        return tempFile;
    }

    public void takeVideo() throws IllegalAccessException {
        if (!PhoneUtil.getSdcardWritable()) {
            throw new IllegalAccessError("内存卡不可用");
        }
        //创建一个文件路径，并且用fileName去保存它
        fileName = String.valueOf(System.currentTimeMillis());
        Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempPath()));
        if (mActivity == null)
            mFragment.startActivityForResult(intent, REQUEST_TAKE);
        else
            mActivity.startActivityForResult(intent, REQUEST_TAKE);
    }

    /**
     * 从相册中选取图片
     */
    public void pickVideo() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openAlbumIntent.putExtra("return-data", false);
        openAlbumIntent.setType("video/*");
        if (mActivity == null)
            mFragment.startActivityForResult(openAlbumIntent, REQUEST_PICK);
        else
            mActivity.startActivityForResult(openAlbumIntent, REQUEST_PICK);
    }
}
