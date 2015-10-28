package com.young.share;

import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.annotation.InjectView;
import com.young.base.CustomActBarActivity;
import com.young.config.Contants;
import com.young.model.User;
import com.young.myCallback.GotoAsyncFunction;
import com.young.network.ResetApi;
import com.young.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * 找回密码
 * 根据手机号码
 * <p/>
 * Created by Nearby Yang on 2015-10-21.
 */
public class FindPwdActivity extends CustomActBarActivity implements View.OnClickListener {

    private boolean hadVerfi = false;//是否验证
    private String phone;

    @InjectView(R.id.et_find_pwd_user_name)
    private EditText et_user_name;
    @InjectView(R.id.et_find_pwd_pwd)
    private EditText et_new_pwd;
    @InjectView(R.id.tv_find_pwd_btn)
    private TextView findPwdBtn;
    @InjectView(R.id.lly_find_pwd_pwd)
    private LinearLayout findPwdLayout;


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
        super.initData();
        setBarVisibility(false, false);
        settitle(R.string.find_pwd_text);
                SMSSDK.initSDK(this, Contants.SMS_APP_KEY, Contants.SMS_APP_SECRET);
    }

    @Override
    public void bindData() {

    }

    @Override
    public void handerMessage(Message msg) {

        intents.setClass(this, LoginActivity.class);
        startActivity(intents);
        this.finish();
    }

    @Override
    public void onClick(View v) {

        String userName = et_user_name.getText().toString().trim();

        if (!hadVerfi) {

            LogUtils.logE(userName + " lenght = " + userName.length());

            if (userName.length() < 1) {

                SVProgressHUD.showInfoWithStatus(this, getString(R.string.email_not_empty), SVProgressHUD.SVProgressHUDMaskType.Gradient);

            } else {

                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereEqualTo("username", userName);
                query.findObjects(this, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> object) {
                        if (object.size() > 0) {

                            if (object.get(0).getMobilePhoneNumber() != null) {

                                smsVerfied(object.get(0).getMobilePhoneNumber() );

                            }else {

                                SVProgressHUD.showErrorWithStatus(FindPwdActivity.this, getString(R.string.user_non_verif_monilePhome), SVProgressHUD.SVProgressHUDMaskType.Gradient);

                            }


                        } else {

                            SVProgressHUD.showErrorWithStatus(FindPwdActivity.this, getString(R.string.user_non_existent), SVProgressHUD.SVProgressHUDMaskType.Gradient);

                        }

//                        LogUtils.logE(getClass().getName(), "查询用户成功：" + object.size());

                    }

                    @Override
                    public void onError(int code, String msg) {
                        LogUtils.logE(getClass().getName(), "查询用户失败：" + msg);
                    }
                });


            }
        } else {//更新到数据库

            findPwd();

        }
    }

    /**
     * 短信验证
     * @param mobilePhoneNumber
     */
    private void smsVerfied(final String mobilePhoneNumber) {

        //打开注册页面
        RegisterPage registerPage = new RegisterPage();

        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
// 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    phone = (String) phoneMap.get("phone");

                    if (mobilePhoneNumber.equals(phone)){//手机验证与预留手机号不相同

                        // 提交用户信息
                        hadVerfi = true;
                        findPwdLayout.setVisibility(View.VISIBLE);

                    }else {//手机验证与预留手机号不相同

                        SVProgressHUD.showErrorWithStatus(FindPwdActivity.this, getString(R.string.mobilePhone_not_equals), SVProgressHUD.SVProgressHUDMaskType.GradientCancel);

                    }


                }
            }
        });

//显示验证窗口
        registerPage.show(this);
    }

    /**
     * 找回密码
     */
    private void findPwd() {

        JSONObject params = new JSONObject();
        String pwd = et_new_pwd.getText().toString().trim();

        try {
            params.put("phone", phone);

        } catch (JSONException e) {
            LogUtils.logE(e.toString());

        }

        if (pwd != null) {

            try {
                params.put("password", pwd);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            SVProgressHUD.showInfoWithStatus(this, getString(R.string.pwd_not_empty), SVProgressHUD.SVProgressHUDMaskType.Gradient);
        }

//云端代码 更新密码
        ResetApi.AsyncFunction(this, params, ResetApi.aceName.FIND_PWD, new GotoAsyncFunction() {
            @Override
            public void onSuccess(Object object) {

                if (object != null) {

                    if (object.toString().equals("1")) {
                        SVProgressHUD.showSuccessWithStatus(FindPwdActivity.this, getString(R.string.find_pwd_success));
                        mHandler.sendEmptyMessageDelayed(103, Contants.ONE_SECOND);
                    } else {

                    }

                } else {

                }
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });

    }


}
