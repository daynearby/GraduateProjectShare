package com.young.share.views;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.share.R;
import com.young.share.utils.LogUtils;
import com.young.share.views.base.BaseDialog;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.VerifySMSCodeListener;


/**
 * 验证码的输入框
 * Created by Nearby Yang on 2016-03-29.
 */
public class IdentifyCodeDialog extends BaseDialog implements View.OnClickListener {

    private EditText identifyCodeEt;
    private ImageView identifyStateIm;
    private ImageView dissmissIm;
    private TextView timerTxt;

    private ReSendIdentify reSendIdentifycode;
    private String phoneNumber;//手机号
    private boolean mobilePhoneVerified;
    private InputMethodManager imm;

    public IdentifyCodeDialog(Context context) {
        super(context);
        setCancelable(false);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 设置手机号码
     *
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;

    }

    /**
     * 设置回调
     *
     * @param reSendIdentifycode
     */
    public void setReSendIdentifycode(ReSendIdentify reSendIdentifycode) {
        this.reSendIdentifycode = reSendIdentifycode;
    }

    /**
     * 获取验证之后的结果
     *
     * @return
     */
    public boolean isMobilePhoneVerified() {
        LogUtils.d("dialog mobilePhoneVerified = "+mobilePhoneVerified);
        return mobilePhoneVerified;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_identify_code;
    }

    @Override
    protected void findview() {
        identifyCodeEt = (EditText) view.findViewById(R.id.et_dialog_identify_code_input);
        identifyStateIm = (ImageView) view.findViewById(R.id.im_identify_state);
        timerTxt = (TextView) view.findViewById(R.id.txt_et_dialog_identify_code_time);
        dissmissIm = (ImageView) view.findViewById(R.id.im_dialog_dismiss_);
    }

    @Override
    protected void bindData() {
        /*确认点击*/
        view.findViewById(R.id.txt_identify_confirm).setOnClickListener(this);
        timerTxt.setOnClickListener(this);
        dissmissIm.setOnClickListener(this);
        identifyCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                final String code = identifyCodeEt.getText().toString().trim();
                LogUtils.e("code = " + code);

/*输入验证码的长度不为空,长度6位，数字*/
                if (!TextUtils.isEmpty(code) && code.length() == 6) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            verificaCode(code);
                        }
                    }, 500);
                }

            }

        });


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.txt_et_dialog_identify_code_time://时间到了之后就显示重试，可以点击状态

                if (reSendIdentifycode != null) {
                    reSendIdentifycode.resend();
                }

                break;
            case R.id.txt_identify_confirm://点击确认按钮。关闭dialog
                LogUtils.e(" mobilePhoneVerified = " + mobilePhoneVerified);
                if (mobilePhoneVerified) {
                    dismiss();
                }

                break;

            case R.id.im_dialog_dismiss_:
                dismiss();
                break;

        }
    }

    /**
     * 初始化状态
     */

    public void initSate() {
        identifyCodeEt.getText().clear();
        identifyStateIm.setVisibility(View.GONE);
    }

    /**
     * 获取 时间控件
     *
     * @return
     */
    public TextView getTimerTxt() {
        return timerTxt;
    }

    /**
     * @param identifyCode 验证码
     */
    private void verificaCode(String identifyCode) {

        BmobSMS.verifySmsCode(context, phoneNumber, identifyCode, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException ex) {
                if (ex == null) {//短信验证码已验证成功
                    identifyStateIm.setImageResource(R.drawable.icon_checked);
                    identifyStateIm.setVisibility(View.VISIBLE);
                    mobilePhoneVerified = true;
                    LogUtils.d("短信验证验证通过 ");

                } else {
                    identifyStateIm.setVisibility(View.GONE);
                    identifyCodeEt.setError(Html.fromHtml("<font color='white'>验证码错误</font>"));
                    mobilePhoneVerified = false;
                    LogUtils.e("验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                }
            }
        });
    }


    public interface ReSendIdentify {
        void resend();
    }
}
