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
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.XmlUtils;
import com.young.share.views.IdentifyCodeDialog;

import java.util.List;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 修改用户资料
 * Created by Nearby Yang on 2015-11-13.
 */
public class EditPersonalInfoActivity extends BaseAppCompatActivity implements View.OnClickListener {

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
    @InjectView(R.id.et_edit_personal_signture)
    private EditText signtureEdt;

    @InjectView(R.id.confirm_pwd_bt)
    private TextView save_tv;
    //    @InjectView(R.id.cancel_pwd)
//    private TextView cancel_tv;
    private IdentifyCodeDialog identifyCodeDialog;
    private CountDownTimer timer;
    private boolean phoneVerific = true;//手机号验证结果
    private boolean phoneFormateVerific = true;//手机号格式验证结果,默认没有进行信息修改
    private boolean nameVerific = true;//用户名长度验证结果
    private int contextMenuType = 0;//contextMenu的类型

    private long allTime = 60;//60秒
    private long intevel = 1000 - 10;//一秒,计时会有10ms误差
    private int homeTownClick = 0;//城镇,两次点击

    private static final int CONTEXT_MENU_GENDER = 0x01;//性别
    private static final int CONTEXT_MENU_AGE = 0x02;//年龄
    private static final int CONTEXT_MENU_HOME_TOWN = 0x03;//家乡


    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_personal_info;
    }

    @Override
    public void initData() {
        initialiToolbar();
        setTitle(R.string.edit_personal_info);


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
/*输入框监听*/
        setTextChangListener();
/*context menu */
        setContextMenuCreatListener();
//
        getUserDatas();
        //初始化计时器
        timeCount();

        /*dialog*/
        dialogSetDismiss();
    }

    /**
     * 输入框输入监听
     */
    private void setTextChangListener() {
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
                        nameVerific = true;
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
                        phoneFormateVerific = false;
                    } else {
                        phoneFormateVerific = true;
                    }
                } else {//手机号码为空
                    phoneFormateVerific = true;
                }
            }
        });
    }

    /**
     * contextmenu
     */
    private void setContextMenuCreatListener() {
//性别
        gender_tv.setOnCreateContextMenuListener(contextMenuListener);
        //年龄
        age_tv.setOnCreateContextMenuListener(contextMenuListener);

        hometown_tv.setOnCreateContextMenuListener(contextMenuListener);
    }

    /**
     * contextMenu
     */
    private View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            int menuViewId = 0;
            switch (view.getId()) {

                case R.id.popupwin_edit_personnal_info_gender://性别选择
                    menuViewId = R.menu.menu_context_gender;
                    contextMenuType = CONTEXT_MENU_GENDER;
                    break;
                case R.id.popupwin_edit_personnal_info_age:
                    menuViewId = R.menu.menu_context_empty;
                    addMenu(contextMenu);
                    contextMenuType = CONTEXT_MENU_AGE;
                    break;

                case R.id.popupwin_edit_personnal_info_hometown:
                    menuViewId = R.menu.menu_context_empty;
                    addCityMenu(contextMenu);
                    contextMenuType = CONTEXT_MENU_HOME_TOWN;
                    break;
            }


            getMenuInflater().inflate(menuViewId, contextMenu);
        }
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (contextMenuType) {
            case CONTEXT_MENU_GENDER:
                gender_tv.setText(item.getTitle());
                break;
            case CONTEXT_MENU_AGE:
                age_tv.setText(item.getTitle());
                break;

            case CONTEXT_MENU_HOME_TOWN:

                if (homeTownClick == 0) {
                    hometown_tv.setText(item.getTitle());
                    homeTownClick++;
                } else if (homeTownClick == 1) {
                    hometown_tv.append(item.getTitle());
                    homeTownClick = 0;
                }
                break;

        }


        return super.onContextItemSelected(item);
    }

    /**
     * 添加城市选择的菜单
     *
     * @param contextMenu
     */
    private void addCityMenu(ContextMenu contextMenu) {

        int cityId = 0x100;

        int base = Menu.FIRST;
        List<String> cityList = XmlUtils.getSelectCities(this);

        for (int i = 0; i < cityList.size(); i++) {
//            contextMenu.add(0, cityId + i, Menu.NONE, cityList.get(i));
            int groundId = 2;
            SubMenu subMenu = contextMenu.addSubMenu(base, cityId + i, Menu.NONE, cityList.get(i));
            for (String area : XmlUtils.getSelectArea(this, i)) {

                subMenu.add(groundId, cityId + i, groundId, area);
                groundId++;
            }
            base++;
        }
    }

    /**
     * 添加菜单内容
     */
    private void addMenu(ContextMenu contextMenu) {

        for (int i = 16; i <= 60; i++) {
            contextMenu.add(0, i, Menu.NONE, String.valueOf(i));
        }

    }


    /**
     * 初始化oolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_edit_personal_info);
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
                LogUtils.ts(" phoneVerific = " + phoneVerific);

                if (phoneVerific) {
                    mobilePhone_et.setEnabled(false);
                    mobilePhone_et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_checked, 0);
                    // 提交用户信息
                    cuser.setMobilePhoneNumber(mobilePhone_et.getText().toString());
//更新用户资料
                    updateUserInfo();
                } else {

                    toast(R.string.toast_phone_verif_failure);
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
        nickname_et.setText(cuser.getNickName());
        qq_et.setText(cuser.getQq() == null ? "" : cuser.getQq());
        gender_tv.setText(cuser.isGender() ? Contants.GENDER_MALE : Contants.GENDER_FEMALE);
        age_tv.setText(cuser.getAge() == 0 ? String.valueOf(20) : String.valueOf(cuser.getAge()));
        email_et.setText(cuser.getEmail() == null ? "" : cuser.getEmail());
        mobilePhone_et.setText(cuser.getMobilePhoneNumber() == null ? "" : cuser.getMobilePhoneNumber());
        signtureEdt.setText(cuser.getSignture() == null ? "" : cuser.getSignture());
        hometown_tv.setText(cuser.getAddress() != null && !TextUtils.isEmpty(cuser.getAddress()) ? getString(R.string.gd_hz) : cuser.getAddress());

    }

    @Override
    public void handerMessage(Message msg) {
        mActivity.finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {

            mActivity.finish();
            return true;
        }


        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_pwd_bt://保存修改
//验证用户名
                if (nameVerific) {//昵称
                    if (phoneFormateVerific) {//手机号格式

                        handlerUIDatas();
                    } else {
                        toast(R.string.toast_phone_number_format_not_correct);

                    }
                } else {
                    toast(R.string.toast_edit_nicke_name);
                }
                break;

            case R.id.popupwin_edit_personnal_info_gender://性别

                openContextMenu(v);
                break;

            case R.id.popupwin_edit_personnal_info_age://年龄
                openContextMenu(v);
                break;

            case R.id.popupwin_edit_personnal_info_hometown://城市
                openContextMenu(v);
                break;
        }
    }


    private void handlerUIDatas() {
        //1手机号，邮箱，昵称，qq，家乡，性别，年龄

        if (!TextUtils.isEmpty(mobilePhone_et.getText().toString())) {

            //未验证,该应用现在使用手机号注册，应该不会有这种情况
            if (TextUtils.isEmpty(cuser.getMobilePhoneNumber())) {

                smsVerfied(mobilePhone_et.getText().toString());

            } else {//已经验证。验证的手机号和当前验证的手机号不相符。则需要进行验证手机号
                        /*修改手机号*/
                if (!mobilePhone_et.getText().toString().equals(cuser.getMobilePhoneNumber())) {
                    smsVerfied(mobilePhone_et.getText().toString());
                } else {
                    updateUserInfo();
                }
            }

        } else {

            cuser.setMobilePhoneNumber("");
            cuser.setMobilePhoneNumberVerified(false);
            updateUserInfo();
        }


    }

    /**
     * 更新用户的信息
     */
    private void updateUserInfo() {

        if (!TextUtils.isEmpty(nickname_et.getText().toString())) {
            cuser.setNickName(nickname_et.getText().toString());
            cuser.setGender(Contants.GENDER_MALE.equals(gender_tv.getText().toString()));
            cuser.setAge(Integer.valueOf(age_tv.getText().toString()));
            cuser.setQq(qq_et.getText().toString());
            cuser.setEmail(email_et.getText().toString());
            cuser.setAddress(hometown_tv.getText().toString());
            cuser.setSignture(signtureEdt.getText().toString().trim());
            SVProgressHUD.showWithStatus(mActivity, getString(R.string.updating));
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
        } else {
            toast(R.string.email_not_empty);
        }
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

    }

}
