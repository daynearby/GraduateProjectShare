package com.young.share;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.annotation.InjectView;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.utils.LogUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 重置密码
 * <p/>
 * Created by Nearby Yang on 2015-11-13.
 */
public class ResetPwdActivity extends ItemActBarActivity implements View.OnClickListener {

    @InjectView(R.id.old_pwd)
    private EditText oldPwd;
    @InjectView(R.id.new_pwd)
    private EditText newPwd;
    @InjectView(R.id.confirm_pwd_et)
    private EditText confPwd;
    @InjectView(R.id.confirm_pwd_bt)
    private TextView confirm;
    @InjectView(R.id.cancel_pwd)
    private TextView cancel;

    private static final int OLD_PASSWORD_INCORRECT = 210;

    @Override
    public int getLayoutId() {
        return R.layout.activity_reset_pwd;
    }

    @Override
    public void initData() {
        super.initData();
        setTvTitle(R.string.revise_pwd);
        setBarItemVisible(true, false);
    }

    @Override
    public void findviewbyid() {
        confirm.setOnClickListener(this);

        cancel.setOnClickListener(this);
    }

    @Override
    public void bindData() {
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                back2super();
            }

            @Override
            public void rightClivk(View v) {

            }
        });
    }

    @Override
    public void handerMessage(Message msg) {
        back2super();
    }


    /**
     * 重置密码
     *
     * @param orinpwd
     * @param newpwd
     */
    private void bmobResetPwd(String orinpwd, String newpwd) {
        BmobUser.updateCurrentUserPassword(this, orinpwd, newpwd, new UpdateListener() {

            @Override
            public void onSuccess() {
                LogUtils.logD("smile", "密码修改成功，可以用新密码进行登录啦");
                SVProgressHUD.showSuccessWithStatus(mActivity, getString(R.string.revise_success));
                mHandler.sendEmptyMessageDelayed(101, Contants.ONE_SECOND);

            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.logD("smile", "密码修改失败：" + msg + "(" + code + ")");
                switch (code) {
                    case OLD_PASSWORD_INCORRECT:
                        SVProgressHUD.showErrorWithStatus(mActivity, getString(R.string.orin_pwd_not_math));
                        break;
//                    case :
//
//                    break;
                }

//                SVProgressHUD.showErrorWithStatus(mActivity, getString(R.string.revise_faile));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.confirm_pwd_bt://修改密码

                if (!TextUtils.isEmpty(oldPwd.getText().toString())) {
                    if (!TextUtils.isEmpty(newPwd.getText().toString()) && newPwd.getText().toString().length() >= Contants.PWD_LENGHT) {


                        if (newPwd.getText().toString().equals(confPwd.getText().toString())) {
                            SVProgressHUD.showWithStatus(this, getString(R.string.revising));
                            bmobResetPwd(oldPwd.getText().toString(), confPwd.getText().toString());

                        } else {//两次输入的密码不相同
                            SVProgressHUD.showInfoWithStatus(this, getString(R.string.pwd_not_equals));
                        }

                    } else {//密码长度不少于6位
                        SVProgressHUD.showInfoWithStatus(this, getString(R.string.new_pwd_lenght_not_enough));
                    }
                } else {//原密码为空
                    SVProgressHUD.showInfoWithStatus(this, getString(R.string.old_pwd_empty));
                }

                break;

            case R.id.cancel_pwd:
                back2super();
                break;
        }
    }

    /**
     * 返回到上一个Activity
     */
    private void back2super() {
        mBackStartActivity(PersonalCenterActivity.class);
        this.finish();
    }

}
