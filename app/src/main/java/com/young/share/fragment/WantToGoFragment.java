package com.young.share.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.young.share.R;
import com.young.share.base.BaseFragment;
import com.young.share.config.Contants;
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
 * 点赞区,想去显示的内容
 * Created by Nearby Yang on 2016-03-19.
 */

public class WantToGoFragment extends BaseFragment {

    private List<String> userIdList;
    private List<MyUser> userList;
    private TextView avatarTxt;

    /*加载图片的宽高*/
    private static final int imageWidth = 100;
    private static final int imageHeight = 100;
    private static final int MESSAGE_GET_USER_INFO = 0x01;//获取了用户信息，需要进行图片加载
    private static final int MESSAGE_CONVERT_USER_INFO = 0x02;//将用户的id转化成对应的用户头像图片
    public static final String BUNDLE_USERID_LIST = "bundle_userid_list";

    public WantToGoFragment() {


    }

    @Override
    protected void getDataFromBunlde(Bundle bundle) {

        userIdList = (List<String>) bundle.getSerializable(BUNDLE_USERID_LIST);
        userList = new ArrayList<>();
    }


    /**
     * 更新用户数据
     *
     * @param userIdList
     */
    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }


    @Override
    protected void onSaveState(Bundle outState) {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_want_to_go_like;
    }

    @Override
    public void initData() {
        /*初始化数据*/
//        initFragment();

        if (userIdList != null && userIdList.size() > 0) {
            String userIds = "[";

            for (int i = 0; i < userIdList.size(); i++) {
                userIds = userIds + "\"" + userIdList.get(i) + "\",";
            }

            userIds = userIds.substring(0, userIds.length() - 1) + "]";

//            LogUtils.e(" user id array =  " + userIds);
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
                    LogUtils.d("userList size = " + userList.size());
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
            /**
             * 移除，移除为空 的时候
             */
            avatarTxt.setText("");
        }
    }

    @Override
    public void initView() {
        avatarTxt = $(R.id.tv_want_to_go_avatar);

    }

    @Override
    public void bindData() {
        /*可点击*/
        avatarTxt.setMovementMethod(LinkMovementMethod.getInstance());
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

            ImageLoader.getInstance().loadImage(TextUtils.isEmpty(u.getAvatar()) ?
                            Contants.DEFAULT_AVATAR : NetworkUtils.getRealUrl(context,
                            u.getAvatar()),
                    mImageSize,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);

                            avatar.add(loadedImage);

                            /*当全部的图片都下载完，或者说是加载完*/
                            if (userIdList.size() == avatar.size()) {
                                Message message = new Message();
                                message.what = MESSAGE_CONVERT_USER_INFO;
                                message.obj = StringUtils.idConver2Bitmap(context, userIdList, avatar, textLink);
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
