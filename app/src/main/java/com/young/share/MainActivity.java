package com.young.share;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.young.adapter.MainPagerAdapter;
import com.young.base.CustomActBarActivity;
import com.young.config.Contants;
import com.young.model.MyBmobInstallation;
import com.young.model.User;
import com.young.share.fragment.DiscountFragment;
import com.young.share.fragment.DiscoverFragment;
import com.young.share.fragment.RankFragment;
import com.young.utils.BDLBSUtils;
import com.young.utils.LogUtils;
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

    private ArcMenu mArcMenu;
    private BDLBSUtils bdlbsUtils;
    private MainPagerAdapter pagerAdapter;

    private String province = "广东省";
    private String city = "惠州市";
    private String district;
    private String street;
    private String streetNumber;

    private int times = 0;
    private static final int callbackTimes = 10;//回调10次
    private boolean isToggle = false;
    private boolean isRegistBordcast = false;//是否注册了广播接收者
    private boolean isDiscount = false;//当前是否为商家优惠界面。true -->是

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void findviewbyid() {

        List<Fragment> list = new ArrayList<>();
        ViewPager viewPager = $(R.id.vp_main);
        mArcMenu = $(R.id.id_menu);

        LayoutInflater inflater = LayoutInflater.from(this);

        list.add(new DiscountFragment(this));
        list.add(new DiscoverFragment(this));
        list.add(new RankFragment(this));

        mArcMenu.setOnMenuItemClickListener(new onitmeListener());

        pagerAdapter = new MainPagerAdapter( list,
                getSupportFragmentManager(), viewPager,
                new pageChangeListener());

        viewPager.setCurrentItem(1);

    }

    @Override
    public void initData() {
        super.initData();
        //初始化Bmob的消息推送
        configBmob();

        bdlbsUtils = BDLBSUtils.builder(this, new locationListener());
        bdlbsUtils.startLocation();
        registerBoradcastReceiverRefreshUI();
//        SharePreferenceUtils sharePreferenceUtils = new SharePreferenceUtils(this);
    }

    /**
     * 初始化Bmob的消息推送
     */
    private void configBmob() {
        // 初始化BmobSDK
        Bmob.initialize(this, Contants.BMOB_APP_KEY);
        // 使用推送服务时的初始化操作
        savaUserWithInsId();

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

    @Override
    public void mBack() {

    }

    private class pageChangeListener implements MainPagerAdapter.OnPageSelected {


        @Override
        public void onselected(int position) {

            switch (position) {

// TODO: 2015-10-09 页面切换更换title 
                case 0:
                    settitle(R.string.discount);
                    mArcMenu.setVisibility(View.VISIBLE);
                    isDiscount = true;
                    break;

                case 1:
                    settitle(R.string.discover);
                    mArcMenu.setVisibility(View.VISIBLE);
                    isDiscount = false;
                    break;

                case 2:
                    settitle(R.string.rank);
                    mArcMenu.setVisibility(View.GONE);
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
        myIntentFilter.addAction(Contants.BORDCAST_REQUEST_LOCATIONINFO);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);

        isRegistBordcast = true;

    }

    //注册广播接收者。查看消息
    public void registerBoradcastReceiverClearMessages() {
        myIntentFilter.addAction(Contants.BORDCAST_CLEAR_MESSAGES);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);

        isRegistBordcast = true;

    }

    //注册广播接收者。在修改了数据后刷新列表

    public void registerBoradcastReceiverRefreshUI() {
        myIntentFilter.addAction(Contants.BORDCAST_REQUEST_REFRESH);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
        isRegistBordcast = true;

    }

    /**
     * 登录过，才能进行登录
     * 没有登录过，则不进行其他操作
     */
    private void loginFunction() {
        Dialog4Tips.loginFunction(mActivity);


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

//                LogUtils.logI("定位成功 定位信息 发送广播 ");

                bdlbsUtils.stopLocation();
            }

            if (times >= callbackTimes) {
                bdlbsUtils.stopLocation();
            }
        }
    }

    /**
     * 消息的广播接收者
     * 处理说到的广播信息
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {


            switch (intent.getAction()) {
                case Contants.BMOB_PUSH_MESSAGES://"Bmob  信息

                    LogUtils.logE("Bmob 收到信息" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
                    initMessagesIcon(true);


                    break;
                case Contants.BORDCAST_REQUEST_LOCATIONINFO://使用百度定位
                    //开始 百度定位
                    times = 0;
                    startLocation();
                    break;

                case Contants.BORDCAST_CLEAR_MESSAGES://清空消息
                    initMessagesIcon(false);
                    break;
                case Contants.BORDCAST_REQUEST_REFRESH:
                    pagerAdapter.refreshUI(intent.getIntExtra(Contants.REFRESH_TYPE, 0));
                    LogUtils.logD("get refresh broadcast code = " + intent.getIntExtra(Contants.REFRESH_TYPE, 0));
                    break;


            }

        }

    };

    /**
     * 有新消息，改变图标
     *
     * @param hadNewMessage true -- > 有新消息，出现小红点。反之
     */
    private void initMessagesIcon(boolean hadNewMessage) {
        ImageView imageView;

        if (hadNewMessage) {
            imageView = (ImageView) mArcMenu.getChildAt(0);
            imageView.setImageResource(R.drawable.icon_more_light);

            imageView = (ImageView) mArcMenu.getChildAt(2);
            imageView.setImageResource(R.drawable.icon_comment_light);
        } else {
            imageView = (ImageView) mArcMenu.getChildAt(0);
            imageView.setImageResource(R.drawable.icon_more);

            imageView = (ImageView) mArcMenu.getChildAt(2);
            imageView.setImageResource(R.drawable.icon_comment);
        }

    }

    /**
     * 自定义按钮的点击事件
     */
    private class onitmeListener implements ArcMenu.OnMenuItemClickListener {
        private ImageView itemIm;


        @Override
        public void onClick(View view, int pos) {
//            LogUtils.logI("view = "+view+" position = "+pos);
            itemIm = (ImageView) view;
            mUser = BmobUser.getCurrentUser(mActivity, User.class);

            switch (pos) {
                case 1://分享信息

                    if (mUser != null) {

                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Contants.BUNDLE_CURRENT_IS_DISCOUNT, isDiscount);
                        //定位信息请求，注册广播接收者
                        registerBoradcastReceiverRequestLocation();
                        mStartActivity(ShareMessageActivity.class, bundle);

                    } else {

                        loginFunction();
                    }

                    break;
                case 2://消息中心

                    if (mUser != null) {
                        //注册广播接收者
                        registerBoradcastReceiverClearMessages();
                        mStartActivity(MessageCenterActivity.class);

                    } else {
//用户还没登陆
                        loginFunction();

                    }
                    break;
                case 3://个人中心

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

    /**
     * 将installationId与user绑定
     */
    private void savaUserWithInsId() {
        if (mUser != null) {
            MyBmobInstallation myBmobInstallation = new MyBmobInstallation(this);
            myBmobInstallation.setUser(mUser);
            myBmobInstallation.setInstallationId(MyBmobInstallation.getInstallationId(this));
            myBmobInstallation.save(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mUser = BmobUser.getCurrentUser(mActivity, User.class);
//
//        savaUserWithInsId();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bdlbsUtils.stopLocation();
        if (isRegistBordcast) {
            unregisterReceiver(mBroadcastReceiver);
        }
//        ApplicationConfig.getInstance().getThreadInstance().stop();
    }
}
