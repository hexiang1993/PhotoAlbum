package com.xhe.photoalbum.data;

import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;

import com.xhe.photoalbum.R;

/**
 * 主题通用样式数据类
 * Created by gengqiquan on 2017/7/10.
 */

public class ThemeData {
    private static ThemeData instance;

    protected ThemeData(ThemeData.ThemeBuilder builder) {
        this.backgroundColor = builder.backgroundColor;
        this.titleBarColor = builder.titleBarColor;
        this.titleTextColor = builder.titleTextColor;
        this.statusBarColor = builder.statusBarColor;
        this.checkBoxDrawable = builder.checkBoxDrawable;
        this.spanCount = builder.spanCount;
        this.singleChoiceShowBox = builder.singleChoiceShowBox;
    }


    public static void init(ThemeData themeData) {
        if (instance == null)
            synchronized (ThemeData.class) {
                if (instance == null)
                    instance = themeData;
            }
    }

    /**
     * 单选对时候是否展示选择框
     */
    private static boolean singleChoiceShowBox = true;

    /**
     * 界面背景颜色
     */
    private static int backgroundColor = Color.WHITE;
    /**
     * toolbar的颜色
     */
    @ColorInt
    private static int titleBarColor = Color.parseColor("#009def");

    /**
     * 状态栏的颜色
     */
    @ColorInt
    private static int titleTextColor = Color.WHITE;
    /**
     * 选择框的style drawable
     */
    @DrawableRes
    public static int checkBoxDrawable = R.drawable.checkbox_style;

    /**
     * 状态栏颜色
     */
    private static int statusBarColor = Color.parseColor("#55000000");

    /**
     * 相册展示的列数
     */
    private static int spanCount = 3;

    public static int getSpanCount() {
        return spanCount;
    }

    public static int getBackgroundColor() {
        return backgroundColor;
    }

    public static int getTitleBarColor() {
        return titleBarColor;
    }

    public static int getTitleTextColor() {
        return titleTextColor;
    }

    public static int getCheckBoxDrawable() {
        return checkBoxDrawable;
    }

    public static int getStatusBarColor() {
        return statusBarColor;
    }

    public static boolean isSingleChoiceShowBox() {
        return singleChoiceShowBox;
    }

    public static final class ThemeBuilder {
        /**
         * 单选对时候是否展示选择框
         */
        private boolean singleChoiceShowBox = true;

        private int backgroundColor = Color.WHITE;

        /**
         * toolbar的颜色
         */
        @ColorInt
        private int titleBarColor = Color.parseColor("#009def");

        /**
         * 状态栏的颜色
         */
        @ColorInt
        private int titleTextColor = Color.WHITE;
        /**
         * 选择框的style drawable
         */
        @DrawableRes
        private int checkBoxDrawable = R.drawable.checkbox_style;

        /**
         * 状态栏颜色
         */
        private int statusBarColor = Color.parseColor("#55000000");

        /**
         * 相册展示的列数
         */
        private int spanCount = 3;

        public ThemeBuilder spanCount(@IntRange(from = 1) int count) {
            spanCount = count;
            return this;
        }


        public ThemeBuilder backgroundColor(@ColorInt int color) {
            backgroundColor = color;
            return this;
        }

        public ThemeBuilder titleBarColor(@ColorInt int toolbarColor) {
            this.titleBarColor = toolbarColor;
            return this;
        }

        public ThemeBuilder titleTextColor(@ColorInt int titleTxetColor) {
            this.titleTextColor = titleTxetColor;
            return this;
        }

        public ThemeBuilder checkBoxDrawable(@DrawableRes int checkBoxDrawable) {
            this.checkBoxDrawable = checkBoxDrawable;
            return this;
        }

        public ThemeBuilder statusBarColor(@ColorInt int statusBarColor) {
            this.statusBarColor = statusBarColor;
            return this;
        }

        public ThemeBuilder singleChoiceShowBox(boolean singleChoiceShowBox) {
            this.singleChoiceShowBox = singleChoiceShowBox;
            return this;
        }

        @CheckResult(suggest = "ThemeBuilder must be setted in to ThemeData.init(ThemeBuilder builder)")
        public ThemeData build() {

            return new ThemeData(this);
        }
    }
}
