package com.hexiang.photoalbumdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gengqiquan.adapter.adapter.SBAdapter;
import com.gengqiquan.adapter.interfaces.Converter;
import com.gengqiquan.adapter.interfaces.Holder;
import com.xhe.photoalbum.PhotoAlbum;
import com.xhe.photoalbum.data.ThemeData;
import com.xhe.photoalbum.utils.ImageDisplayer;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private SBAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeData.init(new ThemeData.ThemeBuilder()
                .spanCount(3)
                .titleBarColor(Color.WHITE)
                .titleTextColor(Color.BLACK)
                .backgroundColor(Color.WHITE)
                .checkBoxDrawable(R.drawable.checkbox_style)
                .statusBarColor(getResources().getColor(R.color.main_color))
                .build());
//        ImageDisplayer.setDisplayer(new ImageDisplayer.Displayer() {
//            @Override
//            public void display(@NonNull String url, @NonNull final ImageView imageView) {
//                Glide.with(imageView.getContext()).load(url).into(imageView);
//            }
//
//            @Override
//            public void pauseRequests() {
//
//            }
//
//            @Override
//            public void resumeRequests() {
//
//            }
//        });
        findViewById(R.id.tv_start_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new PhotoAlbum(MainActivity.this)
                        .addRemovePaths(list)
                        .setShowCamera(false)
                        .setLimitCount(1)
                        .setSingleChoiceShowBox(false)
                        .getAlbumIntent(), 1000);
            }
        });

        GridView gridView = (GridView) findViewById(R.id.gridview);
        adapter = new SBAdapter<String>(this)
                .layout(R.layout.item_img)
                .list(list)
                .bindViewData(new Converter<String>() {
                    @Override
                    public void convert(Holder holder, String item) {
                        ImageView imageView = holder.getView(R.id.imageview);
                        Glide.with(imageView.getContext())
                                .load(item).into(imageView);
                    }
                });
        gridView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            List<String> paths = PhotoAlbum.parseResult(data);
            list.clear();
            list.addAll(paths);
            adapter.notifyDataChanged();
            Log.d("PhotoAlbum", list.toString());
        }
    }
}
