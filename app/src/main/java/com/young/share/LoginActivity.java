package com.young.share;

import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.base.CustomActBarActivity;
import com.young.config.Contants;
import com.young.model.User;
import com.young.utils.LogUtils;
import com.young.utils.SharePreferenceUtils;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Nearby Yang on 2015-10-18.
 */
public class LoginActivity extends CustomActBarActivity implements View.OnClickListener {

    private EditText et_loginEmail;
    private EditText et_loginPwd;
    private TextView tv_forgetPwd;
    private TextView tv_registerNewAccount;
    private TextView tv_login_Btn;

    public static final String ACCOUNT = "account";
    public static final String PWD = "pwd";
    private SharePreferenceUtils sharePreferenceUtils ;
    private  User userLogin;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void findviewbyid() {

        et_loginEmail = $(R.id.et_login_email);
        et_loginPwd = $(R.id.et_login_pwd);
        tv_forgetPwd = $(R.id.tv_login_forgetPwd);
        tv_registerNewAccount = $(R.id.tv_login_register);
        tv_login_Btn = $(R.id.tv_login_btn);

        tv_forgetPwd.setOnClickListener(this);
        tv_registerNewAccount.setOnClickListener(this);
        tv_login_Btn.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        settitle(R.string.personal_center);
        setBarVisibility(false, false);


        sharePreferenceUtils = new SharePreferenceUtils(this);
    }

    @Override
    public void bindData() {
        //自动登陆
//        autoLogin();
    }

    @Override
    public void handerMessage(Message msg) {
        intents.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intents);
        LoginActivity.this.finish();
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.tv_login_forgetPwd:

                intents.setClass(this, FindPwdActivity.class);
                startActivity(intents);

                break;
            case R.id.tv_login_register:
                intents.setClass(this, RegisterActivity.class);
                startActivity(intents);
                break;

            case R.id.tv_login_btn:
                SVProgressHUD.show(this);
                login();

                break;

        }
    }

    /**
     * 登陆账号
     */
    private void login() {

         userLogin = new User();

        final String userName = et_loginEmail.getText().toString().trim();
        final String pwd = et_loginPwd.getText().toString().trim();

        if (userName != null) {
            if (pwd != null) {
                userLogin.setUsername(userName);
                userLogin.setPassword(pwd);
                userLogin.login(this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                        sharePreferenceUtils.setString(Contants.SH_ACCOUNT.hashCode()+"", ACCOUNT, userName);
                        sharePreferenceUtils.setString(Contants.SH_PWD.hashCode() + "", PWD, pwd);

                        SVProgressHUD.showSuccessWithStatus(LoginActivity.this, getString(R.string.login_success));

                        mHandler.sendEmptyMessageDelayed(102, Contants.ONE_SECOND);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        LogUtils.logE(getClass().getName(), "登陆失败  code = " + i + " message = " + s);
                        switch(i){
                            case 101:
                                SVProgressHUD.showInfoWithStatus(LoginActivity.this, getString(R.string.account_pwd_incorrect), SVProgressHUD.SVProgressHUDMaskType.Gradient);
                                break;
                        }


                    }
                });
            } else {
                SVProgressHUD.showInfoWithStatus(this, getString(R.string.pwd_not_empty), SVProgressHUD.SVProgressHUDMaskType.Gradient);

            }
        } else {
            SVProgressHUD.showInfoWithStatus(this, getString(R.string.email_not_empty), SVProgressHUD.SVProgressHUDMaskType.Gradient);

        }

    }

//    private void autoLogin(){
//        userLogin = new User();
//
//        String userName = sharePreferenceUtils.getString(Contants.SH_ACCOUNT.hashCode()+"", ACCOUNT);
//        String pwd = sharePreferenceUtils.getString(Contants.SH_PWD.hashCode()+"", PWD);
//
//        if (userName != null) {
//            userLogin.setUsername(userName);
//            userLogin.setPassword(pwd);
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
//        }
//
//    }


}
