package cn.xmrk.rkandroid.config;

/**
 * 创建日期： 2016/1/26.
 */
public interface IRKConfig {

    /**
     * 测试模式
     */
    boolean isDebug();

    /**
     * 网站域名
     */
    String getBaseUrl();

    /**
     * 打开LeakCanary检测内存泄漏
     *
     * @return
     */
    boolean isLeakWatch();

    /**
     * 网络连接超时时间
     */
    int getNetTimeout();

    /**
     * 网络连接重试次数
     */
    int getNetRetryCount();

}
