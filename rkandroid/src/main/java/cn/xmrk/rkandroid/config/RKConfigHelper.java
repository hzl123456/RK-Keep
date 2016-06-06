package cn.xmrk.rkandroid.config;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import cn.xmrk.rkandroid.utils.PackageUtil;
import cn.xmrk.rkandroid.utils.UnitUtil;
import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 配置文件
 */
public class RKConfigHelper implements IRKConfig {

    private static RKConfigHelper mInstance;

    public static RKConfigHelper getInstance() {
        return mInstance;
    }

    /**
     * 进行初始化，从rkconfig.json里读取出来
     */
    public static void init(Application context, IRKConfig config) {
        if (mInstance != null) {
            mInstance.context = null;
            mInstance.mRKConfig = null;
        }
        mInstance = new RKConfigHelper();
        mInstance.mRKConfig = config;
        mInstance.context = context;
        mInstance.initExceptionHandler();
        mInstance.initLog4J();
        mInstance.initRefWatcher();
    }

    private IRKConfig mRKConfig;
    private Application context;

    private final Logger log = Logger.getLogger(RKConfigHelper.class);
    private RefWatcher mRefWatcher;
    private StatisticsConfig mStatisticsConfig;

    protected void initRefWatcher() {
        if (RKConfigHelper.getInstance().isLeakWatch()) {
            mRefWatcher = LeakCanary.install(context);
        } else {
            mRefWatcher = RefWatcher.DISABLED;
        }
    }


    protected void initLog4J() {
        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(PackageUtil.getDir(context) + File.separator
                + "log.txt");
        logConfigurator.setRootLevel(org.apache.log4j.Level.ALL);
        // 文件最大512kb
        logConfigurator.setMaxFileSize(512 * 1000);
        // 设置false为不打印到文件
        logConfigurator.setUseFileAppender(true);
        // 打印到Logcat上
        logConfigurator.setUseLogCatAppender(RKConfigHelper.getInstance().isDebug());
        // 只备份一个文件
        logConfigurator.setMaxBackupSize(1);
        logConfigurator.configure();
    }

    private void initExceptionHandler() {
        final Thread.UncaughtExceptionHandler dueh = Thread.getDefaultUncaughtExceptionHandler();
		/* 处理未捕捉异常 */
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                FileOutputStream fos = null;
                PrintStream ps = null;
                try {
                    File path = context.getExternalCacheDir();
                    if (!path.isDirectory()) {
                        path.mkdirs();
                    }
                    fos = new FileOutputStream(/* CommonUtil.getDir() */path.getAbsolutePath() + File.separator + "crash_log.txt", true);
                    ps = new PrintStream(fos);
                    ps.println(UnitUtil.getFullTimestamp(System.currentTimeMillis()));
                    ex.printStackTrace(ps);
                    log.fatal("程序崩溃", ex);
                } catch (FileNotFoundException e) {
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                dueh.uncaughtException(thread, ex);
            }
        });
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    /**
     * 设置统计分析配置
     * @return
     */
    public void setStatisticsConfig(StatisticsConfig config) {
        this.mStatisticsConfig = config;
    }

    /**
     * 获取当前设置好的统计分析配置
     * @return
     */
    public StatisticsConfig getStatisticsConfig() {
        return mStatisticsConfig;
    }


    public Context getContext() {
        return context;
    }

    /**
     * 测试模式
     */
    public boolean isDebug() {
        return mRKConfig.isDebug();
    }

    /**
     * 网站域名
     */
    public String getBaseUrl() {
        return mRKConfig.getBaseUrl();
    }

    /**
     * 打开LeakCanary检测内存泄漏
     *
     * @return
     */
    public boolean isLeakWatch() {
        return mRKConfig.isLeakWatch();
    }

    /**
     * 网络连接超时时间
     */
    public int getNetTimeout() {
        return mRKConfig.getNetTimeout();
    }

    /**
     * 网络连接重试次数
     */
    public int getNetRetryCount() {
        return mRKConfig.getNetRetryCount();
    }

}
