package com.young.share;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.young.base.BaseActivity;
import com.young.model.User;

/**
 * Created by Nearby Yang on 2015-10-18.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_loginEmail;
    private EditText et_loginPwd;
    private TextView tv_forgetPwd;
    private TextView tv_registerNewAccount;
    private TextView tv_login_Btn;


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

        setBarVisibility(false, false);
        settitle(R.string.personal_center);
    }

    @Override
    public void bindData() {

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.tv_login_forgetPwd:
                // TODO: 2015-10-20 忘记密码界面及功能实现

                break;
            case R.id.tv_login_register:
                intents.setClass(this, RegisterActivity.class);
                startActivity(intents);
                break;

            case R.id.tv_login_btn:

                login();

// TODO: 2015-10-20 登陆功能实现
                break;

        }
    }
    /**
     *登陆账号
     */
    private void login() {

        User userLogin = new User();
//        userLogin.setPassword();
//        et_loginEmail.getText().toString().trim()

    }
}
