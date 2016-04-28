package cn.xmrk.rkandroid.net;


import com.android.volley.Response;

import java.lang.ref.WeakReference;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.pojo.ResultInfo;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.StringUtil;

public abstract class CommonListener implements Response.Listener<ResultInfo> {
	
	private WeakReference<BaseActivity> mActivity;
	
	public CommonListener(){}
	
	public CommonListener(BaseActivity activity) {
		mActivity = new WeakReference<>(activity);
	}

	@Override
	public void onResponse(ResultInfo result) {
		if (mActivity != null && mActivity.get() != null) {
			if (mActivity.get().hasPDM()) {
				mActivity.get().getPDM().dismiss();
			}
		}
		if (isSuccess(result)) {
			onSuccess(result);
		} else {
			onOtherFlag(result);
		}
	}

	protected boolean isSuccess(ResultInfo result) {
		return StringUtil.isEqualsString("success", result.flag);
	}
	
	public abstract void onSuccess(ResultInfo resultInfo);
	
	public void onOtherFlag(ResultInfo resultInfo) {
		CommonUtil.showToast(resultInfo.info);
	}

}
