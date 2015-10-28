package com.young.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.young.share.R;
import com.young.utils.CommonUtils;
import com.young.utils.LogUtils;

/**
 * 表情面板
 * 从下方弹出，与消失
 * Created by Nearby Yang on 2015-10-28.
 */
public class PopupWinEmotionPanel extends PopupWindow {

    private Context ctx;
    private InputMethodManager imm;
    private View view;
    private static final int EMOTIONPANEL_HEIGHT = 250;

    public PopupWinEmotionPanel(Context ctx) {
        this.ctx = ctx;


        init();
        findView();

    }

    private void findView() {


// TODO: 2015-10-28 表情输入面板
    }

    //初始化popupwindow
    private void init() {
        view = LayoutInflater.from(ctx).inflate(R.layout.content_popup_window_emotion_panel, null);
        setContentView(view);
        setFocusable(true);
        imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        setBackgroundDrawable(new BitmapDrawable());
        setHeight(EMOTIONPANEL_HEIGHT);
        setWidth(CommonUtils.getWidth((Activity) ctx));

    }

    /**
     * 显示表情选择面板
     *
     * @param parentView
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onShow(View parentView) {
//        showAsDropDown(parentView, 0, 0, Gravity.CENTER);

        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        LogUtils.logE("offset y = " + (CommonUtils.getHeight((Activity) ctx) - EMOTIONPANEL_HEIGHT));
//        LogUtils.logE("px 2 dp  " + CommonUtils.px2dp((Activity) ctx,(CommonUtils.getHeight((Activity) ctx) - EMOTIONPANEL_HEIGHT)));
//        showAsDropDown(parentView, 0, (int) (CommonUtils.getHeight((Activity) ctx) - EMOTIONPANEL_HEIGHT - view.getY()));
        showAtLocation(parentView, Gravity.END,100, 100);
//        showAsDropDown(parentView,0,EMOTIONPANEL_HEIGHT,Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);

    }


}
