package rkkeep.keep.help;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.rey.material.app.Dialog;

import cn.xmrk.rkandroid.utils.FileUtil;
import cn.xmrk.rkandroid.utils.PhotoUtil;
import rkkeep.keep.R;

/**
 * 弹窗进行图片选择或者是拍照选择
 */
public class PictureChooseHelper implements IMessageOperate, View.OnClickListener {

    private PhotoUtil mPhotoUtil;

    private Dialog mDialog;

    private Activity activity;

    private View view;

    private String picPath;

    public PictureChooseHelper(Activity activity) {
        this.activity = activity;
        mPhotoUtil = new PhotoUtil(activity);
    }


    public void showDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(activity);
        }
        view = activity.getLayoutInflater().inflate(R.layout.layout_choose_pic, null);
        LinearLayout layoutTake = view.findViewById(R.id.layout_take);
        LinearLayout layoutChoose = view.findViewById(R.id.layout_choose);
        layoutTake.setOnClickListener(this);
        layoutChoose.setOnClickListener(this);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PhotoUtil.REQUEST_TAKE) { //拍照请求结束
                picPath = mPhotoUtil.getTempPath().getAbsolutePath();
                //返回获取的图片地址
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mOnPictureGetListener.OnPic(picPath);
            } else if (requestCode == PhotoUtil.REQUEST_PICK) { //请求结束
                if (data.getExtras().containsKey("imagePath")) {
                    picPath = data.getExtras().get("imagePath").toString();
                } else {
                    picPath = FileUtil.uri2Path(data.getData());
                }
                //返回获取的图片地址
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mOnPictureGetListener.OnPic(picPath);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == view.findViewById(R.id.layout_take)) {//图片拍照
            mPhotoUtil.takePhoto();
        } else if (v == view.findViewById(R.id.layout_choose)) {//选择图片
            mPhotoUtil.pickPhoto();
        }
    }

    private OnPictureGetListener mOnPictureGetListener;

    public void setOnPictureGetListener(OnPictureGetListener mOnPictureGetListener) {
        this.mOnPictureGetListener = mOnPictureGetListener;

    }

    public interface OnPictureGetListener {
        void OnPic(String path);
    }
}
