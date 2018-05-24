package com.xhe.photoalbum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xhe.photoalbum.data.PhotoAlbumPicture;
import com.xhe.photoalbum.data.ThemeData;
import com.xhe.photoalbum.interfaces.OnAdapterViewItemClickLisenter;
import com.xhe.photoalbum.interfaces.OnCheckChangedLisenter;
import com.xhe.photoalbum.utils.DisplayUtils;
import com.xhe.photoalbum.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhe on 2017/6/17.
 * 照片列表adapter
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private final LayoutInflater inflater;

    private View.OnClickListener cameraClickLisenter;
    private OnCheckChangedLisenter checkChangedLisenter;
    private OnAdapterViewItemClickLisenter itemClickLisenter;
    private boolean showCamera;
    private int limitCount;//限制的最大选择数量
    private Context context;
    private List<PhotoAlbumPicture> listPhotos = new ArrayList<>();
    private int imgSize;//照片尺寸，宽度高度一致

    public PhotoAdapter(Context context, boolean showCamera, int limitCount) {
        this.inflater = LayoutInflater.from(context);
        this.showCamera = showCamera;
        this.context = context;
        this.limitCount = limitCount;
        this.imgSize = (int) ((DisplayUtils.getScreenWidth(context) - (ThemeData.getSpanCount() - 1) * 0.5) / ThemeData.getSpanCount());
    }

    public void setList(@NonNull List<PhotoAlbumPicture> list) {
        listPhotos = list;
        notifyDataSetChanged();
    }

    public void notifyItemDataChanged(int index) {
        if (index < 0) {
            return;
        }
        notifyItemChanged(showCamera ? index + 1 : index);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CAMERA:
                return new ViewHolder(viewType, inflater.inflate(R.layout.layout_btn_camera, parent, false));
            case TYPE_PICTURE:
                return new ViewHolder(viewType, inflater.inflate(R.layout.layout_item_picture, parent, false));
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final PhotoAdapter.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        holder.itemView.getLayoutParams().width = imgSize;
        holder.itemView.getLayoutParams().height = imgSize;
//        holder.itemView.requestLayout();
        /**相机**/
        if (viewType == TYPE_CAMERA) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cameraClickLisenter != null) {
                        cameraClickLisenter.onClick(v);
                    }
                }
            });
            return;
        }

        /**相册**/
        final int photoIndex = showCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition();
        PhotoAlbumPicture photo = listPhotos.get(photoIndex);
        //单选的时候选择框隐藏
        if (limitCount == 1 && !ThemeData.isSingleChoiceShowBox()) {
            holder.cbChecked.setVisibility(View.INVISIBLE);
        }
        //选择框的样式
        holder.cbChecked.setButtonDrawable(ThemeData.getCheckBoxDrawable());

        String path = photo.getPath();
        Glide.with(holder.ivPhoto.getContext())
                .load(path).into(holder.ivPhoto);

        holder.cbChecked.setChecked(photo.isChecked());
        holder.checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cbChecked.isChecked()) {
                    checkChangedLisenter.remove(showCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition());
                    holder.cbChecked.setChecked(false);
                } else {
                    holder.cbChecked.setChecked(checkChangedLisenter.add(showCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition()));

                }

            }
        });

        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickLisenter != null) {
                    itemClickLisenter.itemClick(v, showCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (showCamera) {
            return 1 + listPhotos.size();
        }
        return listPhotos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return TYPE_CAMERA;
        }
        return TYPE_PICTURE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        CheckBox cbChecked;
        int viewType;
        View itemView;
        View checkView;

        public ViewHolder(int viewType, View itemView) {
            super(itemView);
            this.viewType = viewType;
            this.itemView = itemView;
            initView();
        }

        private void initView() {
            switch (viewType) {
                case TYPE_CAMERA:
                    break;
                case TYPE_PICTURE:
                    ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
                    cbChecked = (CheckBox) itemView.findViewById(R.id.cb_photo_check);
                    checkView = itemView.findViewById(R.id.v_check);
                    break;
            }
        }
    }

    public void setItemClickListener(OnAdapterViewItemClickLisenter listener) {
        itemClickLisenter = listener;
    }

    public void setCheckChangedLisenter(OnCheckChangedLisenter lisenter) {
        checkChangedLisenter = lisenter;
    }

    public void setCameraClickLisenter(View.OnClickListener lisenter) {
        cameraClickLisenter = lisenter;
    }

}
