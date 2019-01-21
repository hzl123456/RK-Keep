package rkkeep.keep.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.rey.material.app.Dialog;

import java.io.File;

import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.FileUtil;
import cn.xmrk.rkandroid.utils.VideoUtil;
import rkkeep.keep.R;
import rkkeep.keep.util.VoiceUtil;

/**
 * Created by Au61 on 2016/5/18.
 */
@SuppressLint("HandlerLeak")
public class VideoChooseHelper implements IMessageOperate, View.OnClickListener {

    private Snackbar mSnackbar;

    private Activity activity;

    private VideoUtil mVideoUtil;

    private Dialog mDialog;

    private View view;

    private String videoPath;

    public VideoChooseHelper(Activity activity, View view) {
        this.activity = activity;
        mVideoUtil = new VideoUtil(activity);
        mSnackbar = Snackbar.make(view, "正在加载视频文件", Snackbar.LENGTH_INDEFINITE);
    }


    public void showDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(activity);
        }
        view = activity.getLayoutInflater().inflate(R.layout.layout_choose_video, null);
        LinearLayout layoutTake = (LinearLayout) view.findViewById(R.id.layout_take);
        LinearLayout layoutChoose = (LinearLayout) view.findViewById(R.id.layout_choose);
        layoutTake.setOnClickListener(this);
        layoutChoose.setOnClickListener(this);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VideoUtil.REQUEST_TAKE) {
                videoPath = mVideoUtil.getTempPath().getAbsolutePath();
                setVideo(videoPath);
            } else if (requestCode == VideoUtil.REQUEST_PICK) {
                videoPath = FileUtil.uri2Path(data.getData());
                setVideo(videoPath);
            }
        }
    }

    public void setVideo(final String Path) {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mSnackbar.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String imagePath = VoiceUtil.getImagePath() + System.currentTimeMillis() + ".png";
                FileUtil.saveBmpToFilePng(CommonUtil.getVidioBitmap(Path, 0, 0, 0), new File(imagePath));
                mHandler.sendMessage(mHandler.obtainMessage(0, imagePath));
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            if (msg.what == 0) {
                Log.i(videoPath, (String) msg.obj);
                if (mOnVideoGetListener != null) {
                    mOnVideoGetListener.OnPic(videoPath, (String) msg.obj);
                }
                mSnackbar.dismiss();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == view.findViewById(R.id.layout_take)) {//图片拍照
            mVideoUtil.takeVideo();
        } else if (v == view.findViewById(R.id.layout_choose)) {//选择图片
            mVideoUtil.pickVideo();
        }
    }

    private OnVideoGetListener mOnVideoGetListener;

    public void setOnVideoGetListener(OnVideoGetListener mOnVideoGetListener) {
        this.mOnVideoGetListener = mOnVideoGetListener;

    }

    public interface OnVideoGetListener {
        void OnPic(String path, String imagePath);
    }
}
