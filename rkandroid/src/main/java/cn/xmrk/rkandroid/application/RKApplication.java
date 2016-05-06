package cn.xmrk.rkandroid.application;

import android.app.Application;

import com.squareup.leakcanary.RefWatcher;

import cn.xmrk.rkandroid.config.IRKConfig;
import cn.xmrk.rkandroid.config.RKConfigHelper;
import cn.xmrk.rkandroid.config.StatisticsConfig;

public abstract class RKApplication extends Application {

	private static RKApplication mApplication;

	public static final RKApplication getInstance() {
		return mApplication;
	}

	public RefWatcher getRefWatcher() {
		return RKConfigHelper.getInstance().getRefWatcher();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		init();
	}

	private void init() {
		RKConfigHelper.init(this, getRKConfig());
	}


	/**
	 * 设置统计分析配置
	 * @return
	 */
	public void setStatisticsConfig(StatisticsConfig config) {
		RKConfigHelper.getInstance().setStatisticsConfig(config);
	}

	/**
	 * 获取当前设置好的统计分析配置
	 * @return
	 */
	public StatisticsConfig getStatisticsConfig() {
		return RKConfigHelper.getInstance().getStatisticsConfig();
	}

	public abstract IRKConfig getRKConfig();

}
