package com.xhe.photoalbum.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xhe on 2017/6/16.
 * 相册照片信息
 */

public class PhotoAlbumPicture implements Parcelable, Comparable<PhotoAlbumPicture> {
    private int id;
    /**
     * 图片路径。
     */
    private String path;
    /**
     * 图片名称。
     */
    private String name;
    /**
     * 被添加到库中的时间。
     */
    private long addTime;
    /**
     * 是否选中。
     */
    private boolean isChecked;
    /**
     * 被选中的时间
     */
    private long checkedTime;

    @Override
    public int compareTo(PhotoAlbumPicture o) {
        //按照照片加入库中的时间排序
        long time = o.getAddTime() - getAddTime();
        if (time > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        else if (time < -Integer.MAX_VALUE)
            return -Integer.MAX_VALUE;
        return (int) time;
    }

    @Override
    public boolean equals(Object obj) {
        PhotoAlbumPicture p = (PhotoAlbumPicture) obj;
        return path.equals(p.getPath());
    }

    @Override
    public String toString() {
        return "PhotoAlbumPicture{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public long getCheckedTime() {
        return checkedTime;
    }

    public void setCheckedTime(long checkedTime) {
        this.checkedTime = checkedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeLong(this.addTime);
        dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
        dest.writeLong(this.checkedTime);
    }

    public PhotoAlbumPicture() {
    }

    protected PhotoAlbumPicture(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
        this.name = in.readString();
        this.addTime = in.readLong();
        this.isChecked = in.readByte() != 0;
        this.checkedTime = in.readLong();
    }

    public static final Parcelable.Creator<PhotoAlbumPicture> CREATOR = new Parcelable.Creator<PhotoAlbumPicture>() {
        public PhotoAlbumPicture createFromParcel(Parcel source) {
            return new PhotoAlbumPicture(source);
        }

        public PhotoAlbumPicture[] newArray(int size) {
            return new PhotoAlbumPicture[size];
        }
    };


    @Override
    public int hashCode() {
        return (isChecked ? 1 : 0);
    }
}
