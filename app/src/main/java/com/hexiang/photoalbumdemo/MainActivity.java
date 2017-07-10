package com.hexiang.photoalbumdemo;

import android.content.Intent;
import android.graphics.Color;
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
import com.xhe.photoalbum.data.ThemeData;
import com.xhe.photoalbum.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private SBAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeData.init(new ThemeData.ThemeBuilder()
                .spanCount(3)
                .titleBarColor(Color.parseColor("#009def"))
                .titleTextColor(Color.WHITE)
                .backgroundColor(Color.WHITE)
                .checkBoxDrawable(R.drawable.checkbox_style)
                .statusBarColor(getResources().getColor(R.color.main_color))
                .build());

        findViewById(R.id.tv_start_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoAlbum(MainActivity.this)
                        .setRemovePaths(list)
                        .setLimitCount(5)
                        .startAlbum()
                        .subscribe(new Action1<Intent>() {
                            @Override
                            public void call(Intent intent) {
                                if (intent == null) {
                                    return;
                                }
                                list.clear();
                                list.addAll(PhotoAlbum.parseResult(intent));
                                adapter.notifyDataChanged();
                                Log.d("PhotoAlbum",list.toString());
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
