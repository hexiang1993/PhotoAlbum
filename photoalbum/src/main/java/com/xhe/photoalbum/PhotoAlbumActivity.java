package com.xhe.photoalbum;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.xhe.photoalbum.data.PhotoAlbumFolder;
import com.xhe.photoalbum.data.PhotoAlbumPicture;
import com.xhe.photoalbum.data.PhotoAlbumScaner;
import com.xhe.photoalbum.interfaces.OnAdapterViewItemClickLisenter;
import com.xhe.photoalbum.interfaces.OnCheckChangedLisenter;
import com.xhe.photoalbum.utils.Util;
import com.xhe.photoalbum.widget.CustomTitlebar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xhe on 2017/6/17.
 * 相册展示activity
 */
public class PhotoAlbumActivity extends AppCompatActivity {
    private Context context;
    private RecyclerView recyclerView;
    private TextView tvPreview;
    private TextView tvCheckedCount;
    private TextView tvCheckedFinish;
    private CustomTitlebar titlebar;
    private PhotoAdapter photoAdapter;

    private List<PhotoAlbumFolder> listFolders = new ArrayList<>();//相册列表
    private List<PhotoAlbumPicture> listChecked = new ArrayList<>();//已选中的照片列表

    private int spanCount = 3;//TODO 配置 列数
    private boolean showCamera = true;//TODO 配置 是否展示相机按钮
    private int limitCount = 10;//TODO 配置 允许选择的最大数量
    private int titleBarColor = Color.WHITE;//TODO 配置 标题栏颜色
    private int titleTextColor = Color.BLACK;//TODO 配置 标题栏文字颜色
    private int normalColor;//选择框的默认颜色
    private int checkedColor;//选择框的选中颜色【跟标题栏一致】
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
        initView();
        initRecyclerView();

        //先检查权限
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            loadPhotos(0);
                            return;
                        }
                        toast("您拒绝了访问文件的权限，请到设置中开启");
                    }
                });
    }

    /**
     * 处理intent数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        spanCount = intent.getIntExtra(PhotoAlbum.KEY_ALBUM_SPAN_COUNT, 3);
        limitCount = intent.getIntExtra(PhotoAlbum.KEY_ALBUM_MAX_LIMIT_COUNT, 1);
        showCamera = intent.getBooleanExtra(PhotoAlbum.KEY_ALBUM_SHOW_CAMERA, false);
        titleBarColor = intent.getIntExtra(PhotoAlbum.KEY_ALBUM_TITLEBAR_COLOR, Color.WHITE);
        titleTextColor = intent.getIntExtra(PhotoAlbum.KEY_ALBUM_TITLE_TEXT_COLOR, Color.parseColor("#333333"));
        normalColor = Color.parseColor("#e0e0e0");
        checkedColor = titleBarColor;

    }

    private void initTitlebar() {
        titlebar = (CustomTitlebar) findViewById(R.id.title_bar);
        titlebar.setBackgroundColor(titleBarColor);
        titlebar.setCenterText("所有照片")
                .setCenterColor(titleTextColor)
                .setLeftText("相册")
                .setLeftColor(titleTextColor)
                .setLeftImage(R.drawable.icon_arrow2left_black, titleTextColor)
                .setRightText("取消")
                .setRightColor(titleTextColor)
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
                listCheckedTemp = new ArrayList<>(listChecked);
                Collections.copy(listCheckedTemp, listChecked);
                showPreviewPop(0, listCheckedTemp, new OnCheckChangedLisenter() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                        PhotoAlbumPicture albumImage = listCheckedTemp.get(position);
                        albumImage.setChecked(isChecked);
                        int i = listFolders.get(currenFolderIndex).getPhotos().indexOf(albumImage);
                        checkChanged(buttonView, isChecked, i);
                        photoAdapter.notifyItemDataChanged(i);
                    }
                });
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
        //如果确定每个item的内容不会改变RecyclerView的大小，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(context, spanCount);
        recyclerView.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter(context, normalColor, checkedColor, showCamera, spanCount);
        photoAdapter.setCameraClickLisenter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RxPermissions(PhotoAlbumActivity.this)
                        .request(Manifest.permission.CAMERA)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) {
                                    startCamera();
                                    return;
                                }
                                toast("您拒绝了使用相机的权限，如需要，请到设置中开启");
                            }
                        });
            }
        });
        photoAdapter.setCheckChangedLisenter(new OnCheckChangedLisenter() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                checkChanged(buttonView, isChecked, position);
            }
        });
        photoAdapter.setItemClickListener(new OnAdapterViewItemClickLisenter() {
            @Override
            public void itemClick(View view, int position) {
                //查看大图 这个position是在数据列表中的position
                showPreviewPop(position, listFolders.get(currenFolderIndex).getPhotos(), new OnCheckChangedLisenter() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                        checkChanged(buttonView, isChecked, position);
                        photoAdapter.notifyItemDataChanged(position);
                    }
                });

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
                            if (context != null) Glide.with(context).resumeRequests();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                        try {
                            if (context != null) Glide.with(context).pauseRequests();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                        try {
                            if (context != null) Glide.with(context).pauseRequests();
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
    private void showPreviewPop(int position, List<PhotoAlbumPicture> listPhotos, OnCheckChangedLisenter lisenter) {
        if (previewPop != null) {
            previewPop = null;
        }
        previewPop = PopWindowHelp.initPreviewPop(context, titleBarColor, titleTextColor, listPhotos, listChecked, position, lisenter, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toResult(false);
            }
        });
        previewPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                previewPop = null;
            }
        });

        if (!previewPop.isShowing()) {
            PopWindowHelp.showDropDown(previewPop,findViewById(R.id.view_top),0,0);
        }
    }

    /**
     * 展示相册目录的列表
     */
    private void showAlbumFolder() {
        if (folderPop == null) {
            folderPop = PopWindowHelp.initFolerPop(context, titleBarColor, titleTextColor, listFolders,
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

    /**
     * 照片的选择状态改变时要做的处理
     *
     * @param buttonView
     * @param isChecked
     * @param position
     */
    private void checkChanged(CompoundButton buttonView, boolean isChecked, int position) {
        PhotoAlbumPicture picture = listFolders.get(currenFolderIndex).getPhotos().get(position);
        picture.setChecked(isChecked);
        Log.i("Photo", position + "---ischecked=" + isChecked + "\n" + listFolders.get(currenFolderIndex).toString());
        if (!isChecked) {
            listChecked.remove(picture);
            setBtnEnabled();
            return;
        }

        //先判断该图片是否已被选中
        if (listChecked.contains(picture)) {
            setBtnEnabled();
            return;
        }

        //该图片未被选中
        //当前checked是否达到最大数量
        if (listChecked.size() >= limitCount) {
            toast(String.format(Locale.getDefault(), "你最多可以选择%1$d张图片", limitCount));
            buttonView.setChecked(false);
            picture.setChecked(false);
            setBtnEnabled();
            return;
        }
        listChecked.add(picture);
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
        Observable
                .create(new Observable.OnSubscribe<List<PhotoAlbumFolder>>() {
                    @Override
                    public void call(Subscriber<? super List<PhotoAlbumFolder>> subscriber) {
                        if (listFolders == null || listFolders.size() <= 0) {
                            listFolders.addAll(PhotoAlbumScaner.getInstance().getPhotoAlbum(context));
                        }
                        subscriber.onNext(listFolders);
                        subscriber.onCompleted();
                    }
                })
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<PhotoAlbumFolder>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<PhotoAlbumFolder> photoFolders) {
                        currenFolderIndex = index;
                        PhotoAlbumFolder folder = photoFolders.get(index);
                        titlebar.setCenterText(folder.getName());
                        photoAdapter.setList(folder.getPhotos());
                        layoutManager.scrollToPosition(0);
                        Log.d("Photo", folder.getPhotos().toString());
                    }
                });
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
}
