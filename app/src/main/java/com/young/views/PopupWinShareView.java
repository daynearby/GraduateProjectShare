package com.young.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.young.share.R;
import com.young.utils.CommonUtils;

/**
 * Created by Nearby Yang on 2015-10-23.
 */
public class PopupWinShareView extends PopupWindow implements View.OnKeyListener {

    private View view;
    private Context ctx;

    private TextView title_tv;
    private TextView content_tv;
    private TextView shareLocation_tv;
    private Spinner tagSpinner;


    // TODO: 2015-10-23 文本输入框，图片选择，位置，标签

    public PopupWinShareView(Context ctx) {
        this.ctx = ctx;

        view = LayoutInflater.from(ctx).inflate(R.layout.content_popup_window_share, null);
        setContentView(view);

        init();

        findView();
    }

    private void findView() {
        title_tv = (TextView) view.findViewById(R.id.tv_contnent_popupwin_title);

    }

    private void init() {
        setWidth(CommonUtils.getWidth((Activity) ctx) / 5 * 4);
        setHeight(900);
        setFocusable(true);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onShow(View v) {
//        showAsDropDown(v, CommonUtils.getWidth((Activity) ctx) / 4, CommonUtils.getHeight((Activity) ctx) / 5 * 2, Gravity.START);
        showAsDropDown(title_tv, CommonUtils.getWidth((Activity) ctx) / 5, 0, Gravity.CENTER);


    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.dismiss();
            return true;
        }
        return false;
    }

}
