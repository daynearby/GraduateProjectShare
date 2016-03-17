package com.young.share.views;

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

import com.young.share.base.BasePopupWin;
import com.young.share.config.Contants;
import com.young.share.model.MyUser;
import com.young.share.R;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.ImageHandlerUtils;

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


    public PopupWinUserInfo(Context context, MyUser myUser) {
        super(context, myUser);


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

        boolean isLoaction;
        String url;

        if (TextUtils.isEmpty(myUser.getAvatar())) {
            url = Contants.DEFAULT_AVATAR;
            isLoaction = true;
        } else {
            url = myUser.getAvatar();
            isLoaction = false;
        }

        ImageHandlerUtils.loadIamge(context, url, avatar, isLoaction);

        nickname.setText(myUser.getNickName() == null ?
                context.getText(R.string.user_name_defual) : myUser.getNickName());

        signture.setText(myUser.getSignture() == null ?
                context.getText(R.string.user_info_hint_enjoy_life) : myUser.getSignture());

        int genderId = myUser.isGender() ? R.drawable.icon_male : R.drawable.icon_female;

        nickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, genderId, 0);

        if (!TextUtils.isEmpty(myUser.getQq())) {
            qq.setText(myUser.getQq());
            qq_layout.setVisibility(View.VISIBLE);
        } else {
            qq_layout.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myUser.getEmail())) {
            email.setText(myUser.getEmail());
            email_layout.setVisibility(View.VISIBLE);
        } else {
            email_layout.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(myUser.getAddress())) {
            hometown.setText(myUser.getAddress());
            hometown_layout.setVisibility(View.VISIBLE);
        } else {
            hometown_layout.setVisibility(View.GONE);
        }


    }


    @Override
    public void onShow(View v) {
        showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}
