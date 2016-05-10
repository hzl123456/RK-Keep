package rkkeep.keep.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by Au61 on 2016/4/27.
 */
@DatabaseTable(tableName = "NoticeInfo")
public class NoticeInfo implements Parcelable {


    /**
     * 只在长按选择的时候使用
     **/
    public boolean isCheck;

    /**
     * 信息的类别，默认为未提醒的信息。当取-1的时候取的是（非垃圾箱的信息）
     * 未提醒信息
     * 提醒信息
     * 垃圾箱信息
     **/
    public final static int NO_DUSTBIN = -1;
    public final static int NOMAL_TYPE = 0;
    public final static int TIXING_TYPE = 1;
    public final static int NOMAL_TYPE_DUSTBIN = 2;


    /**
     * 拥有者的id
     **/
    @DatabaseField(columnName = "ownerId", canBeNull = false)
    public int ownerId;

    /**
     * 标题
     **/
    @DatabaseField(columnName = "title")
    public String title;

    /**
     * 内容
     **/
    @DatabaseField(columnName = "content")
    public String content;

    /**
     * 信息的类型
     */
    @DatabaseField(columnName = "infoType")
    public int infoType;

    /**
     * 信息的id，在这里为系统的时间，毫秒值
     **/
    @DatabaseField(columnName = "infoId", id = true, unique = true)
    public long infoId;

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


    /**
     * 拥有语音
     **/
    @DatabaseField(columnName = "hasVoice")
    public boolean hasVoice;

    /**
     * 拥有图片
     **/
    @DatabaseField(columnName = "hasPic")
    public boolean hasPic;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isCheck ? (byte) 1 : (byte) 0);
        dest.writeInt(this.ownerId);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeInt(this.infoType);
        dest.writeLong(this.infoId);
        dest.writeString(this.color);
        dest.writeLong(this.editTime);
        dest.writeLong(this.remindTime);
        dest.writeInt(this.noticeTimes);
        dest.writeTypedList(infos);
        dest.writeString(this.noticeImgVoiceInfosString);
        dest.writeParcelable(this.addressInfo, flags);
        dest.writeString(this.addressInfoString);
        dest.writeByte(hasVoice ? (byte) 1 : (byte) 0);
        dest.writeByte(hasPic ? (byte) 1 : (byte) 0);
    }

    public NoticeInfo() {
    }

    protected NoticeInfo(Parcel in) {
        this.isCheck = in.readByte() != 0;
        this.ownerId = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.infoType = in.readInt();
        this.infoId = in.readLong();
        this.color = in.readString();
        this.editTime = in.readLong();
        this.remindTime = in.readLong();
        this.noticeTimes = in.readInt();
        this.infos = in.createTypedArrayList(NoticeImgVoiceInfo.CREATOR);
        this.noticeImgVoiceInfosString = in.readString();
        this.addressInfo = in.readParcelable(AddressInfo.class.getClassLoader());
        this.addressInfoString = in.readString();
        this.hasVoice = in.readByte() != 0;
        this.hasPic = in.readByte() != 0;
    }

    public static final Creator<NoticeInfo> CREATOR = new Creator<NoticeInfo>() {
        @Override
        public NoticeInfo createFromParcel(Parcel source) {
            return new NoticeInfo(source);
        }

        @Override
        public NoticeInfo[] newArray(int size) {
            return new NoticeInfo[size];
        }
    };
}
