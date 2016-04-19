package com.young.share.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.young.share.R;
import com.young.share.base.BaseFragment;
import com.young.share.config.Contants;
import com.young.share.config.TextMovementMethod;
import com.young.share.interfaces.AsyncListener;
import com.young.share.model.MyUser;
import com.young.share.model.UserList;
import com.young.share.network.BmobApi;
import com.young.share.utils.LogUtils;
import com.young.share.utils.NetworkUtils;
import com.young.share.utils.StringUtils;
import com.young.share.views.PopupWinUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 去过区 显示的内容
 * Created by Nearby Yang on 2016-03-19.
 */
public class HadGoFragment extends BaseFragment {

    private TextView avatarTxt;
    private List<String> wantUserId;
    private List<MyUser> userList;

    /*加载图片的宽高*/
    private static final int imageWidth = 100;
    private static final int imageHeight = 100;
    private static final int MESSAGE_GET_USER_INFO = 0x01;//获取了用户信息，需要进行图片加载
    private static final int MESSAGE_CONVERT_USER_INFO = 0x02;//将用户的id转化成对应的用户头像图片

    public static final String BUNDLE_USER_ID_LIST = "bundle_user_id_list";

    public HadGoFragment() {
        userList = new ArrayList<>();

    }


    @Override
    protected void onSaveState(Bundle outState) {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {

    }

    @Override
    protected void getDataFromBunlde(Bundle bundle) {

        wantUserId = (List<String>) bundle.getSerializable(BUNDLE_USER_ID_LIST);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_want_to_go_like;
    }

    /**
     * 更新用户id
     *
     * @param wantUserId
     */
    public void setWantUserId(List<String> wantUserId) {
        this.wantUserId = wantUserId;
    }

    @Override
    public void initData() {
        /*初始化数据*/
//        initFragment();
        LogUtils.d("had go context = " + context);
        avatarTxt = $(R.id.tv_want_to_go_avatar);

        if (wantUserId != null && wantUserId.size() > 0) {

            String userIds = "[";

            for (int i = 0; i < wantUserId.size(); i++) {
                userIds = userIds + "\"" + wantUserId.get(i) + "\",";
            }

            userIds = userIds.substring(0, userIds.length() - 1) + "]";

            JSONObject params = new JSONObject();

            try {
                params.put(Contants.PARAMS_USER_OBJECT_IDS, userIds);
            } catch (JSONException e) {
                LogUtils.e("获取用户信息，添加参数失败" + e.toString());
            }

        /*用户信息*/
            BmobApi.AsyncFunction(context, params, BmobApi.GET_USER_AVATAR, UserList.class, new AsyncListener() {
                @Override
                public void onSuccess(Object object) {
                    UserList userLists = (UserList) object;

                    for (MyUser user : userLists.getUserList()) {
                        userList.add(user);
                    }

                /*只要用户存在，发送信息*/
                    if (userList.size() > 0) {
                        mhandler.sendEmptyMessage(MESSAGE_GET_USER_INFO);
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                /*存在错误的话，还不知道呀有怎样的错误，现在不做处理*/
                    LogUtils.e("code = " + code + " msg = " + msg);
                }
            });
        } else {
            avatarTxt.setText("");
        }
    }

    @Override
    public void initView() {
        avatarTxt = $(R.id.tv_want_to_go_avatar);
        LogUtils.e("init view ");
    }

    @Override
    public void bindData() {

  /*可点击*/

//        avatarTxt.setMovementMethod(LinkMovementMethod.getInstance());
        avatarTxt.setMovementMethod(new TextMovementMethod());
    }

    @Override
    public void handler(Message msg) {
        switch (msg.what) {
            case MESSAGE_GET_USER_INFO:
                /*获取用户信息，那么就进行图片加载，显示用户头像*/

                loadAvatar();

                break;

            case MESSAGE_CONVERT_USER_INFO:
                if (avatarTxt == null) {
                    avatarTxt = $(R.id.tv_want_to_go_avatar);
                }
                avatarTxt.setText(msg.obj != null ? (CharSequence) msg.obj : "");
                break;
        }
    }

    /**
     * 根据用户信息进行加载头像
     */
    private void loadAvatar() {
        ImageSize mImageSize = new ImageSize(imageWidth, imageHeight);
        final List<Bitmap> avatar = new ArrayList<>();

        for (MyUser u : userList) {

            ImageLoader.getInstance().loadImage(TextUtils.isEmpty(u.getAvatar()) ? Contants.DEFAULT_AVATAR :
                            NetworkUtils.getRealUrl(context, u.getAvatar()
                            ),
                    mImageSize,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);

                            avatar.add(loadedImage);

                            /*当全部的图片都下载完，或者说是加载完*/
                            if (wantUserId.size() == avatar.size()) {
                                Message message = new Message();
                                message.what = MESSAGE_CONVERT_USER_INFO;
                                message.obj = StringUtils.idConver2Bitmap(context, wantUserId, avatar, textLink);
                                mhandler.sendMessage(message);

                            }

                        }
                    });
        }


    }


    /**
     * 点击用户头像，显示用户信息
     */
    private StringUtils.TextLink textLink = new StringUtils.TextLink() {
        @Override
        public void onclick(String str) {
//            LogUtils.ts(str);
            for (int i = 0; i < userList.size(); i++) {
                LogUtils.d(" click str = " + str + " userList ObjectId() " + userList.get(i).getObjectId());

                if (str.equals(userList.get(i).getObjectId())) {

                    showUserInfo(avatarTxt, userList.get(i));
                    break;
                }

            }


        }
    };

    /**
     * 查看用户资料
     *
     * @param v
     */
    private void showUserInfo(View v, MyUser user) {

        PopupWinUserInfo userInfo = new PopupWinUserInfo(context, user);
        userInfo.onShow(v);
    }
}
