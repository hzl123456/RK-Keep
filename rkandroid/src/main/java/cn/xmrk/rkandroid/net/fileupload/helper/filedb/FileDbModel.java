package cn.xmrk.rkandroid.net.fileupload.helper.filedb;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Au61 on 2016/1/7.
 */
@DatabaseTable(tableName = "db_file")
public class FileDbModel {
    @DatabaseField(columnName = "url")
    private String url;
    @DatabaseField(columnName = "file_path")
    private String filePath;
    @DatabaseField(columnName = "file_length")
    private long fileLength;
    @DatabaseField(columnName = "cur_length")
    private long curLength;
    @DatabaseField(columnName = "is_upload")
    private boolean isUpload;

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public FileDbModel(){}

    public FileDbModel(String url, String filePath, long fileLength, long curLength) {
        this.url = url;
        this.filePath = filePath;
        this.fileLength = fileLength;
        this.curLength = curLength;
    }

    public FileDbModel(String url, String filePath, long fileLength, long curLength, boolean isUpload) {
        this.url = url;
        this.filePath = filePath;
        this.fileLength = fileLength;
        this.curLength = curLength;
        this.isUpload = isUpload;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getCurLength() {
        return curLength;
    }

    public void setCurLength(long curLength) {
        this.curLength = curLength;
    }



    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }
}
