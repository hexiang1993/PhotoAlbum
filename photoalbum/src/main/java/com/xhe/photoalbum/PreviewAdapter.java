package com.xhe.photoalbum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xhe.photoalbum.data.PhotoAlbumPicture;
import com.xhe.photoalbum.utils.ImageDisplayer;
import com.xhe.photoalbum.utils.Util;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by xhe on 2017/6/20.
 * 预览adapter
 */

public class PreviewAdapter extends PagerAdapter {

    private List<PhotoAlbumPicture> mAlbumImages;

    public PreviewAdapter(List<PhotoAlbumPicture> mAlbumImages) {
        this.mAlbumImages = mAlbumImages;
    }

    @Override
    public int getCount() {
        return mAlbumImages == null ? 0 : mAlbumImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(imageView);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
        final String path = mAlbumImages.get(position).getPath();
        Glide.with(imageView.getContext().getApplicationContext())
                .load(path)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        bitmap = BitmapFactory.decodeFile(path, options);

                        imageView.setImageBitmap(bitmap);
                        attacher.update();
                    }
                });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((View) object));
    }
}