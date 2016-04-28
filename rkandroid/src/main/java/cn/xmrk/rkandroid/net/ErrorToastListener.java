package cn.xmrk.rkandroid.net;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.apache.log4j.Logger;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

import cn.xmrk.rkandroid.activity.BaseActivity;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.StringUtil;

/**
 * 请求错误的时候Toast出来
 * 
 * @author 思落羽 2014年11月17日 下午4:56:22
 *
 */
public class ErrorToastListener implements ErrorListener {

	private Logger log = Logger.getLogger(getClass());

	private WeakReference<BaseActivity> mActivity;

	private String msg;

	public ErrorToastListener() {
	}

	public ErrorToastListener(BaseActivity activity) {
		mActivity = new WeakReference<BaseActivity>(activity);
	}

	public ErrorToastListener(Context context) {
		this(context instanceof BaseActivity ? (BaseActivity) context : null);
	}

	public ErrorToastListener(String msg) {
		setMsg(msg);
	}

	public ErrorToastListener(BaseActivity activity, String msg) {
		mActivity = new WeakReference<BaseActivity>(activity);
		setMsg(msg);
	}

	public ErrorToastListener(Context context, String msg) {
		this(context instanceof BaseActivity ? (BaseActivity) context : null);
		setMsg(msg);
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		try {
			log.error(error.getMessage(), error.fillInStackTrace());
		} catch (Exception e){
			log.error("无错误栈");
		}
		String fm = error.getMessage();
		if (fm == null) {
			fm = error.getClass().getName();
		}
		if (fm == null) {
			fm = "";
		}
		String msg;
		if (UnknownHostException.class.equals(error.getClass())) {
			msg = "网络错误，请检查您的网络";
		} else if (TimeoutError.class.equals(error.getClass())) {
			msg = "网络连接超时";
		} else if (fm.startsWith("java.cn.xmrk.rkandroid.net.ConnectException")) {
			msg = "网络错误，请检查您的网络";
		} else if (fm.startsWith("java.cn.xmrk.rkandroid.net.SocketTimeoutException") || fm.startsWith("org.apache.http.conn.ConnectTimeoutException")) {
			msg = "网络连接超时";
		} else if (fm.startsWith("java.cn.xmrk.rkandroid.net.UnknownHostException")) {
			msg = "网络错误，请检查您的网络";
		} else {
			if (StringUtil.isEmptyString(this.msg)) {
				msg = "未知网络错误";
			} else {
				msg = this.msg;
			}
		}
		onErrorMessageShow(msg);
		if (mActivity != null && mActivity.get() != null)
			if (mActivity.get().hasPDM())
				mActivity.get().getPDM().dismiss();
	}
	
	/**
	 * 转为为Toast出来，可以自己进行显示
	 * @param msg 中文的错误信息
	 * @return
	 */
	public void onErrorMessageShow(String msg) {
		CommonUtil.showToast(msg);
	}

}
