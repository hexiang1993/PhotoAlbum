package com.hexiang.photoalbumdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.gengqiquan.adapter.adapter.SBAdapter;
import com.gengqiquan.adapter.interfaces.Converter;
import com.gengqiquan.adapter.interfaces.Holder;
import com.xhe.photoalbum.PhotoAlbum;
import com.xhe.photoalbum.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private SBAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_start_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new PhotoAlbum(MainActivity.this, 100)
//                        .setLimitCount(5)
//                        .startAlbum();
                new PhotoAlbum(MainActivity.this)
                        .setLimitCount(5)
                        .startAlbum(new PhotoAlbum.ActivityForResultCallBack() {
                            @Override
                            public void result(Intent data) {
                                if (data == null) return;
                                list.clear();
                                list.addAll(PhotoAlbum.parseResult(data));
                                adapter.notifyDataChanged();
                            }
                        });

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
                        ImageLoader.getInstance(MainActivity.this)
                                .load(item, imageView);
                    }
                });
        gridView.setAdapter(adapter);
    }
}
