package com.xhe.photoalbum.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xhe.photoalbum.utils.SelectorUtils;

/**
 * Created by xhe on 2017/6/17.
 * 自定义标题栏
 */

public class CustomTitlebar extends RelativeLayout {
    private TextView tvLeft;
    private TextView tvRight;
    private TextView tvCenter;

    public CustomTitlebar(Context context) {
        this(context, null);
    }


    public CustomTitlebar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitlebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        tvCenter = new TextView(context);
        tvLeft = new TextView(context);
        tvRight = new TextView(context);

        tvLeft.setTextColor(Color.parseColor("#333333"));
        tvCenter.setTextColor(Color.BLACK);
        tvRight.setTextColor(Color.parseColor("#333333"));
        tvCenter.setTextSize(17);
        tvLeft.setTextSize(15);
        tvRight.setTextSize(15);
        tvCenter.setIncludeFontPadding(false);
        tvLeft.setIncludeFontPadding(false);
        tvRight.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.addRule(CENTER_IN_PARENT);
        tvCenter.setSingleLine();
        addView(tvCenter, lp1);

        LayoutParams lp2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.addRule(CENTER_VERTICAL);
        lp2.addRule(ALIGN_PARENT_LEFT);
        tvLeft.setSingleLine();
        tvLeft.setGravity(Gravity.CENTER_VERTICAL);
        addView(tvLeft, lp2);

        LayoutParams lp3 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp3.addRule(CENTER_VERTICAL);
        tvRight.setSingleLine();
        lp3.addRule(ALIGN_PARENT_RIGHT);
        addView(tvRight, lp3);

        int padding = dp2px(context, 15);
        setPadding(padding, 0, padding, 0);
        setBackgroundColor(Color.WHITE);

        if (attrs == null) {
            return;
        }

    }

    public CustomTitlebar setCenterText(String text) {
        if (isEmpty(text)) {
            return this;
        }
        tvCenter.setText(text);
        return this;
    }

    public CustomTitlebar setCenterTextSize(int size) {
        tvCenter.setTextSize(size);
        return this;
    }

    public CustomTitlebar setLeftImage(@DrawableRes int imgId,int color) {
        Drawable drawable = getResources().getDrawable(imgId);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, SelectorUtils.createColorStateList(color,color));
        wrappedDrawable.setBounds(0, 0, dp2px(tvLeft.getContext(),10), dp2px(tvLeft.getContext(),18));
        // 这一步必须要做,否则不会显示.
        tvLeft.setCompoundDrawables(wrappedDrawable, null, null, null);
        tvLeft.setCompoundDrawablePadding(dp2px(getContext(), 10));
        return this;
    }

    public CustomTitlebar setLeftText(String text) {
        if (isEmpty(text)) {
            text = " ";
        }
        tvLeft.setText(text);
        return this;
    }

    public CustomTitlebar setLeftTextSize(int size) {
        tvLeft.setTextSize(size);
        return this;
    }

    public CustomTitlebar setRightImage(@DrawableRes int imgId) {
        Drawable drawable = getResources().getDrawable(imgId);
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvRight.setCompoundDrawables(drawable, null, null, null);
        return this;
    }

    public CustomTitlebar setRightText(String text) {
        if (isEmpty(text)) {
            text = " ";
        }
        tvRight.setText(text);
        return this;
    }

    public CustomTitlebar setRightTextSize(int size) {
        tvRight.setTextSize(size);
        return this;
    }

    public CustomTitlebar setLeftClickLisenter(@NonNull View.OnClickListener lisenter) {
        tvLeft.setOnClickListener(lisenter);
        return this;
    }

    public CustomTitlebar setRightClickLisenter(@NonNull View.OnClickListener lisenter) {
        tvRight.setOnClickListener(lisenter);
        return this;
    }

    public CustomTitlebar setCenterClickLisenter(@NonNull View.OnClickListener lisenter) {
        tvCenter.setOnClickListener(lisenter);
        return this;
    }

    public CustomTitlebar setLeftColor(@ColorInt int color) {
        tvLeft.setTextColor(color);
        return this;
    }

    public CustomTitlebar setRightColor(@ColorInt int color) {
        tvRight.setTextColor(color);
        return this;
    }

    public CustomTitlebar setCenterColor(@ColorInt int color) {
        tvCenter.setTextColor(color);
        return this;
    }

    private boolean isEmpty(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * dp转px
     *
     * @param context
     * @return
     */
    public int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
