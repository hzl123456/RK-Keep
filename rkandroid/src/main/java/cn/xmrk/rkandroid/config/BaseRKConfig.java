package cn.xmrk.rkandroid.config;

/**
 * 创建日期： 2016/1/26.
 */
public abstract class BaseRKConfig implements IRKConfig {

    @Override
    public boolean isLeakWatch() {
        return false;
    }

    @Override
    public int getNetTimeout() {
        return 10000;
    }

    @Override
    public int getNetRetryCount() {
        return 3;
    }
}
