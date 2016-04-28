package cn.xmrk.rkandroid.net;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.xmrk.rkandroid.config.RKConfigHelper;
import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.PackageUtil;


public abstract class BaseRequest<T> extends Request<T> {

	private Listener<T> mListener;
	
	private Map<String, String> mParams;
	
	public BaseRequest(String url, ErrorListener listener) {
		this(Request.Method.POST, url, null, listener);
		init();
	}

	public BaseRequest(String url, Listener<T> listener, ErrorListener errorListener) {
		this(Request.Method.POST, url, listener, errorListener);
		init();
	}
	
	public BaseRequest(int method, String url, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		setListener(listener);
		init();
	}
	
	private void init() {
		// 连接重试机制
		setRetryPolicy(new DefaultRetryPolicy(RKConfigHelper.getInstance().getNetTimeout(), RKConfigHelper.getInstance().getNetTimeout(), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> map = super.getHeaders();
		HashMap<String, String> headers = new HashMap<String, String>();
		if (map != null) {
			headers.putAll(map);
		}
		headers.put("version", String.valueOf(PackageUtil.getVersionCode(CommonUtil.getAppContext())));
		return headers;
	}
	
	public void setListener(Listener<T> listener) {
		this.mListener = listener;
	}
	
	public void setParams(Map<String, String> params) {
		this.mParams = params;
	}
	
	public void addParams(String key, String value) {
		if (mParams == null) {
			mParams = new HashMap<String, String>();
		}
		mParams.put(key, value);
	}
	
	public void addParams(String key, float value) {
		addParams(key, value + "");
	}
	
	public void addParams(String key, double value) {
		addParams(key, value + "");
	}
	
	public void addParams(String key, int value) {
		addParams(key, value + "");
	}
	
	public void removeParams(String key) {
		if (mParams != null) {
			mParams.remove(key);
		}
	}
	
	public void addAll(Map<String, String> params) {
		if (mParams == null) {
			mParams = new HashMap<String, String>();
		}
		mParams.putAll(params);
	}
	
	public String getParams(String key) {
		if (mParams == null) {
			return null;
		}
		return mParams.get(key);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}
	
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		if (response.statusCode != 200) {
			return Response.error(new ParseError(response));
		}
		try {
			String jsonStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			Logger.getLogger("Result").debug("Result ==> " + jsonStr);
			// 验证Json的合法性
			JsonObject jObj = null;
			try {
				jObj = new JsonParser().parse(jsonStr).getAsJsonObject();
			} catch (Exception e) {
				jObj = new JsonParser().parse("{\"status\":\"false\",\"msg\",\"服务端异常\"}").getAsJsonObject();
				return Response.error(new VolleyError("服务器异常", e));
			}
			// 验证是否存在 totalcount
			if (jObj.has("totalcount")) {
				try {
					 jObj.get("totalcount").getAsInt();
				} catch (Exception e) {
					jObj.remove("totalcount");
					jsonStr = jObj.toString();
				}
			}
			// 判断Session过期
			return Response.success(CommonUtil.getGson().fromJson(jsonStr, getTClass()), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	/**
	 * 得到泛型的实际类型用以Gson解析对象
	 * @return
	 */
	protected abstract Class<T> getTClass();

	@Override
	protected void deliverResponse(T response) {
		if (mListener != null) {
			mListener.onResponse(response);
		}
	}
	
}
