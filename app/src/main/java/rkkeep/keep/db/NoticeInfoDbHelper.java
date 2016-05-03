package rkkeep.keep.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import org.apache.log4j.Logger;

import cn.xmrk.rkandroid.application.RKApplication;
import rkkeep.keep.util.UserInfoUtil;

/**
 * Created by Au61 on 2016/5/3.
 */
public class NoticeInfoDbHelper {

    private static final Logger log = Logger.getLogger("ChatDBUtils");

    public String dbKey;

    public int msgOwner;

    OpenHelper mOpenHelper;

    public NoticeInfoDbHelper() {
        this("rk_keep_default", UserInfoUtil.getUserInfo().userId);
    }

    public NoticeInfoDbHelper(String dbKey, int msgOwner) {
        this(RKApplication.getInstance(), dbKey, msgOwner);
    }

    public NoticeInfoDbHelper(Context context, String dbKey, int msgOwner) {
        this.dbKey = dbKey;
        this.msgOwner = msgOwner;
        open(context);
    }

    public void open(Context context) {
        if (mOpenHelper == null || !mOpenHelper.isOpen()) {
            mOpenHelper = OpenHelper.getInstance(context, dbKey);
        }
    }

    public void close() {
        if (mOpenHelper != null) {
            mOpenHelper.close();
        }

    }

    public int getMsgOwner() {
        return msgOwner;
    }


    public Dao getChatDao() {
        return mOpenHelper.getNoticeInfoDao();
    }

}
