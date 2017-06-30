package com.xhe.photoalbum.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.SimpleTarget;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;


/**
 * Created by xhe on 2016/7/13.
 */
@TargetApi(10)
public class ImageLoader {
    private static ImageLoader imageLoader;

    private ImageLoader(Context context) {
        init(context);
    }

    public static ImageLoader getInstance(Context context) {
        if (imageLoader != null) {
            return imageLoader;
        }

        synchronized (ImageLoader.class) {
            if (imageLoader == null) {
                imageLoader = new ImageLoader(context.getApplicationContext());
            }
            return imageLoader;
        }
    }

    public void init(Context context) {
        context = context.getApplicationContext();
        int memoryCacheSize = calculateMemoryCacheSize(context);
        if (memoryCacheSize > 1024 * 1024 * 20) {
            memoryCacheSize = 1024 * 1024 * 20;
        }
//        GlideBuilder builder = new GlideBuilder(context);
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, DEFAULT_DISK_CACHE_SIZE));
//        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, DEFAULT_DISK_CACHE_DIR, DEFAULT_DISK_CACHE_SIZE));
//        builder.setBitmapPool(new LruBitmapPool(20 * 1024 * 1024));
//        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
//        Glide.setup(builder);

    }

    public void loadThumbnail(@NonNull String url, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .thumbnail(0.2f)
                .dontAnimate()
                .into(imageView);
    }

    public void load(@NonNull String url, @NonNull ImageView imageView,int width,int height){
        Glide.with(imageView.getContext())
                .load(url)
                .override(width,height)
                .dontAnimate()
                .skipMemoryCache(false)
                .into(imageView);
    }

    public void load(@NonNull String url, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .dontAnimate()
                .skipMemoryCache(false)
                .into(imageView);
    }

    public void load(@NonNull String url, @NonNull final ImageView imageView,SimpleTarget<GlideDrawable> target) {
        Glide.with(imageView.getContext())
                .load(url)
                .dontAnimate()
                .skipMemoryCache(false)
                .into(target);
    }

    public void loadDrawable(@DimenRes int img_id, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(img_id)
                .dontAnimate()
                .into(imageView);
    }

    public void loadGif(@DimenRes int img_id, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(img_id)
                .asGif()
                .into(imageView);
    }


    public int calculateMemoryCacheSize(Context context) {
        ActivityManager am = getService(context, ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && SDK_INT >= HONEYCOMB) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
        }
        // Target ~15% of the available heap.
        return 1024 * 1024 * memoryClass / 7;
    }

    @TargetApi(HONEYCOMB)
    public static class ActivityManagerHoneycomb {
        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }
}
