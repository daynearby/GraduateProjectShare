package com.young.share;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.young.adapter.MainPagerAdapter;
import com.young.base.BaseCustomActBarActivity;
import com.young.bmobPush.MyPushMessageReceiver;
import com.young.config.Contants;
import com.young.model.MyBmobInstallation;
import com.young.utils.BDLBSUtils;
import com.young.utils.LogUtils;
import com.young.utils.SharePreferenceUtils;
import com.young.utils.XmlUtils;
import com.young.views.ArcMenu;
import com.young.views.Dialog4Tips;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.push.PushConstants;
import cn.bmob.v3.Bmob;


public class MainActivity extends BaseCustomActBarActivity {

    private ViewPager viewPager;
    private List<View> list;
    private ArcMenu mArcMenu;
    private SharePreferenceUtils sharePreferenceUtils;
    private BDLBSUtils bdlbsUtils;

    private String province = "广东省";
    private String city = "惠州市";
    private String district;
    private String street;
    private String streetNumber;

    private int times = 0;
    private static final int callbackTimes = 10;//回调10次

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void findviewbyid() {

        list = new ArrayList<>();
        viewPager = $(R.id.vp_main);
        mArcMenu = $(R.id.id_menu);

        LayoutInflater inflater = LayoutInflater.from(this);

        list.add(inflater.inflate(R.layout.pager_discount, null));
        list.add(inflater.inflate(R.layout.pager_discover, null));
        list.add(inflater.inflate(R.layout.pager_rank, null));

        mArcMenu.setOnMenuItemClickListener(new onitmeListener());

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this, list);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new pageChangeListener());
        viewPager.setCurrentItem(1);

    }

    @Override
    public void initData() {

        //初始化Bmob的消息推送
        configBmob();

        bdlbsUtils = new BDLBSUtils(this, new locationListener());
        bdlbsUtils.startLocation();

        sharePreferenceUtils = new SharePreferenceUtils(this);
    }

    /**
     * 初始化Bmob的消息推送
     */
    private void configBmob() {
        // 初始化BmobSDK
        Bmob.initialize(this, Contants.BMOB_APP_KEY);
        // 使用推送服务时的初始化操作
        MyBmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Contants.BMOB_APP_KEY);
        //注册信息接收者
        registerBoradcastReceiver();
    }

    @Override
    public void bindData() {

        settitle(R.string.discover);
        setBarVisibility(true, false);
        setCity(XmlUtils.getSelectCities(this));
        if (mUser == null) {
            loginFunction();
        }


    }

    @Override
    public void handerMessage(Message msg) {

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

    //注册广播接收者
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(MyPushMessageReceiver.BMOB_PUSH_MESSAGES);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
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


    /**
     * 登录过，才能进行登录
     * 没有登录过，则不进行其他操作
     */
    private void loginFunction() {

        final Dialog4Tips dialog4Tips = new Dialog4Tips(MainActivity.this);

        dialog4Tips.setBtnCancelText(getString(R.string.skin));
        dialog4Tips.setBtnOkText(getString(R.string.login_text));
        dialog4Tips.setBtnCancelVisi(View.VISIBLE);

        dialog4Tips.setDialogListener(new Dialog4Tips.Listener() {
            @Override
            public void btnOkListenter() {//登陆  按钮 ，进入登陆界面

                intents.setClass(MainActivity.this, LoginActivity.class);
                dialog4Tips.dismiss();

                startActivity(intents);
            }

            @Override
            public void btnCancelListener() {//跳过  按钮 进入主界面


                dialog4Tips.dismiss();

            }
        });

        dialog4Tips.show();


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
                                    bdlbsUtils.stopLocation();
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

    /**
     * 定位回调
     * 回调 10次 还是没有得到位置，那么就停止
     */
    private class locationListener implements BDLBSUtils.LocationInfoListener {
        private List<String> cityList = new ArrayList<>();

        @Override
        public void LocationInfo(String Province, String City, String District, String Street, String StreetNumber) {

            province = Province;
            city = City;
            district = District;
            street = Street;
            streetNumber = StreetNumber;

            times++;

            if (Province != null) {
                cityList = XmlUtils.getSelectCities(MainActivity.this);

                for (int i = 0; i < cityList.size(); i++) {
                    city.equals(cityList.get(i));
                    setDefaultCity(i);
                }

                bdlbsUtils.stopLocation();
            }

            if (times == callbackTimes) {
                bdlbsUtils.stopLocation();
            }
        }
    }

    /**
     * 消息的广播接收者
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        private ImageView imageView;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MyPushMessageReceiver.BMOB_PUSH_MESSAGES)) {

                LogUtils.logE("收到信息" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));

                imageView = (ImageView) mArcMenu.getChildAt(0);
                imageView.setImageResource(R.drawable.icon_more_light);

                imageView = (ImageView) mArcMenu.getChildAt(2);
                imageView.setImageResource(R.drawable.icon_comment_light);

            }
        }

    };


    /**
     * 自定义按钮的点击事件
     */
    private class onitmeListener implements ArcMenu.OnMenuItemClickListener {
        private ImageView itemIm;

        @Override
        public void onClick(View view, int pos) {
//            LogUtils.logI("view = "+view+" position = "+pos);
            itemIm = (ImageView) view;
            switch (pos) {
                case 1://分享信息
// TODO: 2015-10-22 弹窗，填写，并且可以保存草稿 使用popwindow
                    break;
                case 2://评论
// TODO: 2015-10-22 查看评论列表
                    itemIm.setImageResource(R.drawable.icon_comment);

                    //跳转到评论页面
                    break;
                case 3://个人中心

// TODO: 2015-10-22 判断是否已经登录，已经等了进入个人中心，未登录进入登录界面
                    if (mUser != null) {

                        intents.setClass(MainActivity.this, PersonalCenterActivity.class);
                        startActivity(intents);

                    } else {
                        loginFunction();
                    }
                    break;
            }
        }

        @Override
        public void onViewGroundClickListener(View view) {
            itemIm = (ImageView) view;
            itemIm.setImageResource(R.drawable.icon_more);
        }
    }


}
