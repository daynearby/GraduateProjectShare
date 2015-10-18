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
import com.young.annotation.InjectView;
import com.young.base.BaseActivity;
import com.young.config.Contants;
import com.young.views.ArcMenu;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;


public class MainActivity extends BaseActivity {

    //    private GridView myGridview;
//    private ImageView im_user;
//    @InjectView(R.id.id_tv)
//    private TextView tx;
    private ViewPager viewPager;
    private List<View> list;
    private ArcMenu mArcMenu;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void findviewbyid() {

        list = new ArrayList<>();
        viewPager = $(R.id.vp_main);
        mArcMenu = (ArcMenu) findViewById(R.id.id_menu);

        LayoutInflater inflater = LayoutInflater.from(this);

        list.add(inflater.inflate(R.layout.pager_discount, null));
        list.add(inflater.inflate(R.layout.pager_discover, null));
        list.add(inflater.inflate(R.layout.pager_rank, null));

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this, list);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new pageChangeListener());
        viewPager.setCurrentItem(1);

    }

    @Override
    public void initData() {

        //初始化Bmob的消息推送
        configBmob();
    }

    /**
     * 初始化Bmob的消息推送
     *
     */
    private void configBmob() {
        // 初始化BmobSDK
        Bmob.initialize(this, Contants.BMOB_APP_KEY);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Contants.BMOB_APP_KEY);
    }

    @Override
    public void bindData() {
        settitle(R.string.discover);
        setBarVisibility(true, false);
        setCity(cityList());

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
                    settitle(R.string.discount);
                    break;

                case 1:
                    settitle(R.string.discover);
                    break;

                case 2:
                    settitle(R.string.rank);
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

//
//
////        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
//        Uri destination = Uri.fromFile(filepath);
//
//        Crop.of(source, destination).asSquare().start(this);
//
//    }
// TODO: 15/10/10 将这些文字资源存放到xml的资源中 
    private List<String> tagList() {
        List<String> list = new ArrayList<>();
        list.add("旅游圣地");
        list.add("约会圣地");
        list.add("儿童乐园");
        list.add("摄影");

        return list;
    }

    private List<String> cityList(){
        List<String> list = new ArrayList<>();
        list.add("惠州");
        list.add("深圳");
        list.add("广州");
        list.add("东莞");
        return list;
    }


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
