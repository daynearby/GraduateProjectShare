package com.young.share;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.young.adapter.MainPagerAdapter;
import com.young.base.BaseActivity;
import com.young.config.Contants;
import com.young.model.User;
import com.young.utils.BDLBSUtils;
import com.young.utils.SharePreferenceUtils;
import com.young.utils.XmlUtils;
import com.young.views.ArcMenu;
import com.young.views.Dialog4Tips;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;


public class MainActivity extends BaseActivity {

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
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Contants.BMOB_APP_KEY);
    }

    @Override
    public void bindData() {

        settitle(R.string.discover);
        setBarVisibility(true, false);
        setCity(XmlUtils.getSelectCities(this));
        loginFunction();

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
        User currnetUser = BmobUser.getCurrentUser(this, User.class);

//        User userLogin = new User();
//
//        String userName = sharePreferenceUtils.getString(Contants.SH_ACCOUNT.hashCode() + "", LoginActivity.ACCOUNT);
//        String pwd = sharePreferenceUtils.getString(Contants.SH_PWD.hashCode() + "", LoginActivity.PWD);
//
//        if (userName != null) {
//
//            userLogin.setUsername(userName);
//            userLogin.setPassword(pwd);
//
//            userLogin.login(this, new SaveListener() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//
//                }
//            });

        if (currnetUser == null) {//用户未登录

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

        } else {//用户已经登陆过

        }
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


//    private Handler mhandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
//
//
//        }
//
//
//    };
}
