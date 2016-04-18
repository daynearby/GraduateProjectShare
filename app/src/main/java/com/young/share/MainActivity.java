package com.young.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.young.share.adapter.MainPagerAdapter;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.bmobPush.MessageNotification;
import com.young.share.config.Contants;
import com.young.share.fragment.DiscountFragment;
import com.young.share.fragment.DiscoverFragment;
import com.young.share.fragment.RankFragment;
import com.young.share.model.MyBmobInstallation;
import com.young.share.model.MyUser;
import com.young.share.utils.BDLBSUtils;
import com.young.share.utils.DialogUtils;
import com.young.share.utils.LogUtils;
import com.young.share.views.ArcMenu;
import com.young.share.views.CustomViewPager;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.actionProvider.MainActyProvider;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.push.PushConstants;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

// TODO: 2016-02-27 百度地图的循环调用，添加一个时间延迟，连续定位没用
// TODO: 2016-04-09 当消息发送成功的进行刷新界面 ，使用startavtivityForResult
public class MainActivity extends BaseAppCompatActivity {

    private ArcMenu mArcMenu;
    private BDLBSUtils bdlbsUtils;
    private MainPagerAdapter pagerAdapter;
    private MainActyProvider mainActyProvider;//ActionProvider
    private String currentCity;//当前用户所在城市
    private DiscountFragment discountFragment;
    private DiscoverFragment discoverFragment;

    private int times = 0;
    private static final int callbackTimes = 10;//回调10次
    private boolean isRegistBordcast = false;//是否注册了广播接收者
    private boolean isDiscount = false;//当前是否为商家优惠界面。true -->是

    private static final int MESSAGE_BAIDU_MAP_DELAY = 0x001;//百度地图
    private static final long DELAYED = 6000L;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        //toolbar
        initialiToolbar();
        //初始化Bmob的消息推送
        configBmob();

        bdlbsUtils = BDLBSUtils.builder(this, new locationListener());
        bdlbsUtils.startLocation();
    }


    @Override
    public void findviewbyid() {

        List<Fragment> list = new ArrayList<>();
        CustomViewPager viewPager = $(R.id.vp_main);
        mArcMenu = $(R.id.id_menu);

        discountFragment = new DiscountFragment();
        discoverFragment = new DiscoverFragment();
        RankFragment rankFragment = new RankFragment();

        list.add(discountFragment);
        list.add(discoverFragment);
        list.add(rankFragment);

        mArcMenu.setOnMenuItemClickListener(new onitmeListener());

        pagerAdapter = new MainPagerAdapter(list,
                getSupportFragmentManager(), viewPager,
                new pageChangeListener());

        viewPager.setCurrentItem(1);

    }


    /**
     * 初始化toolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main_activity);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menu_mian_city);
        mainActyProvider = (MainActyProvider) MenuItemCompat.getActionProvider(item);
        mainActyProvider.setOnPopupMenuitemListener(new MainActyProvider.OnPopupMenuitemListener() {
            @Override
            public void clickItem(String city) {
                currentCity = city;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 初始化Bmob的消息推送
     */
    private void configBmob() {
        // 初始化BmobSDK
        Bmob.initialize(this, Contants.BMOB_APP_KEY);
        // 使用推送服务时的初始化操作
        savaUserWithInsId();
        //自动升级
        BmobUpdateAgent.update(this);
        // 启动推送服务
        BmobPush.startWork(this);
        //注册信息接收者
        registerBoradcastReceiver();
    }

    @Override
    public void bindData() {

        setTitle(R.string.discover);

//        setCity(XmlUtils.getSelectCities(this).get(8));

        if (cuser == null) {
            loginFunction();
        }


    }

    @Override
    public void handerMessage(Message msg) {
        switch (msg.what) {

            case MESSAGE_BAIDU_MAP_DELAY://启动百度地图定位
                bdlbsUtils.startLocation();
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(" resultCode = " + resultCode);

        //回调
        if (isDiscount) {
//            discountFragment.getRemoteData();
            discountFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            discoverFragment.onActivityResult(requestCode, resultCode, data);
        }



    }


    private class pageChangeListener implements MainPagerAdapter.OnPageSelected {


        @Override
        public void onselected(int position) {

            switch (position) {

                case 0:
                    setTitle(R.string.discount);
                    mArcMenu.setVisibility(View.VISIBLE);
                    isDiscount = true;

                    break;

                case 1:

                    setTitle(R.string.discover);
                    mArcMenu.setVisibility(View.VISIBLE);
                    isDiscount = false;

                    break;

                case 2:
                    setTitle(R.string.rank);
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


            AlertDialog.Builder alertbBuilder = DialogUtils.exitDialog(mActivity);
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
        public void LocationInfo(double latitude, double longitude,
                                 String Province, String City,
                                 String District, String Street,
                                 String StreetNumber) {


            times++;

            if (Province != null) {

                if (mainActyProvider != null && mainActyProvider.getCityTx() != null) {
                    mainActyProvider.getCityTx().setText(String.format("%s%s", getString(R.string.txt_current_city), City));
                }

                Bundle bundle = new Bundle();
                bundle.putDouble(Contants.LATITUDE, latitude);
                bundle.putDouble(Contants.LONGITUDE, longitude);
                bundle.putString(Contants.PROVINCE, Province);
                bundle.putString(Contants.CITY, City);
                bundle.putString(Contants.DISTRICT, District);
                bundle.putString(Contants.STREET, Street);
                bundle.putString(Contants.STREETNUMBER, StreetNumber);

                intents.setAction(Contants.BORDCAST_LOCATIONINFO);
                intents.putExtra(BUNDLE_BROADCAST, bundle);
                sendBroadcast(intents);

//                LogUtils.i("定位成功 定位信息 发送广播 ");

                bdlbsUtils.stopLocation();
            } else {//没有定位成功
                bdlbsUtils.stopLocation();
                //如果不够十次的话继续定位
                if (times <= callbackTimes) {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_BAIDU_MAP_DELAY, DELAYED);
                }
            }
        }
    }

    /**
     * 消息的广播接收者
     * 处理说到的广播信息
     */
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {

            LogUtils.d("Intent getAction = " + intent.getAction());
//
            switch (intent.getAction()) {
                case Contants.BMOB_PUSH_MESSAGES://"Bmob  信息
                    LogUtils.e("main acitivity bmob ：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
//                    LogUtils.ts("mian activity" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
/*更新界面数据*/
                    if (!TextUtils.isEmpty(intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING))) {

                        initMessagesIcon(true);
                        MessageNotification.showReceiveComment(mActivity, intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
                    }

                    break;
                case Contants.BORDCAST_REQUEST_LOCATIONINFO://使用百度定位
                    //开始 百度定位
                    times = 0;
                    startLocation();
                    break;

                case Contants.BORDCAST_CLEAR_MESSAGES://清空消息
                    initMessagesIcon(false);
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
//            LogUtils.i("view = "+view+" position = "+pos);
            itemIm = (ImageView) view;
            cuser = BmobUser.getCurrentUser(mActivity, MyUser.class);

            switch (pos) {
                case 1://分享信息
                    shareActivity();

                    break;
                case 2://消息中心
                    messageCenter();
                    break;
                case 3://个人中心

                    if (cuser != null) {

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
     * 进入分享信息界面
     */
    private void shareActivity() {
        if (cuser != null) {

            Bundle bundle = new Bundle();
            bundle.putBoolean(Contants.BUNDLE_CURRENT_IS_DISCOUNT, isDiscount);
            //定位信息请求，注册广播接收者
//            registerBoradcastReceiverRequestLocation();
            intents = new Intent(this, ShareMessageActivity.class);
            intents.putExtras(bundle);
            mActivity.startActivityForResult(intents, Contants.REQUSET_SHARE_DISCOVER);


        } else {

            loginFunction();
        }
    }

    /**
     * 进入消息中心
     */
    private void messageCenter() {

        if (cuser != null) {
            //注册广播接收者
            intents = new Intent(this, MessageCenterActivity.class);
            mActivity.startActivityForResult(intents, Contants.REQUSET_MESSAGE_CENTER);

        } else {
//用户还没登陆
            loginFunction();

        }
    }

    /**
     * 将installationId与user绑定
     */
    private void savaUserWithInsId() {
        if (cuser != null) {
            MyBmobInstallation myBmobInstallation = new MyBmobInstallation(this);
            myBmobInstallation.setMyUser(cuser);
            myBmobInstallation.setInstallationId(MyBmobInstallation.getInstallationId(this));
            myBmobInstallation.save(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        cuser = BmobUser.getCurrentUser(mActivity, MyUser.class);
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

    }
}
