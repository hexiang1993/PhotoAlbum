package com.xhe.photoalbum;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.xhe.photoalbum.data.PhotoAlbumPicture;
import com.xhe.photoalbum.interfaces.OnAdapterViewItemClickLisenter;
import com.xhe.photoalbum.interfaces.OnCheckChangedLisenter;
import com.xhe.photoalbum.utils.DisplayUtils;
import com.xhe.photoalbum.utils.ImageLoader;
import com.xhe.photoalbum.utils.Util;
import com.xhe.photoalbum.utils.SelectorUtils;

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
    private final ColorStateList cbColorStateList;

    private View.OnClickListener cameraClickLisenter;
    private OnCheckChangedLisenter checkChangedLisenter;
    private OnAdapterViewItemClickLisenter itemClickLisenter;
    private boolean showCamera;
    private int limitCount;//限制的最大选择数量
    private Context context;
    private List<PhotoAlbumPicture> listPhotos = new ArrayList<>();
    private int imgSize;//照片尺寸，宽度高度一致

    public PhotoAdapter(Context context, int normalColor, int checkedColor, boolean showCamera, int spanCount,int limitCount) {
        this.inflater = LayoutInflater.from(context);
        this.showCamera = showCamera;
        this.context = context;
        this.limitCount = limitCount;
        this.imgSize = (int) ((DisplayUtils.getScreenWidth(context) - (spanCount - 1) * 0.5) / spanCount);
        this.cbColorStateList = SelectorUtils.createColorStateList(normalColor, checkedColor);
    }

    public void setList(@NonNull List<PhotoAlbumPicture> list) {
        listPhotos = list;
        notifyDataSetChanged();
    }

    public void notifyItemDataChanged(int index){
        if (index<0){
            return;
        }
        notifyItemChanged(showCamera?index+1:index);
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
        holder.itemView.requestLayout();
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
        if (limitCount==1){
            holder.cbChecked.setVisibility(View.INVISIBLE);
        }
        //选择框的颜色
        holder.cbChecked.setSupportButtonTintList(cbColorStateList);
        ImageLoader.getInstance(context).load(Util.LOCAL_FILE_URI_PREFIX + photo.getPath(), holder.ivPhoto, imgSize, imgSize);
        holder.cbChecked.setChecked(photo.isChecked());
        Log.w("Photo", "onBindViewHolder-------" + position+"是否选中"+photo.isChecked());

        holder.cbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkChangedLisenter != null) {
                    //这里的position要重新获取，否则会错乱
                    checkChangedLisenter.onCheckedChanged(buttonView, isChecked, showCamera?holder.getAdapterPosition()-1:holder.getAdapterPosition());
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
        AppCompatCheckBox cbChecked;
        int viewType;
        View itemView;

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
                    cbChecked = (AppCompatCheckBox) itemView.findViewById(R.id.cb_photo_check);
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
