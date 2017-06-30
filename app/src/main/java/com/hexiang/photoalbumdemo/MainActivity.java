package com.hexiang.photoalbumdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xhe.photoalbum.PhotoAlbum;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_start_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoAlbum(MainActivity.this,100)
                        .startAlbum();
            }
        });
    }
}
