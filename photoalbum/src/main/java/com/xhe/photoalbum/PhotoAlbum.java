package com.xhe.photoalbum;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.gengqiquan.result.RxActivityResult;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by xhe on 2017/6/20.
 * 相册操作类
 */

public class PhotoAlbum {
    public static final String KEY_OUTPUT_IMAGE_PATH_LIST = "KEY_OUTPUT_IMAGE_PATH_LIST";//选择的照片路径列表

    public static final String KEY_ALBUM_SPAN_COUNT = "KEY_ALBUM_SPAN_COUNT";//相册显示的列数
    public static final String KEY_ALBUM_MAX_LIMIT_COUNT = "KEY_ALBUM_MAX_LIMIT_COUNT";//允许选择的最大数量
    public static final String KEY_ALBUM_TITLEBAR_COLOR = "KEY_ALBUM_TITLEBAR_COLOR";//相册显示的titlebar的颜色
    public static final String KEY_ALBUM_TITLE_TEXT_COLOR = "KEY_ALBUM_TITLE_TEXT_COLOR";//相册显示的titlebar上文字的颜色
    public static final String KEY_ALBUM_SHOW_CAMERA = "KEY_ALBUM_SHOW_CAMERA";//是否需要展示相机

    /**
     * 解析接受到的结果，开发者需要判断{@code resultCode = Activity.RESULT_OK}.
     *
     * @param intent {@code Intent} from {@code onActivityResult(int, int, Intent)}.
     * @return {@code List<String>}.
     */
    @NonNull
    public static List<String> parseResult(Intent intent) {
        List<String> pathList = intent.getStringArrayListExtra(KEY_OUTPUT_IMAGE_PATH_LIST);
        if (pathList == null)
            pathList = Collections.emptyList();
        return pathList;
    }

    /**
     * 整体设置相册的默认值
     * 需要在Application中调用
     *
     * @param barColr   默认标题栏颜色，传null为不设置
     * @param textColor 默认标题栏文字颜色，传null为不设置
     * @param count     默认相册展示列数，只能为大于1的整数
     */
    public static void init(@ColorInt Integer barColr, @ColorInt Integer textColor, @IntRange(from = 1) Integer count) {
        //默认标题栏颜色值
        //默认标题栏文字颜色值
        //默认相册列数
        if (barColr != null) {
            toolbarColor = barColr;
        }
        if (textColor != null) {
            titleTxetColor = textColor;
        }

        spanCount = count;
    }


    /**
     * 接收文件的activity
     */
    private AppCompatActivity activity;

    /**
     * 接收文件的fragment
     */
    private Fragment fragment;

    /**
     * 最多能选择的图片数量
     * 默认为1
     */
    private int limitCount = Integer.MAX_VALUE;

    /**
     * toolbar的颜色
     */
    private static int toolbarColor = Color.WHITE;

    /**
     * 状态栏的颜色
     */
    private static int titleTxetColor = Color.parseColor("#333333");
    /**
     * 是否展示相机按钮
     * 默认不展示
     */
    private boolean showCamera = false;

    /**
     * 相册展示的列数
     */
    private static int spanCount = 3;

    private static PhotoAlbum photoAlbum;

    private PhotoAlbum() {
    }

    public PhotoAlbum(AppCompatActivity activity) {
        this.activity = activity;
    }

    public PhotoAlbum(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * 设置限制的最大选择数量
     *
     * @param limitCount
     * @return
     */
    public PhotoAlbum setLimitCount(int limitCount) {
        this.limitCount = limitCount;
        return this;
    }

    /**
     * 设置标题栏的颜色
     *
     * @param toolbarColor
     * @return
     */
    public PhotoAlbum setTitlebarColor(@ColorInt int toolbarColor) {
        this.toolbarColor = toolbarColor;
        return this;
    }

    /**
     * 设置标题栏文字的颜色
     *
     * @param statusBarColor
     * @return
     */
    public PhotoAlbum setTitleTextColor(@ColorInt int statusBarColor) {
        this.titleTxetColor = statusBarColor;
        return this;
    }

    /**
     * 设置是否需要显示相机
     * 默认不显示
     *
     * @param showCamera
     * @return
     */
    public PhotoAlbum setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
        return this;
    }

    /**
     * 设置列表展示的列数
     *
     * @param spanCount
     * @return
     */
    public PhotoAlbum setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        return this;
    }

    /**
     * 最终调用的启动相册
     */
//    public void startAlbum() {
//        Intent intent = new Intent();
//        intent.putExtra(KEY_ALBUM_TITLEBAR_COLOR, toolbarColor);
//        intent.putExtra(KEY_ALBUM_TITLE_TEXT_COLOR, titleTxetColor);
//
//        intent.putExtra(KEY_ALBUM_MAX_LIMIT_COUNT, limitCount);
//        intent.putExtra(KEY_ALBUM_SHOW_CAMERA, showCamera);
//        intent.putExtra(KEY_ALBUM_SPAN_COUNT, spanCount);
//
//        if (activity != null) {
//            intent.setClass(activity, PhotoAlbumActivity.class);
//            activity.startActivityForResult(intent, requestCode);
//            return;
//        }
//
//        if (fragment != null) {
//            intent.setClass(fragment.getContext(), PhotoAlbumActivity.class);
//            fragment.startActivityForResult(intent, requestCode);
//        }
//    }

    /**
     * 最终调用的启动相册
     *
     * @param resultCallBack 回调中处理activityForResult的结果
     */
    public void startAlbum(@NonNull final ActivityForResultCallBack resultCallBack) {
        Intent intent = new Intent();
        intent.putExtra(KEY_ALBUM_TITLEBAR_COLOR, toolbarColor);
        intent.putExtra(KEY_ALBUM_TITLE_TEXT_COLOR, titleTxetColor);

        intent.putExtra(KEY_ALBUM_MAX_LIMIT_COUNT, limitCount);
        intent.putExtra(KEY_ALBUM_SHOW_CAMERA, showCamera);
        intent.putExtra(KEY_ALBUM_SPAN_COUNT, spanCount);

        if (activity != null) {
            intent.setClass(activity, PhotoAlbumActivity.class);
            RxActivityResult.with(activity)
                    .startActivityWithResult(intent)
                    .subscribe(new Consumer<Intent>() {
                        @Override
                        public void accept(Intent intent) throws Exception {
                            resultCallBack.result(intent);
                        }
                    });
            return;
        }

        if (fragment != null) {
            intent.setClass(fragment.getContext(), PhotoAlbumActivity.class);
            RxActivityResult.with(fragment)
                    .startActivityWithResult(intent)
                    .subscribe(new Consumer<Intent>() {
                        @Override
                        public void accept(Intent intent) throws Exception {
                            resultCallBack.result(intent);
                        }
                    });
            return;
        }

        throw new NullPointerException("Activity or Fragment not null");
    }

    public interface ActivityForResultCallBack {
        void result(Intent data);
    }
}
