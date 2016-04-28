package cn.xmrk.rkandroid.net;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

import cn.xmrk.rkandroid.pojo.ResultInfo;

public class CommonRequest extends BaseRequest<ResultInfo>{

	public CommonRequest(int method, String url, Response.Listener<ResultInfo> listener,
			ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	public CommonRequest(String url, ErrorListener listener) {
		super(url, listener);
	}

	public CommonRequest(String url, Response.Listener<ResultInfo> listener,
			ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	@Override
	protected Class<ResultInfo> getTClass() {
		return ResultInfo.class;
	}

}
