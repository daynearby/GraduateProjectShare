package com.young.share;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.young.adapter.MainPagerAdapter;
import com.young.base.CustomActBarActivity;
import com.young.config.Contants;
import com.young.model.MyBmobInstallation;
import com.young.model.User;
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
import cn.bmob.v3.BmobUser;


public class MainActivity extends CustomActBarActivity {

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
    private boolean isToggle = false;
    private boolean isRegistBordcast = false;//是否注册了广播接收者

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
        super.initData();
        //初始化Bmob的消息推送
        configBmob();

        bdlbsUtils = BDLBSUtils.builder(this, new locationListener());
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
        setCity(XmlUtils.getSelectCities(this).get(8));
        if (mUser == null) {
            loginFunction();
        }

        //选择城市的回调
        setItemResult(new itemClickResult() {
            @Override
            public void result(View view, String s, int position) {

                LogUtils.logI("选择城市 = " + s + " position = " + position);
            }
        });

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

    //注册广播接收者。Bmob推送消息 更新UI
    public void registerBoradcastReceiver() {
//        myIntentFilter= new IntentFilter();
        myIntentFilter.addAction(Contants.BMOB_PUSH_MESSAGES);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
        isRegistBordcast = true;

    }

    //注册广播接收者。Bmob推送消息 更新UI
    public void registerBoradcastReceiverRequestLocation() {
//        myIntentFilter= new IntentFilter();
        myIntentFilter.addAction(Contants.BORDCAST_REQUEST_LOCATIONINFO);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);

        isRegistBordcast = true;

    }


    /**
     * 登录过，才能进行登录
     * 没有登录过，则不进行其他操作
     */
    private void loginFunction() {

        final Dialog4Tips dialog4Tips = new Dialog4Tips(mActivity);

        dialog4Tips.setBtnCancelText(getString(R.string.skin));
        dialog4Tips.setBtnOkText(getString(R.string.login_text));
        dialog4Tips.setBtnCancelVisi(View.VISIBLE);

        dialog4Tips.setDialogListener(new Dialog4Tips.Listener() {
            @Override
            public void btnOkListenter() {//登陆  按钮 ，进入登陆界面

                intents.setClass(mActivity, LoginActivity.class);
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
                                    int nPid = android.os.Process.myPid();
                                    android.os.Process.killProcess(nPid);
                                    System.exit(0);
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
     * 开始定位
     */
    public void startLocation() {

        bdlbsUtils.startLocation();
    }

    /**
     * 定位回调
     * 回调 10次 还是没有得到位置，那么就停止
     */
    private class locationListener implements BDLBSUtils.LocationInfoListener {

        @Override
        public void LocationInfo(String Province, String City, String District, String Street, String StreetNumber) {

            province = Province;
            city = City;
            district = District;
            street = Street;
            streetNumber = StreetNumber;

            times++;

            if (Province != null) {
//                cityList = XmlUtils.getSelectCities(mActivity);

//                for (int i = 0; i < cityList.size(); i++) {

                getCity_tv().setText(city);

//                }
                Bundle bundle = new Bundle();
                bundle.putString(Contants.PROVINCE, province);
                bundle.putString(Contants.CITY, city);
                bundle.putString(Contants.DISTRICT, district);
                bundle.putString(Contants.STREET, street);
                bundle.putString(Contants.STREETNUMBER, streetNumber);

                intents.setAction(Contants.BORDCAST_LOCATIONINFO);
                intents.putExtra(BUNDLE_BROADCAST, bundle);
                sendBroadcast(intents);

                LogUtils.logI("定位成功 定位信息 发送广播 ");

                bdlbsUtils.stopLocation();
            }

            if (times >= callbackTimes) {
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
//"Bmob  信息
            if (intent.getAction().equals(Contants.BMOB_PUSH_MESSAGES)) {

                LogUtils.logE("Bmob 收到信息" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));

                imageView = (ImageView) mArcMenu.getChildAt(0);
                imageView.setImageResource(R.drawable.icon_more_light);

                imageView = (ImageView) mArcMenu.getChildAt(2);
                imageView.setImageResource(R.drawable.icon_comment_light);

            } else if (intent.getAction().equals(Contants.BORDCAST_REQUEST_LOCATIONINFO)) {
                //开始 百度定位
                LogUtils.logI("main 收到广播 开始 定位");

                startLocation();

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
            mUser = BmobUser.getCurrentUser(mActivity,User.class);

            switch (pos) {
                case 1://分享信息
                    if (mUser != null) {

                        //定位信息请求，注册广播接收者
                        registerBoradcastReceiverRequestLocation();
                        mStartActivity(ShareMessageActivity.class);

                    } else {

                        loginFunction();
                    }

                    break;
                case 2://评论
// TODO: 2015-10-22 查看评论列表
                    itemIm.setImageResource(R.drawable.icon_comment);

                    if (mUser != null) {

                        //跳转到评论页面
//                        mStartActivity(ShareMessageActivity.class);

                    } else {

                        loginFunction();

                    }
                    break;
                case 3://个人中心

// TODO: 2015-10-22 判断是否已经登录，已经等了进入个人中心，未登录进入登录界面
                    if (mUser != null) {
                        mStartActivity(PersonalCenterActivity.class);

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.logI("touch");
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bdlbsUtils.stopLocation();
        if (isRegistBordcast) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }
}
