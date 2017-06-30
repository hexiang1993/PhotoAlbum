package com.xhe.photoalbum;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xhe.photoalbum.data.PhotoAlbumPicture;
import com.xhe.photoalbum.utils.ImageLoader;

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
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(imageView);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
        ImageLoader.getInstance(container.getContext()).load(mAlbumImages.get(position).getPath(), imageView, new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                imageView.setImageDrawable(resource);
                attacher.update();
                attacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((View) object));
    }
}