package com.young.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.young.adapter.myGridViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class MainActivity extends Activity {

    private GridView myGridview;
    private ImageView im_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_share_main);

        initWidget();

    }

    private void initWidget() {

        myGridViewAdapter gridViewAdapter = new myGridViewAdapter(this);

        myGridview = (GridView) findViewById(R.id.gv_shareimg);
        im_user = (ImageView) findViewById(R.id.im_userH);

//        im_user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Crop.pickImage(MainActivity.this);
//            }
//        });

        myGridview.setAdapter(gridViewAdapter);

    }


    private void beginCrop(Uri source) {

        File filepath;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //外置内存卡存在
            File share = new File(Environment.getExternalStorageDirectory(), "share/user/icon");
            share.mkdirs();

            filepath = new File(Environment.getExternalStorageDirectory(), "share/user/icon/user");

        } else {
            //外置内存卡不存在

            File share = new File(this.getCacheDir(), "/user/icon");
            share.mkdirs();

            filepath = new File(this.getCacheDir(), "user/icon/user");

        }


//        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Uri destination = Uri.fromFile(filepath);

        Crop.of(source, destination).asSquare().start(this);

    }




}
