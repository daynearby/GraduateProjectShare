package com.young.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.young.base.BasePopupWin;
import com.young.model.User;
import com.young.share.R;
import com.young.utils.DisplayUtils;
import com.young.utils.ImageHandlerUtils;

/**
 * Created by Nearby Yang on 2015-11-19.
 */
public class PopupWinUserInfo extends BasePopupWin {

    private ImageView avatar;
    private TextView nickname;
    private TextView signture;
    private TextView qq;
    private TextView email;
    private TextView hometown;

    private LinearLayout qq_layout;
    private LinearLayout email_layout;
    private LinearLayout hometown_layout;


    public PopupWinUserInfo(Context context, User user) {
        super(context, user);


    }

    @Override
    public int getLayoutId() {
        return R.layout.content_popup_window_user_info;
    }

    @Override
    protected void init() {
        int width = DisplayUtils.getScreenWidthPixels((Activity) context);
        int heiht = DisplayUtils.getScreenHeightPixels((Activity) context);
        setWidth(width);
        setHeight(heiht);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layout_content_popupwin_user_info_main);
        ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
        layoutParams.width = width * 3 / 4;
        layoutParams.height = heiht * 3 / 5;
        linearLayout.setLayoutParams(layoutParams);

        view.findViewById(R.id.layout_content_popupwin_user_info_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    protected void findView() {
        avatar = (ImageView) view.findViewById(R.id.im_content_popupwin_user_info_avatar);
        nickname = (TextView) view.findViewById(R.id.tv_content_popupwin_user_info_nickname);
        signture = (TextView) view.findViewById(R.id.tv_content_popupwin_user_info_signture);
        qq = (TextView) view.findViewById(R.id.tv_content_popupwin_user_info_qq);
        email = (TextView) view.findViewById(R.id.tv_content_popupwin_user_info_email);
        hometown = (TextView) view.findViewById(R.id.tv_content_popupwin_user_info_hometown);

        qq_layout = (LinearLayout) view.findViewById(R.id.layout_content_popupwin_user_info_qq);
        email_layout = (LinearLayout) view.findViewById(R.id.layout_content_popupwin_user_info_email);
        hometown_layout = (LinearLayout) view.findViewById(R.id.layout_content_popupwin_user_info_hometwon);


    }

    @Override
    protected void bindData() {
        ImageHandlerUtils.loadIamge(context, user.getAvatar(), avatar, false);

        nickname.setText(user.getNickName() == null ?
                context.getText(R.string.user_name_defual) : user.getNickName());

        signture.setText(user.getSignture() == null ?
                context.getText(R.string.user_info_hint_enjoy_life) : user.getSignture());

        int genderId = user.isGender() ? R.drawable.icon_male : R.drawable.icon_female;
        nickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, genderId, 0);

        if (!TextUtils.isEmpty(user.getQq())) {
            qq.setText(user.getQq());
            qq_layout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(user.getEmail())) {
            email.setText(user.getEmail());
            email_layout.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(user.getAddress())) {
            hometown.setText(user.getAddress());
            hometown_layout.setVisibility(View.VISIBLE);
        }


    }

    public void background() {

//        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
//        lp.alpha = bgAlpha; //0.0-1.0
//        getWindow().setAttributes(lp);
    }


    @Override
    public void onShow(View v) {
        showAtLocation(v, Gravity.CENTER, 0, 0);
        background();
    }
}
