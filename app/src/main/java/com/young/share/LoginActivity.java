package com.young.share;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.annotation.InjectView;
import com.young.base.CustomActBarActivity;
import com.young.config.Contants;
import com.young.model.MyBmobInstallation;
import com.young.model.User;
import com.young.utils.LogUtils;
import com.young.utils.SharePreferenceUtils;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 登陆
 * Created by Nearby Yang on 2015-10-18.
 */
public class LoginActivity extends CustomActBarActivity implements View.OnClickListener {

    @InjectView(R.id.et_login_email)
    private EditText et_loginEmail;
    @InjectView(R.id.et_login_pwd)
    private EditText et_loginPwd;
    @InjectView(R.id.tv_login_forgetPwd)
    private TextView tv_forgetPwd;
    @InjectView(R.id.tv_login_register)
    private TextView tv_registerNewAccount;
    @InjectView(R.id.tv_login_btn)
    private TextView tv_login_Btn;

    public static final String ACCOUNT = "account";
    public static final String PWD = "pwd";

    private SharePreferenceUtils sharePreferenceUtils;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void findviewbyid() {

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
    public void mBack() {

    }

    /**
     * 保存当前用户在installtion表中
     *
     * @param user
     */
    private void saveCurrentUser2InstalltionTable(User user) {
        MyBmobInstallation installation = new MyBmobInstallation(this);
        installation.setUser(user);
        installation.setInstallationId(BmobInstallation.getInstallationId(this));
        installation.save();
    }

    /**
     * 保存当前用户与installid相关联
     *
     * @param user
     */
    private void savaUserWithInstallId(final User user) {
        BmobQuery<MyBmobInstallation> query = new BmobQuery<>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));

        query.findObjects(this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {

                if (object.size() > 0) {//installtionid存在，进行更新

                    MyBmobInstallation mbi = object.get(0);
                    mbi.setUser(user);
                    mbi.update(mActivity, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            LogUtils.logD("bmob", "设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            LogUtils.logD("bmob", "设备信息更新失败:" + msg);
                        }
                    });

                } else {//installtionid不存在，进行保存

                    saveCurrentUser2InstalltionTable(user);

                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.logD("bmob", "查找设备信息 code = " +code +" msg = "+ msg);

            }
        });
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

        final User userLogin = new User();

        final String userName = et_loginEmail.getText().toString();
        final String pwd = et_loginPwd.getText().toString();

        if (!TextUtils.isEmpty(userName)) {
            if (!TextUtils.isEmpty(pwd)) {

                userLogin.setUsername(userName);
                userLogin.setPassword(pwd);

                userLogin.login(this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                        sharePreferenceUtils.setString(Contants.SH_ACCOUNT.hashCode() + "", ACCOUNT, userName);
                        sharePreferenceUtils.setString(Contants.SH_PWD.hashCode() + "", PWD, pwd);

                        SVProgressHUD.showSuccessWithStatus(LoginActivity.this, getString(R.string.login_success));

                        savaUserWithInstallId(userLogin);

                        mHandler.sendEmptyMessageDelayed(102, Contants.ONE_SECOND);


                    }

                    @Override
                    public void onFailure(int i, String s) {

                        LogUtils.logE(getClass().getName(), "登陆失败  code = " + i + " message = " + s);
                        switch (i) {
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
