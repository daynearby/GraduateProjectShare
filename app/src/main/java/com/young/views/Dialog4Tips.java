package com.young.views;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.young.share.R;
import com.young.utils.CommonUtils;


/**
 * 上传照片的对话框
 * <p/>
 * Created by feng on 2015/8/26.
 */
public class Dialog4Tips extends Dialog implements View.OnClickListener {

    private Activity activity;
    private TextView btnOk;
    private TextView btnCancel;
    private TextView contentTv;
    private TextView titleTv;
    private LinearLayout linearLayout;
    private View view;
    private Listener listener;

    public Dialog4Tips(Activity activity) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = LayoutInflater.from(activity).inflate(R.layout.dialog4tips, null);

        setContentView(view);
        this.activity = activity;

        findViewById();
        initDialogSize();
    }

    /**
     * 初始化宽度
     */
    private void initDialogSize() {
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.width = Integer.valueOf(CommonUtils.getWidth(activity)) * 4 / 5;
        linearLayout.setLayoutParams(params);
    }


    /**
     * 设置标题 文字 + 颜色
     *
     * @param title
     */
    public void setTitle(String title,int colorId) {
        if (null != title) {
            titleTv.setText(title);

        }

        if (colorId != 0){
            titleTv.setTextColor(colorId);
        }
    }

    /**
     * 设置显示的内容
     *
     * @param content
     */
    public void setContent(String content,int colorId) {
        if (null != content) {
            contentTv.setText(content);
        }

        if (colorId != 0){
            contentTv.setTextColor(colorId);
        }
    }

    private void findViewById() {
        contentTv = (TextView) view.findViewById(R.id.tv_dialog4tips_content);
        btnOk = (TextView) view.findViewById(R.id.tv_dialog4tips_config);
        btnCancel = (TextView) view.findViewById(R.id.tv_dialog4tips_cancel);
        titleTv = (TextView) view.findViewById(R.id.tv_dialog4tips_title);

        //为了窗口显示尺寸
        linearLayout = (LinearLayout) view.findViewById(R.id.dialog4uploadtips_layout);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        if (v == btnOk){
            listener.btnOkListenter();
        }else{
            listener.btnCancelListener();
        }

    }


    /**
     * 监听事件的回调函数
     */
    public interface Listener{

        /**
         * 确定按钮的回调
         */
        public void btnOkListenter();

        /**
         * 取消按钮的回调
         */
        public void btnCancelListener();
    }

    /**
     * 设置两个按钮的监听
     * @param listener
     */
    public void setDialogListener(Listener listener){
        this.listener = listener;
    }



    /**
     * 设置按钮的显示文字
     *
     * @param btnOkStr 显示文字
     */
    public void setBtnOkText(String btnOkStr) {
        btnOk.setText(btnOkStr);
    }

    /**
     * 设置按钮的显示文字颜色
     *
     * @param textColorr 显示文字颜色
     */
    public void setBtnOkTextColor(int textColorr) {
        btnOk.setTextColor(textColorr);
    }


    /**
     * 设置按钮的显示文字
     *
     * @param btnCancelStr  显示文字
     */
    public void setBtnCancelText(String btnCancelStr) {
        btnCancel.setText(btnCancelStr);
    }

    /**
     * 设置按钮的显示文字
     *
     * @param textColorr  显示文字
     */
    public void setBtnCancelTextColor(int textColorr) {
        btnCancel.setTextColor(textColorr);
    }

    /**
     * 设置左边按钮是否可见
     * @param visibilityOrNot
     */
    public void setBtnCancelVisi(int visibilityOrNot) {
        btnCancel.setVisibility(visibilityOrNot);
    }
}

