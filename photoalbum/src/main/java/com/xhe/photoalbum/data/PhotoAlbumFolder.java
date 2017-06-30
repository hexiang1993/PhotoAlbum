package com.xhe.photoalbum.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhe on 2017/6/16.
 * 相册文件夹目录
 */

public class PhotoAlbumFolder implements Parcelable {
    private int id;
    /**
     * 文件夹名称
     */
    private String name;
    /**
     * 所有图片
     */
    private ArrayList<PhotoAlbumPicture> photos = new ArrayList<>();
    /**
     * 文件夹是否被选中。
     */
    private boolean isChecked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PhotoAlbumPicture> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PhotoAlbumPicture> photos) {
        this.photos = photos;
    }

    public void addPhoto(PhotoAlbumPicture picture){
        this.photos.add(picture);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeList(this.photos);
        dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
    }

    public PhotoAlbumFolder() {
    }

    protected PhotoAlbumFolder(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.photos = new ArrayList<PhotoAlbumPicture>();
        in.readList(this.photos, List.class.getClassLoader());
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PhotoAlbumFolder> CREATOR = new Parcelable.Creator<PhotoAlbumFolder>() {
        public PhotoAlbumFolder createFromParcel(Parcel source) {
            return new PhotoAlbumFolder(source);
        }

        public PhotoAlbumFolder[] newArray(int size) {
            return new PhotoAlbumFolder[size];
        }
    };
}
