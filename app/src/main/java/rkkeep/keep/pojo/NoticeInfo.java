package rkkeep.keep.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by Au61 on 2016/4/27.
 */
@DatabaseTable(tableName = "NoticeInfo")
public class NoticeInfo {

    /**
     * 拥有者的id
     **/
    @DatabaseField(columnName = "id", unique = true, canBeNull = false)
    public int ownerId;

    /**
     * 颜色色值，默认为白色
     **/
    @DatabaseField(columnName = "color")
    public String color = "#ffffffff";

    /**
     * 编辑时间
     **/
    @DatabaseField(columnName = "editTime")
    public long editTime;

    /**
     * 提醒时间
     **/
    @DatabaseField(columnName = "remindTime")
    public long remindTime;

    /**
     * 已经提醒的次数
     **/
    @DatabaseField(columnName = "noticeTimes")
    public int noticeTimes;

    /**
     * 存储图片语音信息
     **/
    public List<NoticeImgVoiceInfo> infos;

    @DatabaseField(columnName = "infos")
    public String noticeImgVoiceInfosString;

    /**
     * 地址信息
     **/
    public AddressInfo addressInfo;

    @DatabaseField(columnName = "addressInfo")
    public String addressInfoString;
}
