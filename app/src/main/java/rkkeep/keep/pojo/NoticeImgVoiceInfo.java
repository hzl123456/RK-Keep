package rkkeep.keep.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 图片跟录音只能包含其中的一种
 */
public class NoticeImgVoiceInfo implements Parcelable {

    //图片的路径
    public String imagePic;

    //语音的路径
    public String voicePic;

    //语音的长度（毫秒值）
    public long length;

    public NoticeImgVoiceInfo(String imagePic) {
        this.imagePic = imagePic;
    }

    public NoticeImgVoiceInfo(String voicePic, long length) {
        this.voicePic = voicePic;
        this.length = length;
    }

    public static Type getListType() {
        return new TypeToken<List<NoticeImgVoiceInfo>>() {
        }.getType();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imagePic);
        dest.writeString(this.voicePic);
        dest.writeLong(this.length);
    }

    protected NoticeImgVoiceInfo(Parcel in) {
        this.imagePic = in.readString();
        this.voicePic = in.readString();
        this.length = in.readLong();
    }

    public static final Creator<NoticeImgVoiceInfo> CREATOR = new Creator<NoticeImgVoiceInfo>() {
        @Override
        public NoticeImgVoiceInfo createFromParcel(Parcel source) {
            return new NoticeImgVoiceInfo(source);
        }

        @Override
        public NoticeImgVoiceInfo[] newArray(int size) {
            return new NoticeImgVoiceInfo[size];
        }
    };
}
