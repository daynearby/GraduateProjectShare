package com.young.config;

import com.young.share.R;

/**
 * 常量
 * Created by Nearby Yang on 2015-07-02.
 */
public class Contants {
    //欢迎界面
    public static final int GO_HOME = 100;
    public static final int GO_GUIDE = 101;
    public static final int TIME = 2000;
    public static final int ONE_SECOND = 1000;

    public static final String sharePreferenceStr = "WELCOME";

    public static final String SH_ACCOUNT = "account";
    public static final String SH_PWD = "pwd";

    //*************************** Bmob key *****************

    public static final String BMOB_APP_KEY = "52f47fe363ec6e189700adf5b51e58ed";

    //******************************Mob key*******************************
    public static final String SMS_APP_KEY = "b63b43f91a14";
    public static final String SMS_APP_SECRET = "78a2317bbf261f05ebb6c62be8262bb7";


    //******************************本地 文件名 前缀***********************
    public static final String FILE_HEAD = "file://";

    public static final String LAST_ADD_IMG = "drawable://" + R.drawable.icon_addimg;

    //******************************定位 Keys***********************
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String DISTRICT = "district";
    public static final String STREET = "street";
    public static final String STREETNUMBER = "streetNumber";

    //***********************自定 选择 照片请求码*********************
    public static final int REQUEST_IMAGE = 2;

    public static final int IMAGENUMBER = 6;//选择照片的数量

    //****************************广播接收者 标识**************************************

    public static final String BORDCAST_SELECTIMAGES = "bordcase_select_images";
    public static final String BORDCAST_LOCATIONINFO = "bordcast_location_info";

    //广播数据的标志

    public static final String BORDCAST_IMAGEPATH_LIST = "bordcast_imagrpath_list";

}
