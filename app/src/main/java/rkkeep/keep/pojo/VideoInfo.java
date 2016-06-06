package rkkeep.keep.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import cn.xmrk.rkandroid.utils.StringUtil;

/**
 * Created by Au61 on 2016/5/18.
 */
public class VideoInfo implements Parcelable {

    public String videoPath;

    public String videoName;

    public String imagePath;

    public int watchLength;

    public VideoInfo(){

    }
    public static Type getListType() {
        return new TypeToken<List<VideoInfo>>() {
        }.getType();
    }

    public VideoInfo(String videoPath, String imagePath) {
        this.videoPath = videoPath;
        this.imagePath = imagePath;
        this.videoName = StringUtil.getFileName(videoPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.videoPath);
        dest.writeString(this.videoName);
        dest.writeString(this.imagePath);
        dest.writeInt(this.watchLength);
    }

    protected VideoInfo(Parcel in) {
        this.videoPath = in.readString();
        this.videoName = in.readString();
        this.imagePath = in.readString();
        this.watchLength = in.readInt();
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel source) {
            return new VideoInfo(source);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };
}
