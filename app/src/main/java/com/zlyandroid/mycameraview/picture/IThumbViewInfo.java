package com.zlyandroid.mycameraview.picture;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

public class IThumbViewInfo implements Parcelable {

    private String url;  //图片地址
    private Rect mBounds; // 记录坐标
    private String user = "用户字段";
    private String videoUrl;

    public IThumbViewInfo(String url) {
        this.url = url;
    }
    public IThumbViewInfo(String videoUrl,String url) {
        this.url = url;
        this.videoUrl = videoUrl;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    public Rect getBounds() {//将你的图片显示坐标字段返回
        return mBounds;
    }
    public void setBounds(Rect bounds) {
        mBounds = bounds;
    }

    protected IThumbViewInfo(Parcel in) {
        this.url = in.readString();
        this.mBounds = in.readParcelable(Rect.class.getClassLoader());
        this.user = in.readString();
        this.videoUrl = in.readString();
    }

    public static final Parcelable.Creator<IThumbViewInfo> CREATOR = new Parcelable.Creator<IThumbViewInfo>() {
        @Override
        public IThumbViewInfo createFromParcel(Parcel source) {
            return new IThumbViewInfo(source);
        }

        @Override
        public IThumbViewInfo[] newArray(int size) {
            return new IThumbViewInfo[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeParcelable(this.mBounds, flags);
        dest.writeString(this.user);
        dest.writeString(this.videoUrl);
    }
}
