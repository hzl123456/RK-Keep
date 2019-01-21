package rkkeep.keep.util;

import cn.xmrk.rkandroid.utils.CommonUtil;
import cn.xmrk.rkandroid.utils.SharedPreferencesUtil;
import rkkeep.keep.application.KeepApplication;
import rkkeep.keep.pojo.UserInfo;

/**
 * Created by Au61 on 2016/4/26.
 */
public class UserInfoUtil {

    private static SharedPreferencesUtil mSharedPreferencesUtil;

    public static void initDefaultUserInfo() {
        if (mSharedPreferencesUtil == null) {
            mSharedPreferencesUtil = new SharedPreferencesUtil(KeepApplication.getInstance(), "USER");
        }
        //为空的话就预设置个
        if (getUserInfo() == null) {
            UserInfo info = new UserInfo();
            info.userId = -1;
            info.userName = "未登录用户";
            info.userIntro = "";
            setUserInfo(info);
        }
    }

    public static UserInfo getUserInfo() {
        return CommonUtil.getGson().fromJson(mSharedPreferencesUtil.getString("user", null), UserInfo.class);
    }

    public static void setUserInfo(UserInfo info) {
        mSharedPreferencesUtil.putString("user", CommonUtil.getGson().toJson(info));
    }
}
