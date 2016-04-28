package cn.xmrk.rkandroid.pojo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 返回值列表
 * 
 * @author 思落羽 2014年4月15日 下午6:15:31
 */
public class ResultInfo {

	/**
	 * success为操作成功，其他为失败 必有参数
	 */
	public String flag;

	/**
	 * 操作结果说明 必有参数
	 */
	public String info;

	/**
	 * 必有参数
	 */
	public long time;

	/**
	 * data如果为数组，该数组长度 非必有参数
	 */
	public int count;

	/**
	 * 操作结果数据 非必有参数
	 */

	public JsonElement data;

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getCount() {
		if (data != null && data.isJsonObject()) {
			JsonObject _jo = data.getAsJsonObject();
			if (_jo.has("count")) {
				return _jo.get("count").getAsInt();
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public void setCount(int count) {
		this.count = count;
	}

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonObject data) {
		this.data = data;
	}

	/**
	 * 返回list:{other}
	 */
	public JsonElement getDataOther() {
		if (data == null) {
			return data;
		} else {
			return ((JsonObject) data).get("data");
		}
	}

	/**
	 * 返回data:{data}
	 */
	public JsonElement getDataData() {
		if (data == null) {
			return data;
		} else {
			return ((JsonObject) data).get("data");
		}
	}

	public long getDataCount() {
		long _count = 0;
		try {
			_count = data.getAsJsonObject().get("count").getAsLong();
			return _count;
		} catch (Exception e) {
			return 0;
		}
	}

}
