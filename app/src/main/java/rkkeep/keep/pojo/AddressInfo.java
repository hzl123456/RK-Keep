package rkkeep.keep.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 记事的地址信息
 */
public class AddressInfo implements Parcelable {

    public String addressName;

    public String addressIntro;

    public double longitude;

    public double latitude;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.addressName);
        dest.writeString(this.addressIntro);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    public AddressInfo() {
    }

    protected AddressInfo(Parcel in) {
        this.addressName = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.addressIntro=in.readString();
    }

    public static final Parcelable.Creator<AddressInfo> CREATOR = new Parcelable.Creator<AddressInfo>() {
        @Override
        public AddressInfo createFromParcel(Parcel source) {
            return new AddressInfo(source);
        }

        @Override
        public AddressInfo[] newArray(int size) {
            return new AddressInfo[size];
        }
    };
}
