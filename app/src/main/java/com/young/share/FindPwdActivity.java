package com.young.share;

import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.model.MyUser;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;

import java.util.List;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

/**
 * 找回密码
 * 根据手机号码,使用bmob的手机号重置
 * <p/>
 * Created by Nearby Yang on 2015-10-21.
 */
public class FindPwdActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private boolean hadVerfi = false;//是否验证
    private String phone;

    @InjectView(R.id.et_find_pwd_user_name)
    private EditText et_user_name;
    @InjectView(R.id.et_find_pwd_pwd)
    private EditText et_new_pwd;
    @InjectView(R.id.tv_find_pwd_btn)
    private TextView findPwdBtn;
    @InjectView(R.id.et_find_identify_code)
    private EditText identifyCodeEdt;

    private boolean identifyCode = false;//验证码
    private String phoneNumber;
    private String code;//验证码
    private String newPwd;//新的密码
    private boolean pwdVaild;//新的密码是否有效

    @Override
    public int getLayoutId() {
        return R.layout.activity_find_pwd;
    }

    @Override
    public void findviewbyid() {
        findPwdBtn.setOnClickListener(this);

    }

    @Override
    public void initData() {
        initializeToolbar();
        setTitle(R.string.find_pwd_text);
        /*初始化短信服务的时候出错*/
        BmobSMS.initialize(this, Contants.BMOB_APP_KEY);
    }

    @Override
    public void bindData() {
        findPwdBtn.setEnabled(false);

        textChangeListener();

    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_find_pwd);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBack();
            }
        });
    }

    /**
     * text 监听
     */
    private void textChangeListener() {

        et_user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                phoneNumber = et_user_name.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    if (StringUtils.phoneNumberValid(phoneNumber)) {
                        /*进行发送验证码*/
                        et_user_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);
                        sendIdentifyCode();
                    } else {
                        et_user_name.setError(Html.fromHtml("<font color='white'>手机号码格式不正确</font>"));
                    }
                }
            }
        });

        /**
         *
         */
        et_new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newPwd = et_new_pwd.getText().toString().trim();

                if (!TextUtils.isEmpty(newPwd)) {
                    if (newPwd.length() < 6) {
                        et_new_pwd.setError(Html.fromHtml("<font color='white'>密码长度不少于6位</font>"));
                        pwdVaild = false;
                    } else {
                        findPwdBtn.setEnabled(true);
                        et_new_pwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);
                        pwdVaild = true;
//                        et_new_pwd.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);
                    }
                }
            }
        });

    }

    @Override
    public void handerMessage(Message msg) {

        intents.setClass(this, LoginActivity.class);
        startActivity(intents);
        this.finish();
    }

    @Override
    public void mBack() {
        mBackStartActivity(LoginActivity.class);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        if (pwdVaild) {
            //提交验证码
            verifiCode();

        }

    }

    /**
     * 发送验证码
     * 先验证手机号是否注册
     */
    private void sendIdentifyCode() {
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("mobilePhoneNumber", phoneNumber);
        query.findObjects(this, new FindListener<MyUser>() {

            @Override
            public void onSuccess(List<MyUser> list) {

                if (list != null && list.size() > 0) {
                    sendMessage();
                } else {
                    toast(R.string.toast_user_not_vail);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.i("根据注册电话查找用户失败 code " + i + " message = " + s);
                toast(R.string.without_network);
            }
        });


    }

    /**
     * 发送验证码
     */
    private void sendMessage() {
        BmobSMS.requestSMSCode(this, phoneNumber, Contants.BMOB_MESSAGE_TEMPLE, new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null) {//验证码发送成功
                    LogUtils.d("短信id：" + smsId);//用于查询本次短信发送详情
                    toast(R.string.toast_send_idnetify_code_success);
                } else {
                    if (10010 == ex.getErrorCode()) {
                        Toast.makeText(mActivity, "该号码请求信息的数量已经到达没上限，明天再试吧", Toast.LENGTH_LONG).show();
                    } else {
                        toast(R.string.toast_send_idnetify_code_failure);
                    }

                    LogUtils.d("重置失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());

                }
            }
        });
    }

    /**
     * 提交验证码还有新密码
     */
    private void verifiCode() {

        BmobUser.resetPasswordBySMSCode(mActivity, identifyCodeEdt.getText().toString().trim(), newPwd, new ResetPasswordByCodeListener() {
            @Override
            public void done(cn.bmob.v3.exception.BmobException ex) {
                if (ex == null) {
                    LogUtils.d("密码重置成功");
                    toast(R.string.find_pwd_success);
                    mBackStartActivity(LoginActivity.class);
                    mActivity.finish();
                } else {
                    LogUtils.d("重置失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                    toast(R.string.find_pwd_failure);
                }
            }

        });

    }


}
