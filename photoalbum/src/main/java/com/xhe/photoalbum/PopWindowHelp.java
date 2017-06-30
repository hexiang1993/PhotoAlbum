package com.xhe.photoalbum;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xhe.photoalbum.data.PhotoAlbumFolder;
import com.xhe.photoalbum.data.PhotoAlbumPicture;
import com.xhe.photoalbum.interfaces.OnAdapterViewItemClickLisenter;
import com.xhe.photoalbum.interfaces.OnCheckChangedLisenter;
import com.xhe.photoalbum.utils.SelectorUtils;
import com.xhe.photoalbum.widget.FixViewPager;

import java.util.List;

/**
 * Created by xhe on 2017/6/19.
 */

public class PopWindowHelp {

    private static boolean isOpen = true;
    private static int checkedImagePosition = 0;

    /**
     * 相册目录
     *
     * @param context
     * @param titleBarColor
     * @param titleTextColor
     * @param listFolder
     * @param itemClickLisenter
     * @return
     */
    public static PopupWindow initFolerPop(Context context, int titleBarColor, int titleTextColor, List<PhotoAlbumFolder> listFolder,
                                           final OnAdapterViewItemClickLisenter itemClickLisenter) {
        View view = LayoutInflater.from(context).inflate(R.layout.popwindow_album_floder, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        view.findViewById(R.id.rl_title).setBackgroundColor(titleBarColor);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setTextColor(titleTextColor);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvCancel.setTextColor(titleTextColor);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setAnimationStyle(R.style.NormalDialogAnimation);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        RecyclerView rvContentList = (RecyclerView) view.findViewById(R.id.rv_content_list);
        rvContentList.setHasFixedSize(true);
        rvContentList.setLayoutManager(new LinearLayoutManager(context));
        FolderAdapter folderAdapter = new FolderAdapter(listFolder);
        folderAdapter.setItemClickLisenter(new OnAdapterViewItemClickLisenter() {
            @Override
            public void itemClick(final View view, final int position) {
                if (isOpen) { // 反应太快，按钮点击效果出不来，故加延迟。
                    isOpen = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupWindow.dismiss();
                            itemClickLisenter.itemClick(view, position);
                            isOpen = true;
                        }
                    }, 200);
                }

            }
        });
        rvContentList.setAdapter(folderAdapter);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        return popupWindow;
    }


    /**
     * 大图预览
     *
     * @param context
     * @param titleBarColor
     * @param titleTextColor
     * @param listPhotos
     * @param listChecked
     * @param clickIndex
     * @param lisenter
     * @return
     */
    public static PopupWindow initPreviewPop(final Context context, int titleBarColor, int titleTextColor,
                                             final List<PhotoAlbumPicture> listPhotos, final List<PhotoAlbumPicture> listChecked, final int clickIndex,
                                             final OnCheckChangedLisenter lisenter, final View.OnClickListener finishListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_photo_preview, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        View title = view.findViewById(R.id.rl_title);
        title.setBackgroundColor(titleBarColor);
        final TextView tvCountPercent = (TextView) view.findViewById(R.id.tv_count_percent);
        tvCountPercent.setTextColor(titleTextColor);
        tvCountPercent.setText((clickIndex + 1) + "/" + listPhotos.size());
        FixViewPager viewPager = (FixViewPager) view.findViewById(R.id.vp_photo);
        final AppCompatCheckBox checkBox = (AppCompatCheckBox) view.findViewById(R.id.cb_photo_check);
        checkBox.setSupportButtonTintList(SelectorUtils.createColorStateList(Color.parseColor("#e0e0e0"), titleTextColor));
        final TextView tvCount = (TextView) view.findViewById(R.id.tv_checked_count);
        final TextView tvFinish = (TextView) view.findViewById(R.id.tv_checked_finish);

        //修改返回箭头图标的颜色
        ImageView ivBack = (ImageView) view.findViewById(R.id.view_back);
        Drawable drawable = context.getResources().getDrawable(R.drawable.icon_arrow2left_black);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, SelectorUtils.createColorStateList(titleTextColor, titleTextColor));
        ivBack.setImageDrawable(wrappedDrawable);

        tvCountPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (finishListener != null) {
                    finishListener.onClick(v);
                }
            }
        });
        tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (finishListener != null) {
                    finishListener.onClick(v);
                }
            }
        });
        setBtnEnabled(context, tvCount, tvFinish, listChecked);
        if (listPhotos.size() > 2)
            viewPager.setOffscreenPageLimit(2);
        final PreviewAdapter previewAdapter = new PreviewAdapter(listPhotos);
        viewPager.setAdapter(previewAdapter);
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d("Photopreview", "大图预览：onPageSelected---" + position);
                checkedImagePosition = position;
                PhotoAlbumPicture albumImage = listPhotos.get(position);
                checkBox.setChecked(albumImage.isChecked());
                tvCountPercent.setText((position + 1) + "/" + listPhotos.size());
            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);
        if (clickIndex == 0) {
            pageChangeListener.onPageSelected(0);
        }
        checkedImagePosition = clickIndex;
        viewPager.setCurrentItem(clickIndex);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Photopreview", "大图预览：onCheckedChanged---" + checkedImagePosition);
                if (lisenter != null) {
                    lisenter.onCheckedChanged(buttonView, isChecked, checkedImagePosition);
                    previewAdapter.notifyDataSetChanged();
                    setBtnEnabled(context, tvCount, tvFinish, listChecked);
                }
            }
        });
        popupWindow.setAnimationStyle(R.style.NormalDialogAnimation2);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        return popupWindow;
    }

    private static void setBtnEnabled(Context context, TextView tvCount, TextView tvFinish, List<PhotoAlbumPicture> listChecked) {
        if (listChecked != null && listChecked.size() > 0) {
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText(listChecked.size() + "");
            tvFinish.setClickable(true);
            tvFinish.setTextColor(context.getResources().getColor(R.color.tv_finish_enabled));
        } else {
            tvCount.setVisibility(View.INVISIBLE);
            tvFinish.setClickable(false);
            tvFinish.setTextColor(context.getResources().getColor(R.color.btn_disabled));
        }
    }

    /**
     * 解决7.0手机popupwindow设为view下方时的位置问题
     *
     * @param popupWindow
     * @param view
     * @param xOff
     * @param yOff
     */
    public static void showDropDown(PopupWindow popupWindow, View view, int xOff, int yOff) {
        if (Build.VERSION.SDK_INT != 24) {
            popupWindow.showAsDropDown(view, xOff, yOff);
        } else {
            // 适配 android 7.0
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int y = location[1];
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, xOff, y + view.getHeight());
        }
    }
}
