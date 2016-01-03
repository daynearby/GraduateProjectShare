package com.young.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.young.share.annotation.InjectView;
import com.young.share.base.CustomActBarActivity;
import com.young.share.config.Contants;
import com.young.share.model.User;
import com.young.share.utils.BDLBSUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Nearby Yang on 2015-10-20.
 */
public class RegisterActivity extends CustomActBarActivity implements View.OnClickListener {

    @InjectView(R.id.et_registrt_email)
    private EditText registEmail;
    @InjectView(R.id.et_register_pwd)
    private EditText registPwd;
    @InjectView(R.id.et_register_config_pwd)
    private EditText registConfigPwd;
    @InjectView(R.id.tv_go_login)
    private TextView gotoLogin;
    @InjectView(R.id.tv_register_btn)
    private TextView registerBtn;

    private BDLBSUtils bdlbsUtils;

    private String province = "广东省";
    private String city = "惠州市";
    private String district;
    private String street;
    private String streetNumber;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void findviewbyid() {
        gotoLogin.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        bdlbsUtils = new BDLBSUtils(this, new locationListener());
        bdlbsUtils.startLocation();
        setBarVisibility(false, false);
        settitle(R.string.regist);
//        saveFile2SDCard();
    }

    @Override
    public void bindData() {

    }

    @Override
    public void handerMessage(Message msg) {
        // 停止定位服务并且回到登陆界面
        gotoLoginAndStopLocationServices();
    }

    @Override
    public void mBack() {
        mBackStartActivity(LoginActivity.class);
    }

    @Override
    public void onClick(View v) {

        String email;
        String pwd;
        String config_pwd;
        User userRegister = new User();

        switch (v.getId()) {
            case R.id.tv_register_btn:

                SVProgressHUD.show(this);

                email = registEmail.getText().toString().trim();
                pwd = registPwd.getText().toString().trim();
                config_pwd = registConfigPwd.getText().toString().trim();

                if (!TextUtils.isEmpty(email)) {

                    if (email.length() > 3) {


                        if (!TextUtils.isEmpty(pwd) || !TextUtils.isEmpty(config_pwd)) {
                            if (pwd.length() > 5) {


                                if (pwd.equals(config_pwd)) {

                                    SVProgressHUD.show(this);
                                    //随机字符串
                                    userRegister.setNickName(StringUtils.getRanDom());
                                    userRegister.setEmail(email);
                                    userRegister.setPassword(pwd);
                                    userRegister.setAddress(province + " " + city + " " + district);
                                    userRegister.setUsername(email.substring(0, email.indexOf("@")));

                                    userRegister.signUp(RegisterActivity.this, new SaveListener() {
                                        @Override
                                        public void onSuccess() {

                                            SVProgressHUD.showSuccessWithStatus(RegisterActivity.this, getString(R.string.register_success));
                                            mHandler.sendEmptyMessageDelayed(101, Contants.ONE_SECOND);

                                        }

                                        @Override
                                        public void onFailure(int i, String s) {

                                            if (i == 202) {
                                                SVProgressHUD.showInfoWithStatus(RegisterActivity.this, getString(R.string.user_had_register), SVProgressHUD.SVProgressHUDMaskType.Gradient);
                                            }
                                            LogUtils.logE(getClass().getName(), "注册失败  code = " + i + " message = " + s);
                                        }
                                    });


                                } else {
                                    registPwd.setText("");
                                    registConfigPwd.setText("");

                                    SVProgressHUD.showInfoWithStatus(this, getString(R.string.pwd_not_equals), SVProgressHUD.SVProgressHUDMaskType.Gradient);

                                }
                            } else {
                                SVProgressHUD.showInfoWithStatus(this, getString(R.string.pwd_lenght_not_enough), SVProgressHUD.SVProgressHUDMaskType.Gradient);

                            }
                        } else {
                            SVProgressHUD.showInfoWithStatus(this, getString(R.string.pwd_not_empty), SVProgressHUD.SVProgressHUDMaskType.Gradient);

                        }
                    } else {
                        SVProgressHUD.showInfoWithStatus(this, getString(R.string.email_eror), SVProgressHUD.SVProgressHUDMaskType.Gradient);
                    }
                } else {
                    SVProgressHUD.showInfoWithStatus(this, getString(R.string.email_not_empty), SVProgressHUD.SVProgressHUDMaskType.Gradient);

                }


                break;

            case R.id.tv_go_login:

                gotoLoginAndStopLocationServices();
                break;
        }
    }


    private void uploadAvatarAndRegisrter(final User user) {

        String filePath = "";
        BTPFileResponse response = BmobProFile.getInstance(this).upload(filePath, new UploadListener() {

            @Override
            public void onSuccess(String fileName, String url, BmobFile file) {
                LogUtils.logI("bmob", "文件上传成功：" + fileName + ",可访问的文件地址：" + file.getUrl());
                // fileName ：文件名（带后缀），这个文件名是唯一的，开发者需要记录下该文件名，方便后续下载或者进行缩略图的处理
                // url        ：文件地址
                // file        :BmobFile文件类型，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
//                注：若上传的是图片，url地址并不能直接在浏览器查看（会出现404错误），需要经过`URL签名`得到真正的可访问的URL地址,当然，`V3.4.1`的版本可直接从'file.getUrl()'中获得可访问的文件地址。

                user.setAvatar(url);


            }

            @Override
            public void onProgress(int progress) {

                LogUtils.logI("bmob", "onProgress :" + progress);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                LogUtils.logI("bmob", "文件上传失败：" + errormsg);
            }
        });
    }

    public void saveFile2SDCard() {
        // 将默认头像放到手机内存
        Bitmap bitmap = null;
        FileOutputStream fos = null;


        String path = "/data/data/com.hzu.zao/files/avatar.png";

        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_default_avatar);


        try {
            fos = openFileOutput("avatar.png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 停止定位，回到登陆界面
     */
    private void gotoLoginAndStopLocationServices() {

        intents.setClass(this, LoginActivity.class);

        bdlbsUtils.stopLocation();


        startActivity(intents);

        this.finish();
    }

    private class locationListener implements BDLBSUtils.LocationInfoListener {

        @Override
        public void LocationInfo(String Province, String City, String District, String Street, String StreetNumber) {

            province = Province;
            city = City;
            district = District;
            street = Street;
            streetNumber = StreetNumber;
        }
    }
}
