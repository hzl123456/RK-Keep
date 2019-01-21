package cn.xmrk.rkandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.xmrk.rkandroid.BuildConfig;
import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.utils.PhotoUtil;


/**
 * 图片获取方式选择
 * 2015年6月17日 下午4:23:42
 */
public class ChoicePicActivity extends Activity implements OnClickListener {

    public static void start(Activity activity, boolean needCrop, int requestCode) {
        Intent _intent = new Intent(activity, ChoicePicActivity.class);
        _intent.putExtra("needCrop", needCrop);
        activity.startActivityForResult(_intent, requestCode);
    }

    protected TextView btnTakePicture;
    protected TextView btnSelectFromGallery;
    protected TextView btnCancel;

    private boolean isDown;

    private PhotoUtil mPhotoUtil;

    /**
     * 需要截图
     */
    private boolean isNeedCrop = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoUtil.REQUEST_TAKE) { //调用拍照后返回
                //获取一个contentUri，这里主要是兼容一个android7.0的操作
                Uri contentUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".FileProvider", mPhotoUtil.getTempPath());
                } else {
                    contentUri = Uri.fromFile(mPhotoUtil.getTempPath());
                }
                if (isNeedCrop) { //需要裁剪的
                    mPhotoUtil.cropPhoto(contentUri);
                } else { //直接返回的
                    data.setData(contentUri);
                    setResult(RESULT_OK, data);
                    finish();
                }
            } else if (requestCode == PhotoUtil.REQUEST_PICK) { //调用相册后返回
                // 选择图片
                if (isNeedCrop) {
                    mPhotoUtil.cropPhoto(data.getData());
                } else {
                    setResult(RESULT_OK, data);
                    finish();
                }
            } else if (requestCode == PhotoUtil.REQUEST_CROP) { //调用裁剪后返回
                //通过bundle去直接返回图片的url地址
                data.putExtra("imagePath", mPhotoUtil.getTempPath());
                setResult(RESULT_OK, data);
                finish();
            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                break;
            case MotionEvent.ACTION_UP:
                if (isDown) {
                    onBackPressed();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                isDown = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_choice_pic_way);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);

        isNeedCrop = getIntent().getBooleanExtra("needCrop", true);

        findViews();

        mPhotoUtil = new PhotoUtil(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnTakePicture) {
            mPhotoUtil.takePhoto();
        } else if (v == btnSelectFromGallery) {
            mPhotoUtil.pickPhoto();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void findViews() {
        btnTakePicture = findViewById(R.id.btn_take_picture);
        btnSelectFromGallery = findViewById(R.id.btn_select_from_gallery);
        btnCancel = findViewById(R.id.btn_cancel);

        btnTakePicture.setOnClickListener(this);
        btnSelectFromGallery.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
}
