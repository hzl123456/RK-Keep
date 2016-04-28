package cn.xmrk.rkandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.PhotoUtil;


/**
 * 图片获取方式选择
 * 2015年6月17日 下午4:23:42
 *
 */
public class ChoicePicActivity extends Activity implements OnClickListener {

	public static void start(Activity activity, boolean needCrop, int requestCode) {
		Intent _intent = new Intent(activity, ChoicePicActivity.class);
		_intent.putExtra("needCrop", needCrop);
		activity.startActivityForResult(_intent, requestCode);
	}
	
	protected Button btnTakePicture;
	protected Button btnSelectFromGallery;
	protected Button btnCancel;
	
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
			if (requestCode == PhotoUtil.REQUEST_PICK) {
				// 选择图片
				if (isNeedCrop) {
					mPhotoUtil.cropPhoto(data.getData());
				} else {
					setResult(RESULT_OK, data);
					finish();
				}
			} else if (requestCode == PhotoUtil.REQUEST_TAKE) {
				// 拍照
				if (isNeedCrop) {
					mPhotoUtil.cropPhoto(Uri.fromFile(mPhotoUtil.getTempPath()));
				} else {
					data.setData(Uri.fromFile(mPhotoUtil.getTempPath()));
					setResult(RESULT_OK, data);
					finish();
				}
			} else if (requestCode == PhotoUtil.REQUEST_CROP) {
				// 截图
				data.setData(Uri.fromFile(mPhotoUtil.getTempPath()));
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
		if ( v == btnTakePicture ) {
			try {
				mPhotoUtil.takePhoto();
			} catch (IllegalAccessException e) {
				CommonUtil.showToast(e.getMessage());
			}
		} else if ( v == btnSelectFromGallery ) {
			mPhotoUtil.pickPhoto();
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}
	
	private void findViews() {
		btnTakePicture = (Button)findViewById( R.id.btn_take_picture );
		btnSelectFromGallery = (Button)findViewById( R.id.btn_select_from_gallery );
		btnCancel = (Button)findViewById( R.id.btn_cancel );
		
		btnTakePicture.setOnClickListener(this);
		btnSelectFromGallery.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}

}
