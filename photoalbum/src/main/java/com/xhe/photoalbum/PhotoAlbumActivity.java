package com.xhe.photoalbum;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xhe.photoalbum.data.PhotoAlbumFolder;
import com.xhe.photoalbum.data.PhotoAlbumPicture;
import com.xhe.photoalbum.data.PhotoAlbumScaner;
import com.xhe.photoalbum.data.ThemeData;
import com.xhe.photoalbum.interfaces.OnAdapterViewItemClickLisenter;
import com.xhe.photoalbum.interfaces.OnCheckChangedLisenter;
import com.xhe.photoalbum.utils.ImageDisplayer;
import com.xhe.photoalbum.utils.Util;
import com.xhe.photoalbum.widget.CustomTitlebar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * Created by xhe on 2017/6/17.
 * 相册展示activity
 */
public class PhotoAlbumActivity extends AppCompatActivity implements OnCheckChangedLisenter {
    private Context context;
    private RecyclerView recyclerView;
    private TextView tvPreview;
    private TextView tvCheckedCount;
    private TextView tvCheckedFinish;
    private CustomTitlebar titlebar;
    private View rlBottom;

    private PhotoAdapter photoAdapter;

    private List<PhotoAlbumFolder> listFolders = new ArrayList<>();//相册列表
    private List<PhotoAlbumPicture> listChecked = new ArrayList<>();//已选中的照片列表

    private boolean showCamera = false;
    private int limitCount = 1;
    private int currenFolderIndex = 0;//当前照片文件夹的index
    private PopupWindow folderPop;
    private GridLayoutManager layoutManager;
    private PopupWindow previewPop;
    private List<PhotoAlbumPicture> listCheckedTemp;
    private String mCameraFilePath;
    private static final int ACTIVITY_REQUEST_CAMERA = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);
        context = this;
        getIntentData();
        initTitlebar();
        setStatusBarColor(ThemeData.getStatusBarColor());
        initView();
        initRecyclerView();
        loadPhotos(0);

    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
                window.setNavigationBarColor(ContextCompat.getColor(this, R.color.albumPrimaryBlack));
            }
        }
    }

    /**
     * 处理intent数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        limitCount = intent.getIntExtra(PhotoAlbum.KEY_ALBUM_MAX_LIMIT_COUNT, 1);
        showCamera = intent.getBooleanExtra(PhotoAlbum.KEY_ALBUM_SHOW_CAMERA, false);
    }

    private void initTitlebar() {
        titlebar = (CustomTitlebar) findViewById(R.id.title_bar);
        titlebar.setBackgroundColor(ThemeData.getTitleBarColor());
        titlebar.setCenterText("所有照片")
                .setCenterColor(ThemeData.getTitleTextColor())
                .setLeftText("相册")
                .setLeftColor(ThemeData.getTitleTextColor())
                .setLeftImage(R.drawable.icon_arrow2left_black, ThemeData.getTitleTextColor())
                .setRightText("取消")
                .setRightColor(ThemeData.getTitleTextColor())
                .setLeftClickLisenter(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //切换到相册目录下
                        showAlbumFolder();
                    }
                })
                .setRightClickLisenter(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toResult(true);
                    }
                });
    }

    private void initView() {
        rlBottom = findViewById(R.id.rl_bottom);
        //单选隐藏底部栏
        if (limitCount == 1 && !ThemeData.isSingleChoiceShowBox()) {
            rlBottom.setVisibility(View.GONE);
        }
        tvPreview = (TextView) findViewById(R.id.tv_preview);
        tvCheckedCount = (TextView) findViewById(R.id.tv_checked_count);
        tvCheckedFinish = (TextView) findViewById(R.id.tv_checked_finish);
        tvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选中图片的预览
                if (listChecked.size() <= 0) {
                    return;
                }
                listCheckedTemp = new ArrayList<>(Arrays.asList(new PhotoAlbumPicture[listChecked.size()]));
                Collections.copy(listCheckedTemp, listChecked);
                showPreviewPop(0, listCheckedTemp, true, PhotoAlbumActivity.this);
            }
        });
        tvCheckedCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toResult(false);
            }
        });
        tvCheckedFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toResult(false);
            }
        });
        setBtnEnabled();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setBackgroundColor(ThemeData.getBackgroundColor());
        //如果确定每个item的内容不会改变RecyclerView的大小，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(context, ThemeData.getSpanCount());
        recyclerView.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter(context, showCamera, limitCount);
        photoAdapter.setCameraClickLisenter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
        photoAdapter.setCheckChangedLisenter(this);
        photoAdapter.setItemClickListener(new OnAdapterViewItemClickLisenter() {
            @Override
            public void itemClick(View view, int position) {
                //查看大图 这个position是在数据列表中的position
                showPreviewPop(position, listFolders.get(currenFolderIndex).getPhotos(), false, PhotoAlbumActivity.this);

            }
        });
        recyclerView.setAdapter(photoAdapter);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //当屏幕停止滚动，加载图片
                        try {
                            if (context != null)
                                Glide.with(context).resumeRequests();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                        try {
                            if (context != null)
                                Glide.with(context).pauseRequests();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                        try {
                            if (context != null)
                                Glide.with(context).pauseRequests();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

        });
    }


    /**
     * 启动相机拍照。
     */
    private void startCamera() {
        String outFileFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        String outFilePath = Util.getNowDateTime("yyyyMMdd_HHmmssSSS") + ".jpg";
        File file = new File(outFileFolder, outFilePath);
        mCameraFilePath = file.getAbsolutePath();
        Util.startCamera(this, ACTIVITY_REQUEST_CAMERA, file);
    }

    /**
     * 展示预览框
     *
     * @param position
     */
    private void showPreviewPop(int position, List<PhotoAlbumPicture> listPhotos, boolean isChose, OnCheckChangedLisenter lisenter) {
        previewPop = PopWindowHelp.initPreviewPop(isChose, context, limitCount, ThemeData.getTitleBarColor(), ThemeData.getTitleTextColor(), listPhotos, listChecked, position, lisenter, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toResult(false);
            }
        });
        previewPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                photoAdapter.notifyDataSetChanged();
                setBtnEnabled();
            }
        });

        if (!previewPop.isShowing()) {
            PopWindowHelp.showDropDown(previewPop, findViewById(R.id.view_top), 0, 0);
        }
    }

    /**
     * 展示相册目录的列表
     */
    private void showAlbumFolder() {
        if (folderPop == null) {
            folderPop = PopWindowHelp.initFolerPop(context, ThemeData.getTitleBarColor(), ThemeData.getTitleTextColor(), listFolders,
                    new OnAdapterViewItemClickLisenter() {
                        @Override
                        public void itemClick(View view, int position) {
                            if (currenFolderIndex != position) {
                                loadPhotos(position);
                            }
                        }
                    });
        }
        if (!folderPop.isShowing()) {
            folderPop.showAtLocation(findViewById(R.id.title_bar), Gravity.CENTER, 0, 0);
        }

    }

    @Override
    public boolean add(int position) {


        List<PhotoAlbumPicture> photos = listFolders.get(currenFolderIndex).getPhotos();
        PhotoAlbumPicture picture = photos.get(position);

        if (limitCount == 1) {
            listChecked.clear();
            for (int i = 0; i < photos.size(); i++) {
                PhotoAlbumPicture p = photos.get(i);
                if (p.isChecked()) {
//                    View v = layoutManager.getChildAt(showCamera ? i + 1 : i);
                    View v = layoutManager.findViewByPosition(showCamera ? i + 1 : i);
                    if (v != null) {
                        Log.d("PhotoAlbum", "add()---getChildAt view !=null");

                        CheckBox cb = (CheckBox) v.findViewById(R.id.cb_photo_check);
                        cb.setChecked(false);
                    } else {
                        Log.d("PhotoAlbum", "add()---getChildAt view =null");
                    }

                    p.setChecked(false);
                }
            }
        }
        if (listChecked.size() >= limitCount) {
            toast(String.format(Locale.getDefault(), "你最多可以选择%1$d张图片", limitCount));
            return false;
        }
        picture.setChecked(true);
        listChecked.add(picture);

        setBtnEnabled();
        return true;
    }

    @Override
    public void remove(int position) {
        List<PhotoAlbumPicture> photos = listFolders.get(currenFolderIndex).getPhotos();
        PhotoAlbumPicture picture = photos.get(position);
        picture.setChecked(false);
//        View v = layoutManager.getChildAt(showCamera ? position + 1 : position);
        View v = layoutManager.findViewByPosition(showCamera ? position + 1 : position);
        if (v != null) {
            Log.d("PhotoAlbum", "remove()---getChildAt view !=null");
            CheckBox cb = (CheckBox) v.findViewById(R.id.cb_photo_check);
            cb.setChecked(false);
        } else {
            Log.d("PhotoAlbum", "remove()---getChildAt view =null");
        }
        listChecked.remove(picture);
        setBtnEnabled();
    }

    /**
     * 设置底部按钮的显示与点击状态
     */

    private void setBtnEnabled() {
        boolean enabled = listChecked.size() > 0 ? true : false;
        tvPreview.setClickable(enabled);
        tvCheckedFinish.setClickable(enabled);
        if (enabled) {
            tvCheckedCount.setVisibility(View.VISIBLE);
            tvCheckedCount.setText(listChecked.size() + "");
            tvPreview.setTextColor(getResources().getColor(R.color.tv_preview_enabled));
            tvCheckedFinish.setTextColor(getResources().getColor(R.color.tv_finish_enabled));
        } else {
            tvCheckedCount.setVisibility(View.INVISIBLE);
            tvPreview.setTextColor(getResources().getColor(R.color.btn_disabled));
            tvCheckedFinish.setTextColor(getResources().getColor(R.color.btn_disabled));
        }
    }


    /**
     * 加载照片数据
     *
     * @param index
     */
    private void loadPhotos(final int index) {
        if (listFolders == null || listFolders.size() <= 0) {
            listFolders.addAll(PhotoAlbumScaner.getInstance().getPhotoAlbum(context, getIntent().getStringArrayListExtra(PhotoAlbum.KEY_ALBUM_REMOVE_PATHS)));
        }
        currenFolderIndex = index;
        PhotoAlbumFolder folder = listFolders.get(index);
        titlebar.setCenterText(folder.getName());
        photoAdapter.setList(folder.getPhotos());
        layoutManager.scrollToPosition(0);

    }

    private void toast(@NonNull String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_REQUEST_CAMERA: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent();
                    ArrayList<String> pathList = new ArrayList<>();
                    pathList.add(mCameraFilePath);
                    intent.putStringArrayListExtra(PhotoAlbum.KEY_OUTPUT_IMAGE_PATH_LIST, pathList);
                    setResult(RESULT_OK, intent);
                    super.finish();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * 处理取消和完成操作
     *
     * @param cancel
     */
    public void toResult(boolean cancel) {
        if (cancel) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        Intent intent = new Intent();
        ArrayList<String> pathList = new ArrayList<>();
        for (PhotoAlbumPicture albumImage : listChecked) {
            pathList.add(albumImage.getPath());
        }
        intent.putStringArrayListExtra(PhotoAlbum.KEY_OUTPUT_IMAGE_PATH_LIST, pathList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        previewPop = null;
        folderPop = null;
        super.onDestroy();
    }

}
