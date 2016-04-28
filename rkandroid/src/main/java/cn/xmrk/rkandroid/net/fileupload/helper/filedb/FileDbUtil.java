package cn.xmrk.rkandroid.net.fileupload.helper.filedb;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Au61 on 2016/1/7.
 */
public class FileDbUtil {
    private Dao<FileDbModel, Integer> fileDaoOpe;
    private FileDbHelper helper;

    @SuppressWarnings("unchecked")
    public FileDbUtil(Context context)
    {
        try
        {
            helper = FileDbHelper.getHelper(context);
            fileDaoOpe = helper.getDao(FileDbModel.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 添加一个下载文件信息
     */
    public void add(FileDbModel fileDbModel)
    {
        try
        {
            if(hasFileInfo(fileDbModel.getFilePath())){
                deleteOneFileInfo(fileDbModel.getFilePath());
            }
            fileDaoOpe.create(fileDbModel);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 更新文件进度
     */
    public int updateFileInfo(String filePath,long curLength)
    {
        int _uc = 0;
        try {
            UpdateBuilder _ub = fileDaoOpe.updateBuilder();
            _ub.setWhere(_ub.where().eq("file_path",filePath));
            _uc += _ub.updateColumnValue("cur_length", curLength).update();
            return _uc;
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * 删除某条记录
     **/
    public void deleteOneFileInfo(String  filePath) {
        DeleteBuilder<FileDbModel, Integer> _db = fileDaoOpe.deleteBuilder();
        Where<FileDbModel, Integer> _where = _db.where();
        try {
            _where.eq("file_path", filePath);
            _db.delete();
        } catch (SQLException e) {
        }
    }
    /**
     * 查看是否有文件的下载上传记录
     * @return
     */
    public boolean hasFileInfo(String filePath,String url){
        return getFileInfoWithPath(filePath,url)!=null;
    }

    /**
     * 查看是否有文件的下载上传记录
     * @return
     */
    public boolean hasFileInfo(String filePath){
        return getFileInfoWithPath(filePath)!=null;
    }

    /**
     * 通过文件路径得到一个下载文件信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public FileDbModel getFileInfoWithPath(String filePath)
    {
        FileDbModel fileDbModel = null;
        try
        {
            fileDbModel = fileDaoOpe.queryBuilder().where().eq("file_path", filePath).queryForFirst();

        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        return fileDbModel;
    }

    /**
     * 通过文件路径得到一个下载文件信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public FileDbModel getFileInfoWithPath(String filePath,String url)
    {
        FileDbModel fileDbModel = null;
        try
        {
            fileDbModel = fileDaoOpe.queryBuilder().where().eq("file_path", filePath).and().eq("url",url).queryForFirst();

        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        return fileDbModel;
    }


    /**
     * 通过UserId获取所有的文章
     * @return
     */
    public List<FileDbModel> getFileList()
    {
        try
        {
            return fileDaoOpe.queryForAll();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
