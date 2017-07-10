package com.xhe.photoalbum.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xhe on 2017/6/16.
 * 相册数据获取类
 */

public class PhotoAlbumScaner {

    private static PhotoAlbumScaner instance;

    private PhotoAlbumScaner() {
    }

    public static PhotoAlbumScaner getInstance() {
        if (instance == null)
            synchronized (PhotoAlbumScaner.class) {
                if (instance == null)
                    instance = new PhotoAlbumScaner();
            }
        return instance;
    }

    /**
     * 获取文件夹列表及其相应照片列表
     */
    public List<PhotoAlbumFolder> getPhotoAlbum(Context context, List<String> removePaths) {
        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
        Map<String, PhotoAlbumFolder> albumFolderMap = new HashMap<>();

        PhotoAlbumFolder allImageAlbumFolder = new PhotoAlbumFolder();
        allImageAlbumFolder.setChecked(true);
        allImageAlbumFolder.setName("所有照片");

        while (cursor.moveToNext()) {
            int imageId = cursor.getInt(0);
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //忽略视频和Gif文件
            if (imagePath == null || imagePath.endsWith(".mp4") || imagePath.endsWith(".gif")) {
                continue;
            }
            Log.d("PhotoAlbum", "照片路径：" + imagePath);
            //不添加需要移除的照片路径的照片
            if (removePaths != null && removePaths.contains(imagePath)) {
                continue;
            }

            String imageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            long addTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

            int bucketId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            String bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

            PhotoAlbumPicture albumImage = new PhotoAlbumPicture();
            albumImage.setId(imageId);
            albumImage.setPath(imagePath);
            albumImage.setName(imageName);
            albumImage.setAddTime(addTime);

            allImageAlbumFolder.addPhoto(albumImage);

            PhotoAlbumFolder albumFolder = albumFolderMap.get(bucketName);
            if (albumFolder != null) {
                albumFolder.addPhoto(albumImage);
            } else {
                albumFolder = new PhotoAlbumFolder();
                albumFolder.setId(bucketId);
                albumFolder.setName(bucketName);
                albumFolder.addPhoto(albumImage);

                albumFolderMap.put(bucketName, albumFolder);
            }
        }
        cursor.close();
        List<PhotoAlbumFolder> albumFolders = new ArrayList<>();

        //按照照片加入库的时候作降序
        Collections.sort(allImageAlbumFolder.getPhotos());
        albumFolders.add(allImageAlbumFolder);

        for (Map.Entry<String, PhotoAlbumFolder> folderEntry : albumFolderMap.entrySet()) {
            PhotoAlbumFolder albumFolder = folderEntry.getValue();
            Collections.sort(albumFolder.getPhotos());
            albumFolders.add(albumFolder);
        }
        return albumFolders;
    }

    /**
     * 设置获取图片的属性
     */
    private static final String[] STORE_IMAGES = {
            /**
             * 图片ID。
            */
            MediaStore.Images.Media._ID,
            /**
             * 图片完整路径。
            */
            MediaStore.Images.Media.DATA,
            /**
             * 文件名称。
            */
            MediaStore.Images.Media.DISPLAY_NAME,
            /**
             * 被添加到库中的时间。
            */
            MediaStore.Images.Media.DATE_ADDED,
            /**
             * 目录ID。
            */
            MediaStore.Images.Media.BUCKET_ID,
            /**
             * 所在文件夹名称。
            */
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    };
}
