package com.young.share.views;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.young.share.R;
import com.young.share.config.Contants;
import com.young.share.utils.LogUtils;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;

/**
 * 验证码的输入框
 * Created by Nearby Yang on 2016-03-29.
 */
public class IdentifyCodeDialog extends BaseDialog implements View.OnClickListener {

    private EditText identifyCodeEt;
    private ImageView identifyStateIm;
    private TextView timerTxt;
    private CountDownTimer timer;

    private String phoneNumber;//手机号
    private boolean mobilePhoneVerified;
    private long allTime = 60;//60秒
    private long intevel = 1000 - 10;//一秒,计时会有10ms误差

    public IdentifyCodeDialog(Context context) {
        super(context);
        setCancelable(false);
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
     * 获取验证之后的结果
     *
     * @return
     */
    public boolean isMobilePhoneVerified() {
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

    }

    @Override
    protected void bindData() {
        /*确认点击*/
        view.findViewById(R.id.txt_identify_confirm).setOnClickListener(this);
        timerTxt.setOnClickListener(this);
        identifyCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String code = identifyCodeEt.getText().toString().trim();
/*输入验证码的长度不为空*/
                if (!TextUtils.isEmpty(code)) {

                    verificaCode(code);
                }

            }

        });

// 初始化计时器
        timeCount();

     /*显示*/
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
//                Toast.makeText(context,"onShow",Toast.LENGTH_SHORT).show();
                LogUtils.d("onShow = "+(allTime * 1000));

//                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        timer.start();
//                    }
//                });

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
                Toast.makeText(context, String.format("%s秒后重试", String.valueOf((millisUntilFinished - 15) / 1000)), Toast.LENGTH_SHORT).show();
//                new Handler().pos
                LogUtils.e(String.format("%s秒后重试", String.valueOf((millisUntilFinished - 15) / 1000)));
                timerTxt.setText(String.format("%s秒后重试", String.valueOf((millisUntilFinished - 15) / 1000)));
            }

            @Override
            public void onFinish() {
                timerTxt.setText(R.string.txt_re_send_identify_code);
                timerTxt.setEnabled(true);
                LogUtils.e(String.format("秒后重试"));
            }
        };

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.txt_et_dialog_identify_code_time) {//时间到了之后就显示重试，可以点击状态

            requestIdentifyCode();
        } else {//点击确认按钮。关闭dialog

            dismiss();
        }

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
                    mobilePhoneVerified = true;
                    LogUtils.d("短信验证验证通过");

                } else {
                    identifyStateIm.setImageResource(R.drawable.ic_text_erro);
                    mobilePhoneVerified = false;
                    LogUtils.e("验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                }
            }
        });
    }

    /**
     * 请求发送验证码，请求按钮不可点击
     */
    private void requestIdentifyCode() {


        if (!TextUtils.isEmpty(phoneNumber)) {
            BmobSMS.requestSMSCode(context, phoneNumber, Contants.BMOB_MESSAGE_TEMPLE, new RequestSMSCodeListener() {
                @Override
                public void done(Integer smsId, BmobException ex) {
                    if (ex == null) {//验证码发送成功
                        LogUtils.i("bmob 短信id：" + smsId);//用于查询本次短信发送详情

                    }
                }
            });

        }

    }
}
