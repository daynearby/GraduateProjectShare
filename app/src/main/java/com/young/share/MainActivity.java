package com.young.share;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.young.adapter.MainPagerAdapter;
import com.young.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    //    private GridView myGridview;
//    private ImageView im_user;
    private TextView tx;
    private ViewPager viewPager;
    private List<View> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void findviewbyid() {

        list = new ArrayList<>();
        viewPager = $(R.id.vp_main);

        LayoutInflater inflater = LayoutInflater.from(this);

        list.add(inflater.inflate(R.layout.pager_discount, null));
        list.add(inflater.inflate(R.layout.pager_discover, null));
        list.add(inflater.inflate(R.layout.pager_rank, null));

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this, list);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new pageChangeListener());

    }

    @Override
    public void initData() {

    }

    @Override
    public void bindData() {


    }

    private class pageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {

            switch (arg0) {
// TODO: 2015-10-09 页面切换更换title 
                case 0:

                    break;

                case 1:

                    break;

                case 2:

                    break;

            }

        }

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


    // 退出程序
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {


            AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(this);

            alertbBuilder
                    .setTitle("真的要退出？")
                    .setMessage("你确定要离开吗?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    int nPid = android.os.Process.myPid();
                                    android.os.Process.killProcess(nPid);

                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    dialog.cancel();

                                }
                            }).create();

            alertbBuilder.show();

            return true;

        } else {

            return super.dispatchKeyEvent(event);

        }
    }

}
