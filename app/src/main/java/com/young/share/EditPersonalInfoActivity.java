package com.young.share;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.annotation.InjectView;
import com.young.share.base.ItemActBarActivity;
import com.young.share.config.Contants;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.XmlUtils;
import com.young.share.views.CitySelectPopupWin;
import com.young.share.views.IdentifyCodeDialog;
import com.young.share.views.PopupWinListView;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 修改用户资料
 * Created by Nearby Yang on 2015-11-13.
 */
public class EditPersonalInfoActivity extends ItemActBarActivity implements View.OnClickListener {

    @InjectView(R.id.et_edit_personal_nickname)
    private EditText nickname_et;
    @InjectView(R.id.et_edit_personal_qq)
    private EditText qq_et;
    @InjectView(R.id.et_edit_personal_email)
    private EditText email_et;
    @InjectView(R.id.et_edit_personal_mobile_phone_number)
    private EditText mobilePhone_et;
    @InjectView(R.id.popupwin_edit_personnal_info_gender)
    private TextView gender_tv;
    @InjectView(R.id.popupwin_edit_personnal_info_age)
    private TextView age_tv;
    @InjectView(R.id.popupwin_edit_personnal_info_hometown)
    private TextView hometown_tv;

    @InjectView(R.id.confirm_pwd_bt)
    private TextView save_tv;
    //    @InjectView(R.id.cancel_pwd)
//    private TextView cancel_tv;
    private IdentifyCodeDialog identifyCodeDialog;
    private CountDownTimer timer;
    private PopupWinListView gender_popupList;
    private PopupWinListView age_popupList;
    private CitySelectPopupWin hometown_popupList;
    private boolean phoneVerific = false;//手机号验证结果
    private boolean phoneFormateVerific = false;//手机号格式验证结果
    private boolean nameVerific = false;//用户名长度验证结果


    private long allTime = 60;//60秒
    private long intevel = 1000 - 10;//一秒,计时会有10ms误差
    private String hometown;
    private String gender;
    private String age;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_personal_info;
    }

    @Override
    public void initData() {
        super.initData();

        setTvTitle(R.string.edit_personal_info);
        setBarItemVisible(true, false);
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                back2super();
            }

            @Override
            public void rightClivk(View v) {

            }
        });

        //手机短信验证
//        SMSSDK.initSDK(this, Contants.SMS_APP_KEY, Contants.SMS_APP_SECRET);
    }

    @Override
    public void findviewbyid() {
        save_tv.setOnClickListener(this);
//        cancel_tv.setOnClickListener(this);
        gender_tv.setOnClickListener(this);
        age_tv.setOnClickListener(this);
        hometown_tv.setOnClickListener(this);
        identifyCodeDialog = new IdentifyCodeDialog(this);
    }

    @Override
    public void bindData() {
        gender_popupList = new PopupWinListView(this, XmlUtils.getSelectGender(this), false);
        age_popupList = new PopupWinListView(this, XmlUtils.getSelectAge(this), false);
        hometown_popupList = new CitySelectPopupWin(this, XmlUtils.getSelectCities(this));
        nickname_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = nickname_et.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {

                    if (nickname_et.length() < 4) {
                        nickname_et.setError(Html.fromHtml("<font color='white'>长度不少于4位</font>"));
                        nameVerific = false;
                    } else if (nickname_et.length() > 12) {
                        nickname_et.setError(Html.fromHtml("<font color='white'>长度不多于12位</font>"));
                        nameVerific = false;
                    } else {
                        nameVerific = false;
                    }
                }
            }
        });
/**
 * 验证手机号码
 */
        mobilePhone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phone = mobilePhone_et.getText().toString().trim();

                if (!TextUtils.isEmpty(phone)) {
                    if (!StringUtils.phoneNumberValid(phone)) {
                        mobilePhone_et.setError("<font color='white'>手机号格式错误</font>");
                    } else {
                        phoneFormateVerific = true;
                    }
                } else {//手机号码为空
                    phoneFormateVerific = true;
                }
            }
        });

        gender_popupList.setItemClick(new PopupWinListView.onItemClick() {
            @Override
            public void onClick(View view, String str, int position, long id) {
//                LogUtils.logD("性别 = "+ str +" position = "+position);
                gender_tv.setText(str);
                gender = str;
            }
        });

        age_popupList.setItemClick(new PopupWinListView.onItemClick() {
            @Override
            public void onClick(View view, String str, int position, long id) {
//                LogUtils.logD("年龄 = "+ str +" position = "+position);
                age_tv.setText(str);
                age = str;
            }
        });

        hometown_popupList.setResultListener(new CitySelectPopupWin.ResultListener() {
            @Override
            public void result(String str) {
//                LogUtils.logD("城市 = "+ str);
                hometown_tv.setText(str);
                hometown = str;
            }
        });
//
        getUserDatas();
        //初始化计时器
        timeCount();

        /*dialog*/
        dialogSetDismiss();
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
                mobilePhone_et.setEnabled(phoneVerific = identifyCodeDialog.isMobilePhoneVerified());
                LogUtils.d(" phoneVerific = " + phoneVerific);
                if (phoneVerific) {
                    mobilePhone_et.setTextColor(getResources().getColor(R.color.gray));
////                        // 提交用户信息
                    cuser.setMobilePhoneNumber(mobilePhone_et.getText().toString());
//                    cuser.setEmailVerified(true);
//
                    updateUserInfo();
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
                requestIdentifyCode(mobilePhone_et.getText().toString().trim());
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
//                Toast.makeText(mActivity, String.format("%s秒后重试", String.valueOf((millisUntilFinished - 15) / 1000)), Toast.LENGTH_SHORT).show();
//                new Handler().pos
//                LogUtils.e(String.format("%s秒后重试", String.valueOf((millisUntilFinished - 15) / 1000)));
                identifyCodeDialog.getTimerTxt().setText(String.format("%s秒后重试", String.valueOf((millisUntilFinished - 15) / 1000)));
            }

            @Override
            public void onFinish() {
                identifyCodeDialog.getTimerTxt().setText(R.string.txt_re_send_identify_code);
                identifyCodeDialog.getTimerTxt().setEnabled(true);
//                LogUtils.e("finish");
            }
        };

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
     * 显示用户的信息
     */
    private void getUserDatas() {
        String name = cuser.getNickName();
        String qq = cuser.getQq();
        String gender = cuser.isGender() ? Contants.GENDER_MALE : Contants.GENDER_FEMALE;
        String age = String.valueOf(cuser.getAge());
        String email = cuser.getEmail();
        String mobilePhoneNumber = cuser.getMobilePhoneNumber();
        String hometown = TextUtils.isEmpty(cuser.getAddress()) ? getString(R.string.gd_hz) : cuser.getAddress();

        nickname_et.setText(name);
        qq_et.setText(qq);
        gender_tv.setText(gender);
        age_tv.setText(age);
        email_et.setText(email);
        mobilePhone_et.setText(mobilePhoneNumber);
        hometown_tv.setText(hometown);

    }

    @Override
    public void handerMessage(Message msg) {
        back2super();
    }

    @Override
    public void mBack() {
        back2super();
    }

    private void back2super() {
        mBackStartActivity(PersonalCenterActivity.class);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_pwd_bt://保存修改
//验证用户名
                if (nameVerific && phoneFormateVerific) {

                    handlerUIDatas();
                }
                break;

            case R.id.popupwin_edit_personnal_info_gender://性别
                gender_popupList.onShow(v);
                break;
            case R.id.popupwin_edit_personnal_info_age://年龄
                age_popupList.onShow(v);
                break;
            case R.id.popupwin_edit_personnal_info_hometown://城市
                hometown_popupList.setDatas(XmlUtils.getSelectCities(this));
                hometown_popupList.onShow(v);
                break;
        }
    }


    private void handlerUIDatas() {
        //1手机号，邮箱，昵称，qq，家乡，性别，年龄
        if (!TextUtils.isEmpty(nickname_et.getText().toString())
                && nickname_et.getText().toString().length() >= Contants.NICKNAME_MIN_LENGHT) {

            if (nickname_et.getText().toString().length() <= Contants.NICKNAME_MAX_LENGHT) {
//MyUser myUser = new MyUser();

                if (!TextUtils.isEmpty(mobilePhone_et.getText().toString())) {

                    //未验证
                    if (!cuser.getMobilePhoneNumberVerified()) {

                        smsVerfied(mobilePhone_et.getText().toString());

                    } else {//已经验证。验证的手机号和当前验证的手机号不相符。则需要进行验证手机号
                        /*修改手机号*/
                        if (!mobilePhone_et.getText().toString().equals(cuser.getMobilePhoneNumber())) {
                            smsVerfied(mobilePhone_et.getText().toString());
                        }
                    }


                } else {
                    cuser.setMobilePhoneNumber("");
                    cuser.setMobilePhoneNumberVerified(false);
                    updateUserInfo();
                }


            } else {//昵称长度太长
                SVProgressHUD.showErrorWithStatus(this, String.format(getString(R.string.nickname_lenght_toolong), Contants.NICKNAME_MAX_LENGHT));
            }
        } else {//你曾长度太短
            SVProgressHUD.showErrorWithStatus(this, String.format(getString(R.string.nickname_lenght_short), Contants.NICKNAME_MAX_LENGHT));
        }
    }

    /**
     * 更新用户的信息
     */
    private void updateUserInfo() {

        cuser.setNickName(nickname_et.getText().toString());
        cuser.setGender(Contants.GENDER_MALE.equals(gender_tv.getText().toString()));
        cuser.setAge(Integer.valueOf(age_tv.getText().toString()));
        cuser.setQq(qq_et.getText().toString());
        cuser.setEmail(email_et.getText().toString());
        cuser.setAddress(hometown_tv.getText().toString());

        cuser.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtils.d("更新信息成功");
                mHandler.sendEmptyMessageDelayed(101, Contants.ONE_SECOND);

                SVProgressHUD.showSuccessWithStatus(mActivity, getString(R.string.update_user_info_success));
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.d("更新失败 code = " + i + " message = " + s);
                SVProgressHUD.showErrorWithStatus(mActivity, getString(R.string.update_user_info_faile));
            }
        });
    }


    /**
     * 短信验证
     *
     * @param mobilePhoneNumber
     */
    private void smsVerfied(final String mobilePhoneNumber) {

        BmobSMS.requestSMSCode(this, mobilePhoneNumber, Contants.BMOB_MESSAGE_TEMPLE, new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null) {//验证码发送成功
                    LogUtils.i("短信id：" + smsId);//用于查询本次短信发送详情
                }
            }
        });

        identifyCodeDialog.setPhoneNumber(mobilePhoneNumber);
        identifyCodeDialog.show();
        //打开注册页面
//
//                        SVProgressHUD.showWithStatus(mActivity, getString(R.string.updating));
//
////                        // 提交用户信息
//                        cuser.setMobilePhoneNumber(mobilePhone_et.getText().toString());
//                        cuser.setEmailVerified(true);
//
//                        updateUserInfo();
//
//                    } else {//手机验证与预留手机号不相同
//
//                        SVProgressHUD.showErrorWithStatus(mActivity, getString(R.string.verify_mobilePhone_faile), SVProgressHUD.SVProgressHUDMaskType.GradientCancel);
//
//        });
//
////显示验证窗口
//        registerPage.show(this);
    }

}
