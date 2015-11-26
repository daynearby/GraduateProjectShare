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

    //**********************时间单位******************
    public static final int TIME = 2000;
    public static final int ONE_SECOND = 1000;

    public static final String sharePreferenceStr = "WELCOME";

    public static final String SH_ACCOUNT = "account";
    public static final String SH_PWD = "pwd";
    //*******************字符串长度**************************
    public static final int PWD_LENGHT = 6;
    public static final int NICKNAME_MIN_LENGHT = 2;
    public static final int NICKNAME_MAX_LENGHT = 12;


    //*************************** Bmob key *****************

    public static final String BMOB_APP_KEY = "52f47fe363ec6e189700adf5b51e58ed";
    public static final String BMOB_APP_ACCESS_KEY = "cf5d7486b89a2ad072047b2cb8d4396c";
    public static final String BMOB_APP_REST_KEY = "15ef57525ecb26c77323665e3a2ca59f";
    public static final String REST_APP_KEY = "X-Bmob-Application-Id";
    public static final String REST_APP_REST_KEY = "X-Bmob-REST-API-Key";
    public static final String BMOB_MAIN_URL = "https://api.bmob.cn/1/classes/";


    //******************************Mob key*******************************
    public static final String SMS_APP_KEY = "b63b43f91a14";
    public static final String SMS_APP_SECRET = "78a2317bbf261f05ebb6c62be8262bb7";


    //******************************本地 文件名 前缀***********************
    public static final String FILE_HEAD = "file://";

    public static final String LAST_ADD_IMG = "drawable://" + R.drawable.icon_addimg;
    public static final String DEFAULT_AVATAR = "drawable://" + R.drawable.icon_avatar;

    public static final String IMAGE_PATH_AND_NAME = "/image/cropped.jpeg";
    public static final String DOWNLOAD_PATH = "share/images/";

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
    public static final String BMOB_PUSH_MESSAGES = "Bmob_Push_Messages";
    public static final String BORDCAST_SELECTIMAGES = "bordcast_select_images";
    public static final String BORDCAST_LOCATIONINFO = "bordcast_location_info";
    public static final String BORDCAST_REQUEST_LOCATIONINFO = "bordcast_request_location_info";

    //广播数据的标志

    public static final String BORDCAST_IMAGEPATH_LIST = "bordcast_imagrpath_list";

    //                                  Acache keys
    //******************************** 保存草稿 keys**********************************************

    public static final String DRAFT_CONTENT = "draft_content";
    public static final String DRAFT_TAG = "draft_tag";
    public static final String DRAFT_LOCATION_INFO = "draft_location_info";
    public static final String DRAFT_IMAGES_LIST = "draft_images_list";

    public static final int DARFT_LIVE_TIME = 60 * 60;//一个小时

    //*************************************上传照片类型***********************************************
    public static final int IMAGE_TYPE_SHARE = 100;
    public static final int IMAGE_TYPE_AVATAR = 101;
    public static final int IAMGE_MAX_WIDTH = 1000;
    public static final int IMAGE_MIN_SIZE = 200;
    public static final int MODEL_ID = 1;

    //**************************性别************************************
    public static final String GENDER_MALE = "男";
    public static final String GENDER_FEMALE = "女";

    //**********************网络请求 常量***************************
    public static final int PAGER_NUMBER = 50;//一次请求的数据条数，与云端代码相符
    public static final String SKIP = "skip";//请求参数


    //************************* 类的跳转 标识***************************************
    public static final String CLAZZ_NAME = "clazz_name";//类名
    public static final String CLAZZ_MAINACTIVITY = "clazz_name_mainactivity";//类名
    public static final String CLAZZ_DISCOVER_ACTIVITY = "clazz_discover_activity";//类名

    public static final String CLAZZ_DATA_SHARE_MESSAGE = "data_share_message";//数据
    public static final String CLAZZ_DATA_MODEL = "data_model";//传输数据

    //************************** 数据model 类型******************************
    public static final int DATA_MODEL_SHARE_MESSAGES = 1010;//传输数据




}
