package com.young.share;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.model.MyUser;
import com.young.share.utils.BDLBSUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;
import com.young.share.views.IdentifyCodeDialog;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 用户注册界面
 * <p/>
 * Created by Nearby Yang on 2015-10-20.
 */
public class RegisterActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.et_reg_phone)
    private EditText registPhone;
    @InjectView(R.id.et_register_pwd)
    private EditText registPwd;
    @InjectView(R.id.et_register_config_pwd)
    private EditText registConfigPwd;
    @InjectView(R.id.tv_go_login)
    private TextView gotoLogin;
    @InjectView(R.id.tv_register_btn)
    private TextView registerBtn;
    @InjectView(R.id.txt_reg_get_identify_code)
    private Button identifyCodeTx;

    private IdentifyCodeDialog identifyCodeDialog;
    private BDLBSUtils bdlbsUtils;

    private CountDownTimer timer;
    private boolean phoneNumberVaild;//手机号验证错误
    private boolean pwdVaild;//密码无效
    private boolean comfPwdVaild;//确认密码无效
    private boolean phoneVerific = false;//手机号验证结果

    private long allTime = 60;//60秒
    private long intevel = 1000 - 10;//一秒,计时会有10ms误差

    private String province = "广东省";
    private String city = "惠州市";
    private String district;
    private String street;
    private String streetNumber;

    private static final int MESSAGE_LOCATION = 0x01;//定位

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initData() {
        initialiToolbar();
        setTitle(R.string.regist);
        bdlbsUtils = new BDLBSUtils(this, new locationListener());
        bdlbsUtils.startLocation();
//
//          /*初始化信息服务*/
//        BmobSMS.initialize(this, Contants.BMOB_APP_KEY);
//        saveFile2SDCard();
    }

    @Override
    public void findviewbyid() {
        gotoLogin.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        identifyCodeDialog = new IdentifyCodeDialog(this);
    }

    @Override
    public void bindData() {
        /*输入监听*/
        textChangeListener();
        // 初始化计时器
        timeCount();
/*设置dialog的dismissListener*/
        dialogSetDismiss();
/*注册按钮，初始状态，不可点击*/
        registerBtn.setEnabled(false);
        identifyCodeTx.setOnClickListener(this);

    }

    /**
     * dialog dismiss listener
     * 取得
     */
    private void dialogSetDismiss() {
        identifyCodeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
/**
 * 验证通过，EditText进入不可编辑状态，反之
 */
                phoneVerific = identifyCodeDialog.isMobilePhoneVerified();
                registPhone.setEnabled(!phoneVerific);

                registPhone.setClickable(!phoneVerific);

                LogUtils.d(" phoneVerific = " + phoneVerific);
                if (!phoneVerific) {
                    registPhone.setTextColor(getResources().getColor(R.color.gray));
                }
            }
        });
        /**
         * 重发验证码
         */
        identifyCodeDialog.setReSendIdentifycode(new IdentifyCodeDialog.ReSendIdentify() {
            @Override
            public void resend() {
                /*重发验证码*/
                requestIdentifyCode(registPhone.getText().toString().trim());
                /*重新开始计时*/
                timer.start();
            }
        });

/**
 * 显示窗口
 */
        identifyCodeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                /*防止第二次点开，还出现相同的状态*/
                identifyCodeDialog.initSate();
                timer.start();
            }
        });
    }

    /**
     * 计时器
     */
    private void timeCount() {

        timer = new CountDownTimer(allTime * 1000, intevel) {
            @Override
            public void onTick(long millisUntilFinished) {
//                LogUtils.e(String.format("%s秒后重试", String.valueOf((millisUntilFinished - 15) / 1000)));
                identifyCodeDialog.getTimerTxt().setText(String.format("%s秒后重试", String.valueOf((millisUntilFinished - 15) / 1000)));
            }

            @Override
            public void onFinish() {
                identifyCodeDialog.getTimerTxt().setText(R.string.txt_re_send_identify_code);
                identifyCodeDialog.getTimerTxt().setEnabled(true);
            }
        };

    }


    /**
     * 输入框的内容判断
     * 减少发送时候的逻辑处理逻辑
     */
    private void textChangeListener() {
        /*文字输入监听,在完成之后进行显示状态*/
        registPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                phoneNumberVaild = StringUtils.phoneNumberValid(registPhone.getText().toString().trim());
                if (phoneNumberVaild) {

                    registPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);

                } else {
                    registPhone.setError(Html.fromHtml("<font color='white'>手机号码格式不对</font>"));
                }
                /*更新注册按钮的状态*/
                registerBtn.setEnabled(phoneVerific && phoneNumberVaild && pwdVaild && comfPwdVaild);
            }
        });
/*第一次输入密码*/
        registPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean minlangth = pwdVaild = !TextUtils.isEmpty(registPwd.getText().toString().trim())
                        && registPwd.getText().toString().trim().length() >= 6
                        && registPwd.getText().toString().trim().length() <= 16;
                /*密码长度*/
                if (!pwdVaild) {
                    if (registPwd.getText().toString().trim().length() < 6) {
                        registPwd.setError(Html.fromHtml("<font color='white'>密码长度不少于6位</font>"));
                    } else if (registPwd.getText().toString().trim().length() > 16) {
                        registPwd.setError(Html.fromHtml("<font color='white'>密码长度不大于16位</font>"));
                    }
                } else {

                    registPwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);

                }
                /*两次密码相同否*/
                boolean pwdEqual = TextUtils.isEmpty(registConfigPwd.getText().toString().trim());

                if (!pwdEqual) {
                    pwdEqual = registPwd.getText().toString().trim()
                            .equals(registConfigPwd.getText().toString().trim());
                    if (!pwdEqual) {

                        registPwd.setError(Html.fromHtml(getString(R.string.html_pwd_not_equal)));
                    } else {
                        registPwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);

                    }
                }


                pwdVaild = pwdVaild && pwdEqual;
                /*更新注册按钮的状态*/
                registerBtn.setEnabled(phoneVerific && phoneNumberVaild && pwdVaild && comfPwdVaild);
            }
        });
/*确认密码*/
        registConfigPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                comfPwdVaild = !TextUtils.isEmpty(registConfigPwd.getText().toString().trim())
                        && registConfigPwd.getText().toString().trim().length() >= 6
                        && registConfigPwd.getText().toString().trim().length() <= 16;


                boolean pwdEqual = registPwd.getText().toString().trim()
                        .equals(registConfigPwd.getText().toString().trim());
                if (!comfPwdVaild) {
                    if (registConfigPwd.getText().toString().trim().length() < 6) {
                        registConfigPwd.setError(Html.fromHtml("<font color='white'>密码长度不少于6位</font>"));
                    } else if (registConfigPwd.getText().toString().trim().length() > 16) {
                        registConfigPwd.setError(Html.fromHtml("<font color='white'>密码长度不大于16位</font>"));
                    }

                    if (!pwdEqual) {
                        registConfigPwd.setError(Html.fromHtml("<font color='white'>两次输入密码不相符</font>"));

                    }

                } else {
                    registConfigPwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);
                    pwdVaild = true;
                }

                registerBtn.setEnabled(phoneVerific && phoneNumberVaild && pwdVaild && comfPwdVaild);
            }
        });
    }

    /**
     * 初始化toolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_register);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });

    }

    @Override
    public void handerMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_LOCATION:

                // 停止定位服务并且回到登陆界面
                gotoLoginAndStopLocationServices();
                break;
        }
    }

    @Override
    public void mBack() {
        mActivity.finish();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            mBackStartActivity(LoginActivity.class);
            return true;
        }


        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register_btn:

                SVProgressHUD.show(this);
                /*注册*/
                regiter();

                break;

            case R.id.tv_go_login:

                gotoLoginAndStopLocationServices();
                break;

            case R.id.txt_reg_get_identify_code://获取验证码
//获取验证码
                getIdentifyCodeEvent();

                break;
        }
    }


    /**
     * 获取验证码的逻辑
     */
    private void getIdentifyCodeEvent() {
        if (!phoneVerific) {//手机号没有验证
        /*请求发送验证码*/
            String phone = registPhone.getText().toString().trim();
            if (!TextUtils.isEmpty(phone)) {//手机号不为空
                if (StringUtils.phoneNumberValid(phone)) {//手机号格式验证

                    requestIdentifyCode(phone);
                    identifyCodeDialog.setPhoneNumber(phone);
                /*输入验证码*/
                    identifyCodeDialog.show();
                } else {
                    Toast.makeText(this, R.string.toast_phone_number_format_not_correct, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.toast_phone_number_empty, Toast.LENGTH_SHORT).show();
            }
        } else {
            toast(R.string.toast_had_verif_phone_number);
        }
    }

    /**
     * 请求发送验证码，请求按钮不可点击
     */
    private void requestIdentifyCode(String phone) {

        BmobSMS.requestSMSCode(this, phone, Contants.BMOB_MESSAGE_TEMPLE, new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null) {//验证码发送成功
                    Toast.makeText(mActivity, R.string.toast_send_idnetify_code_success, Toast.LENGTH_SHORT).show();
                    LogUtils.i("bmob 短信id：" + smsId);//用于查询本次短信发送详情

                }
            }
        });


    }

    /**
     * 用户注册
     */
    private void regiter() {

        MyUser myUserRegister = new MyUser();
        String phone = registPhone.getText().toString().trim();
        String pwd = registPwd.getText().toString().trim();

/*有效*/
        if (phoneNumberVaild && pwdVaild) {


            SVProgressHUD.show(this);
            //随机字符串
            myUserRegister.setNickName(StringUtils.getRanDom());
            myUserRegister.setMobilePhoneNumber(phone);
            myUserRegister.setPassword(pwd);
            myUserRegister.setAddress(province + city + district);
            myUserRegister.setUsername(phone);

            myUserRegister.signUp(RegisterActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {

                    SVProgressHUD.showSuccessWithStatus(RegisterActivity.this, getString(R.string.register_success));
                    mHandler.sendEmptyMessageDelayed(MESSAGE_LOCATION, Contants.ONE_SECOND);

                }

                @Override
                public void onFailure(int i, String s) {
/*j*/
                    if (i == 209 || i == 202) {
                        SVProgressHUD.showInfoWithStatus(RegisterActivity.this, getString(R.string.user_had_register));
                    }
                    LogUtils.e(getClass().getName(), "注册失败  code = " + i + " message = " + s);
                }
            });


        } else {

            SVProgressHUD.showInfoWithStatus(this, getString(R.string.pwd_not_equals), SVProgressHUD.SVProgressHUDMaskType.Gradient);

        }


    }

    /**
     * 停止定位，回到登陆界面
     */
    private void gotoLoginAndStopLocationServices() {
        bdlbsUtils.stopLocation();
        finish();
    }

    private class locationListener implements BDLBSUtils.LocationInfoListener {

        @Override
        public void LocationInfo(double latitude, double longitude,
                                 String Province, String City,
                                 String District, String Street,
                                 String StreetNumber) {

            province = Province;
            city = City;
            district = District;
            street = Street;
            streetNumber = StreetNumber;
        }
    }
}
