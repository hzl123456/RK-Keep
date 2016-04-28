package rkkeep.keep.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.utils.StringUtil;
import rkkeep.keep.pojo.UserInfo;

public class OpenHelper extends OrmLiteSqliteOpenHelper {

    private final Logger log = Logger.getLogger("OpenHelper");

    public static final String DB_NAME = "RK_KEEP";
    public static final int DB_VERSION = 1;

    private transient Integer useCount = 0;
    private String key;

    private Dao<UserInfo, Integer> mUserInfoDao;

    private static HashMap<String, OpenHelper> ohs = new HashMap<>();

    /**
     * 实例化的时候，都有保存key与数据库文件进行关联，getInstance需要使用key把对应的数据库get出来
     *
     * @param key
     * @return
     */
    public final static OpenHelper getInstance(Context context, String key) {
        synchronized (ohs) {
            OpenHelper _oh = ohs.get(key);
            if (_oh == null) {
                _oh = new OpenHelper(context, key);
                ohs.put(key, _oh);
            }
            _oh.useCount++;
            return _oh;
        }
    }

    public OpenHelper(Context context, String key) {
        this(context, getDbPath(key), null, DB_VERSION);
        this.key = key;
    }

    /**
     * OpenHelper有可能被拓展，为了依然能使用这个来进行管理，所以实例化通过这个方法进行反射完成实例化
     *
     * @param key
     * @param cls
     * @return
     */
    public final static OpenHelper getInstance(String key, Class<? extends OpenHelper> cls) throws NoSuchMethodException {
        synchronized (ohs) {
            OpenHelper _oh = ohs.get(key);
            if (_oh == null) {
                Constructor<? extends OpenHelper> _constructor = null;
                try {
                    _constructor = cls.getConstructor(String.class);
                } catch (NoSuchMethodException e) {
                    throw new NoSuchMethodException("没有只带String参数的构造");
                }
                try {
                    if (!_constructor.isAccessible())
                        _constructor.setAccessible(true);
                    _oh = _constructor.newInstance(key);
                } catch (InstantiationException e) {
                    Logger.getLogger("OpenHelper").error("getInstance", e);
                } catch (IllegalAccessException e) {
                    Logger.getLogger("OpenHelper").error("getInstance", e);
                } catch (InvocationTargetException e) {
                    Logger.getLogger("OpenHelper").error("getInstance", e);
                }
                if (_oh == null)
                    _oh = new OpenHelper(key);
                ohs.put(key, _oh);
            }
            _oh.useCount++;
            return _oh;
        }
    }


    @Override
    public void close() {
        synchronized (useCount) {
            useCount--;
            log.debug("[close] useCount == " + useCount.intValue());
            if (useCount <= 0) {
                useCount = 0;
                super.close();
                ohs.remove(key);
            }
        }
    }

    private static String getDbPath(String key) {
        String _dbPath = DB_NAME;
        if (!StringUtil.isEmptyString(key)) {
            _dbPath = key + "-" + DB_NAME;
        }
        return _dbPath;
    }

    public OpenHelper(String key) {
        this(RKApplication.getInstance(), getDbPath(key), null, DB_VERSION);
        this.key = key;
    }

    public OpenHelper(Context context, String databaseName,
                      CursorFactory factory, int databaseVersion, File configFile) {
        super(context, databaseName, factory, databaseVersion, configFile);
    }

    public OpenHelper(Context arg0, String arg1, CursorFactory arg2, int arg3,
                      InputStream arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
    }

    public OpenHelper(Context context, String databaseName,
                      CursorFactory factory, int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }

    public OpenHelper(Context context, String databaseName,
                      CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        log.debug("[onCreate]");
        createTable();
    }

    private void createTable() {
        try {
            TableUtils.createTableIfNotExists(connectionSource, UserInfo.class);
        } catch (SQLException e) {
            log.error("数据库创建失败", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
    }

    /**
     * 获取数据库中，表字段名
     *
     * @param tableName
     * @return
     */
    public List<String> obtainTableField(SQLiteDatabase db, String tableName) {
        Cursor c = db.rawQuery(String.format("PRAGMA table_info(%s)", tableName), null);
        try {
            if (c == null || c.getCount() == 0) {
                return new ArrayList<String>(0);
            }
            List<String> fields = new ArrayList<String>(c.getCount());
            int nameIndex = c.getColumnIndex("name");
            c.moveToFirst();
            do {
                fields.add(c.getString(nameIndex));
            } while (c.moveToNext());
            return fields;
        } finally {
            c.close();
        }
    }

    public Dao<UserInfo, Integer> getUserInfoDao() {
        if (mUserInfoDao == null) {
            try {
                mUserInfoDao = getDao(UserInfo.class);
            } catch (SQLException e) {
                return null;
            }
        }
        return mUserInfoDao;
    }
}
