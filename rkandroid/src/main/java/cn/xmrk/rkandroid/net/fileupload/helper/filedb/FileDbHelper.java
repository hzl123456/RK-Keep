package cn.xmrk.rkandroid.net.fileupload.helper.filedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Au61 on 2016/1/7.
 */
public class FileDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String TABLE_NAME = "sqlite-file.db";
    private static final int DB_VERSION = 1;
    /**
     * FileDbModelDao ，每张表对于一个
     */
    private Dao<FileDbModel, Integer> FileDbModelDao;

    private FileDbHelper(Context context)
    {
        super(context, TABLE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource)
    {
        try
        {
            TableUtils.createTable(connectionSource, FileDbModel.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        try
        {
            TableUtils.dropTable(connectionSource, FileDbModel.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static FileDbHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized FileDbHelper getHelper(Context context)
    {
        if (instance == null)
        {
            synchronized (FileDbHelper.class)
            {
                if (instance == null)
                    instance = new FileDbHelper(context);
            }
        }

        return instance;
    }

    /**
     * 获得FileDbModelDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<FileDbModel, Integer> getFileDbModelDao() throws SQLException
    {
        if (FileDbModelDao == null)
        {
            FileDbModelDao = getDao(FileDbModel.class);
        }
        return FileDbModelDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close()
    {
        super.close();
        FileDbModelDao = null;
    }
}
