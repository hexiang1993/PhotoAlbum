# PhotoAlbum
系统相册

//配置相册的主题
ThemeData.init(new ThemeData.ThemeBuilder()
                .spanCount(3)
                .titleBarColor(Color.parseColor("#009def"))
                .titleTextColor(Color.WHITE)
                .backgroundColor(Color.WHITE)
                .checkBoxDrawable(R.drawable.checkbox_style)
                .statusBarColor(getResources().getColor(R.color.main_color))
                .build());

//启动相册
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
