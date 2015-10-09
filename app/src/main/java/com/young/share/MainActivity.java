package com.young.share;

import android.os.Bundle;
import android.widget.TextView;
import com.young.base.BaseActivity;


public class MainActivity extends BaseActivity {

//    private GridView myGridview;
//    private ImageView im_user;
    private TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void findviewbyid() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void bindData() {

    }


//    private void initWidget() {
//
//        myGridViewAdapter gridViewAdapter = new myGridViewAdapter(this);
//
//        myGridview = (GridView) findViewById(R.id.gv_shareimg);
//        im_user = (ImageView) findViewById(R.id.im_userH);
//
////        im_user.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Crop.pickImage(MainActivity.this);
////            }
////        });
//
//        myGridview.setAdapter(gridViewAdapter);
//
//    }
//
//
//    private void beginCrop(Uri source) {
//
//        File filepath;
//
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            //外置内存卡存在
//            File share = new File(Environment.getExternalStorageDirectory(), "share/user/icon");
//            share.mkdirs();
//
//            filepath = new File(Environment.getExternalStorageDirectory(), "share/user/icon/user");
//
//        } else {
//            //外置内存卡不存在
//
//            File share = new File(this.getCacheDir(), "/user/icon");
//            share.mkdirs();
//
//            filepath = new File(this.getCacheDir(), "user/icon/user");
//
//        }
//
//
////        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
//        Uri destination = Uri.fromFile(filepath);
//
//        Crop.of(source, destination).asSquare().start(this);
//
//    }




}
