package cn.xmrk.rkandroid.config;

/**
 * 创建日期： 2015/10/15.
 * 统计配置
 */
public interface StatisticsConfig {

    /**
     * Activity 的 onResume 回调里执行
     * @param tag 页面的标签
     */
    void onActivityResume(String tag);

    /**
     * Activity 的 onPause 回调里执行
     * @param tag 页面的标签
     */
    void onActivityPause(String tag);

    /**
     * Fragment 的显示时执行
     * @param tag
     */
    void onFragmentShow(String tag);

    /**
     * Fragment 的隐藏时执行
     * @param tag 页面的标签
     */
    void onFragmentHide(String tag);

}
