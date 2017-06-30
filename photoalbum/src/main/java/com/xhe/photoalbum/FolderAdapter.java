package com.xhe.photoalbum;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhe.photoalbum.data.PhotoAlbumFolder;
import com.xhe.photoalbum.data.PhotoAlbumPicture;
import com.xhe.photoalbum.interfaces.OnAdapterViewItemClickLisenter;
import com.xhe.photoalbum.utils.ImageLoader;
import com.xhe.photoalbum.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhe on 2017/6/19.
 * 相册目录adapter
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private List<PhotoAlbumFolder> listFolder;

    public FolderAdapter(List<PhotoAlbumFolder> listFolder) {
        this.listFolder = listFolder;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PhotoAlbumFolder folder = listFolder.get(position);
        holder.tvName.setText(folder.getName());
        ArrayList<PhotoAlbumPicture> photos = folder.getPhotos();
        if (photos != null && photos.size() > 0) {
            holder.tvCount.setText("("+photos.size()+")");
            ImageLoader.getInstance(holder.ivHead.getContext()).load(Util.LOCAL_FILE_URI_PREFIX + photos.get(0).getPath(), holder.ivHead);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickLisenter != null) {
                    itemClickLisenter.itemClick(v,holder.getAdapterPosition());
                }
            }
        });
    }

    OnAdapterViewItemClickLisenter itemClickLisenter;

    public void setItemClickLisenter(OnAdapterViewItemClickLisenter itemClickLisenter) {
        this.itemClickLisenter = itemClickLisenter;
    }

    @Override
    public int getItemCount() {
        return listFolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHead;
        TextView tvName;
        TextView tvCount;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivHead = (ImageView) itemView.findViewById(R.id.iv_folder_first_photo);
            tvName = (TextView) itemView.findViewById(R.id.tv_folder_name);
            tvCount = (TextView) itemView.findViewById(R.id.tv_folder_photo_count);
        }
    }
}
