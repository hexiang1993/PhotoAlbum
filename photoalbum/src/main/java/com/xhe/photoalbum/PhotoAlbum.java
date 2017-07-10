package com.xhe.photoalbum;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.gengqiquan.result.RxActivityResult;
import com.xhe.photoalbum.data.ThemeData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;


/**
 * Created by xhe on 2017/6/20.
 * 相册操作类
 */

public class PhotoAlbum {
    public static final String KEY_OUTPUT_IMAGE_PATH_LIST = "KEY_OUTPUT_IMAGE_PATH_LIST";//选择的照片路径列表

    public static final String KEY_ALBUM_MAX_LIMIT_COUNT = "KEY_ALBUM_MAX_LIMIT_COUNT";//允许选择的最大数量
    public static final String KEY_ALBUM_SHOW_CAMERA = "KEY_ALBUM_SHOW_CAMERA";//是否需要展示相机
    public static final String KEY_ALBUM_REMOVE_PATHS = "KEY_ALBUM_REMOVE_PATHS";//需要在相册中移除的照片路径

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


    private Context context;

    /**
     * 不需要在相册显示的照片路径
     */
    private ArrayList<String> listRemovePath = new ArrayList<>();

    /**
     * 最多能选择的图片数量
     * 默认为1
     */
    private int limitCount = Integer.MAX_VALUE;

    /**
     * toolbar的颜色
     */
    @ColorInt
    private int toolbarColor = ThemeData.getTitleBarColor();

    /**
     * 标题栏文字的颜色
     */
    @ColorInt
    private int titleTxetColor = ThemeData.getTitleTextColor();

    /**
     * 选择框的style drawable
     */
    @DrawableRes
    public int checkBoxDrawable = ThemeData.getCheckBoxDrawable();
    /**
     * 状态栏颜色
     */
    private int statusBarColor = ThemeData.getStatusBarColor();

    private int backgroundColor = ThemeData.getBackgroundColor();

    /**
     * 是否展示相机按钮
     * 默认不展示
     */
    private boolean showCamera = false;

    /**
     * 相册展示的列数
     */
    private int spanCount = ThemeData.getSpanCount();


    private PhotoAlbum() {
    }

    /**
     * context必须是activity或fragment的
     *
     * @param context
     */
    public PhotoAlbum(Context context) {
        this.context = context;
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

    public PhotoAlbum setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
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
     * 设置需要排除的照片路径
     * 一般用在控制不选择重复照片
     *
     * @return
     */
    public PhotoAlbum addRemovePath(@NonNull String path) {
        if (path == null || TextUtils.isEmpty(path.trim())) {
            return this;
        }
        this.listRemovePath.add(path);
        return this;
    }

    public PhotoAlbum addRemovePaths(@NonNull List<String> paths) {
        if (paths == null) {
            return this;
        }
        this.listRemovePath.addAll(paths);
        return this;
    }

    /**
     * 最终调用的启动相册
     */
    public Observable<List<String>> startAlbum() {
        ThemeData.init(new ThemeData.ThemeBuilder()
                .backgroundColor(backgroundColor)
                .titleBarColor(toolbarColor)
                .titleTextColor(titleTxetColor)
                .statusBarColor(statusBarColor)
                .checkBoxDrawable(checkBoxDrawable)
                .spanCount(spanCount)
                .build());

        Intent intent = new Intent();
        intent.putExtra(KEY_ALBUM_MAX_LIMIT_COUNT, limitCount);
        intent.putExtra(KEY_ALBUM_SHOW_CAMERA, showCamera);
        intent.putStringArrayListExtra(KEY_ALBUM_REMOVE_PATHS, listRemovePath);

        if (context == null)
            throw new NullPointerException("context must be not null");

        intent.setClass(context, PhotoAlbumActivity.class);
        return RxActivityResult.with(context)
                .startActivityWithResult(intent)
                .map(new Func1<Intent, List<String>>() {
                    @Override
                    public List<String> call(Intent intent) {
                        return parseResult(intent);
                    }
                });
    }
}
