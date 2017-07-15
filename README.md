# PhotoAlbum
系统相册

## config the theme
```
ThemeData.init(new ThemeData.ThemeBuilder()
         .spanCount(3)
         .titleBarColor(Color.parseColor("#009def"))
         .titleTextColor(Color.WHITE)
         .backgroundColor(Color.WHITE)
         .checkBoxDrawable(R.drawable.checkbox_style)
         .statusBarColor(getResources().getColor(R.color.main_color))
         .build());
```

## start album
```
new PhotoAlbum(MainActivity.this)
                        .addRemovePaths(list)
                        .setLimitCount(3)
                        .startAlbum()
                        .subscribe(new Action1<List<String>>() {
                            @Override
                            public void call(List<String> paths) {
                                list.clear();
                                list.addAll(paths);
                                adapter.notifyDataChanged();
                                Log.d("PhotoAlbum",list.toString());
                            }
                        });
```